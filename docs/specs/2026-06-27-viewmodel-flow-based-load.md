# ViewModelのデータロードをinit{}からFlowベースへ移行 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Draft |
| 作成日 | 2026-06-27 |
| ブランチ | feature/viewmodel-flow-based-load |
| 関連Issue/PR | #765 |

## 1. 背景・目的

`TodoListViewModel` / `RewardListViewModel` / `SettingViewModel` は `init {}` ブロック内でユースケースを実行してデータをロードしている。この設計には、ナビゲーションバックスタックで画面に戻っても再実行されない、`viewModelScope` の `Dispatchers.Main.immediate` による描画との競合、データ鮮度を保証する仕組みの欠如、テスト時に `init {}` 副作用を通過させる必要がある、といった問題がある（参考: https://funkymuse.dev/posts/properly-load-data/ ）。

`init {}` での副作用ロードをやめ、ユースケースを Flow で公開して `stateIn(SharingStarted.WhileSubscribed(5000))` でホットな `StateFlow` に合成する設計へ統一する。これにより、UI購読開始時にロードが走り、再表示時の鮮度は `WhileSubscribed` の再購読で担保される。

## 2. 要件

### 機能要件
- 3つのViewModelの `init {}` からデータロード処理を排除する。
- データソース（DataStore / Room）のリアクティブFlowを `stateIn(WhileSubscribed(5000))` で購読し、初期ロードを購読駆動にする。
- 既存のUI挙動（一覧表示・ポイント表示・トークン接続状態・プルリフレッシュ・各種結果通知）を維持する。

### 非機能要件 / 制約
- `docs/module-dependency.md` の依存方向ルールを守る（`application` は `domain` のIF経由でdataを利用）。
- ViewModelの公開API（Composeが購読しているFlow名）を可能な限り維持し、UI差分を出さない。
- Roborazziのゴールデン画像に差分を出さない（UI変更なし）。

## 3. 画面・UX

- 対象画面: `TodoListScreen` / `RewardListScreen` / `SettingScreen`
- UI変更点: なし（内部のデータロード機構のみ変更）
- 操作フロー: 変更なし。プルリフレッシュ・トークン保存/削除・抽選後のポイント更新は従来通り動作する。

## 4. ドメインへの影響

`docs/domain-model.md` 参照。ドメインモデル・ビジネスルールの変更はない。データ取得経路を「one-shot suspend」から「リアクティブFlow」に切り替えるためのリポジトリIF追加のみ。

- 関係するエンティティ / Value Object: `Todo`, `ApiToken`, `NumberOfTicket`
- 新規追加・変更するルール: なし

## 5. レイヤー別の変更方針

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Domain | domain/todo | `IApiTokenRepository` に `getTokenAsFlow(): Flow<ApiToken?>` を追加 |
| Application | application/todo | `GetApiTokenUseCase` に `executeAsFlow(): Flow<ApiToken?>` を追加、`GetApiTokenInteractor` で実装 |
| Data | data/todo | `ApiTokenRepository.getTokenAsFlow()` を DataStore の `data` Flow をmapして実装（`.first()` を使わない） |
| Feature | feature/todo | `TodoListViewModel`: `init{}` 廃止。`hasAuthToken` をトークンFlowから導出。ネットワーク同期(`fetchTodoListUseCase`)を `refreshTrigger`(`MutableSharedFlow`)＋`onStart`で購読駆動の副作用に。`refreshTodoList()` はトリガーへemit |
| Feature | feature/reward | `RewardListViewModel`: `init{ loadPoint() }` 廃止。`rewardPoint` を `getPointUseCase` のFlowから `stateIn(WhileSubscribed)`。抽選後の明示 `loadPoint()` 呼び出しを削除（DataStore Flowが自動emit）。`loadPoint()` メソッド削除 |
| Feature | feature/setting | `SettingViewModel`: `init{}` 廃止。`hasAccessToken`/`tokenUiState` をトークンFlowと編集用 `MutableStateFlow` の `combine` で合成。保存/削除後の明示 `loadAccessToken()` 呼び出しを削除。`SettingScreen` の `loadAccessToken()` 呼び出しと `loadAccessToken()`/`loadCurrentToken()` を削除 |

### 設計詳細

**TodoListViewModel**
```kotlin
val todoList: StateFlow<List<Todo>> = refreshTrigger
    .onStart { emit(Unit) }                 // 購読開始で初回同期
    .flatMapLatest {
        flow {
            _isRefreshing.update { true }
            runCatching {
                if (getApiTokenUseCase.execute() != null) fetchTodoListUseCase.execute()
            }.onFailure { e -> Timber.e(e); _result.update { Result.failure(e) } }
            _isRefreshing.update { false }
            emitAll(getTodoListUseCase.execute())   // DBを継続観測
        }
    }
    .catch { ... }
    .stateIn(viewModelScope, WhileSubscribed(5000), emptyList())

val hasAuthToken: StateFlow<Boolean> =
    getApiTokenUseCase.executeAsFlow().map { it != null }
        .stateIn(viewModelScope, WhileSubscribed(5000), false)
```
購読開始時とリフレッシュ時にネットワーク同期し、その後DBを継続観測する。再表示時は `WhileSubscribed(5000)` の再購読で再同期される。

**RewardListViewModel**
```kotlin
val rewardPoint: StateFlow<Int> =
    flow { emitAll(getPointUseCase.execute()) }
        .map { it.value }
        .catch { mutableResult.value = Result.failure(it) }
        .stateIn(viewModelScope, WhileSubscribed(5000), 0)
```
抽選はチケットDataStoreを更新し、Flowが自動emitするため明示再ロードは不要。

**SettingViewModel**
```kotlin
private val editState = MutableStateFlow(TokenEditState())  // tokenInput/isLoading/validationError

val hasAccessToken: StateFlow<Boolean> =
    getApiTokenUseCase.executeAsFlow().map { it != null }
        .stateIn(viewModelScope, WhileSubscribed(5000), false)

val tokenUiState: StateFlow<TokenSettingsUiState> =
    combine(getApiTokenUseCase.executeAsFlow(), editState) { token, edit ->
        TokenSettingsUiState(
            tokenInput = edit.tokenInput,
            hasToken = token != null,
            isConnected = token != null,
            isLoading = edit.isLoading,
            validationError = edit.validationError,
        )
    }.stateIn(viewModelScope, WhileSubscribed(5000), TokenSettingsUiState())
```
保存/削除はDataStoreを更新するだけで `hasToken`/`isConnected` がFlow経由で自動更新される。`SettingScreen` の `todoistAuthFinished` 時の明示ロードも不要になる。

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] 3つのViewModelの `init {}` ブロックが削除されている（データロードの副作用がない）。
- [ ] `TodoListViewModel`/`SettingViewModel` がトークンを `getApiTokenUseCase.executeAsFlow()` から購読している。
- [ ] `RewardListViewModel.rewardPoint` が `getPointUseCase` のFlowから `stateIn(WhileSubscribed)` で合成され、抽選後に明示 `loadPoint()` を呼ばずにポイントが更新される。
- [ ] アプリ起動時にTodo一覧・報酬一覧・ポイント・トークン接続状態が従来通り表示される。
- [ ] プルリフレッシュ（`refreshTodoList()`）でネットワーク同期が走る。
- [ ] トークン保存/削除後に接続状態UIが更新される。
- [ ] 全モジュールのユニットテストがパスする。
- [ ] Roborazziに意図しない差分が出ない。

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `TodoListViewModelTest` / `RewardListViewModelTest` / `SettingViewModelTest` を、`init{}`副作用前提から「Flowを `backgroundScope` で購読して検証」する形へ更新。`GetApiTokenInteractor` の `executeAsFlow` テストを追加 |
| Roborazzi | 対象Composableに変更なし。`compareRoborazziDebug` で差分ゼロを確認 |
| Maestro E2E | 既存フロー（`single-lottery-flow.yaml` 等）が引き続きパスすることを確認。新規フローは追加しない |

`WhileSubscribed` のStateFlowは購読者がいないと上流を収集しないため、テストでは `backgroundScope.launch { vm.xxx.collect {} }` で購読を張ってから値を検証する。

## 8. 未決事項・リスク

- `SettingScreen` の `todoistAuthFinished` 引数による明示リフレッシュを削除する。OAuth完了時のトークンが同一DataStoreに保存される前提（`SaveApiTokenUseCase` 経由）であれば、Flowが自動反映するため問題ない。OAuth保存経路が別系統の場合は要再検討。
- `hasAuthToken` は現状UIで未消費だが、内部整合と将来利用のため公開Flowとして残す。
