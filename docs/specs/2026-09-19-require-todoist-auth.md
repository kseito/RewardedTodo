# Todoist認証の必須化 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Draft |
| 作成日 | 2026-09-19 |
| ブランチ | feature/require-todoist-auth |
| 関連Issue/PR | Issue: #929 / 前提PR: #927 (E2E用のモックサーバーを追加) |

## 1. 背景・目的

本アプリはTodoistのタスクを取り込み、完了でチケットを得て報酬を抽選する。したがってTodoist連携が前提の設計だが、現状は未連携でもローカルのみで動く状態が残っている。これは本来の使い方ではなく、ローカル専用の経路を維持するためだけにフォールバック実装が3箇所に分岐している。

Todoist認証を必須にし、未連携では何も操作できないようにする。あわせて未連携フォールバックを削除し、「連携済みであること」をアプリの不変条件にする。

## 2. 要件

### 機能要件

- 初回起動時、Todoistと未連携ならTodoist認証画面を表示する。認証が通るまで他の機能には到達できない
- 認証が通るとホーム画面（Todo / Reward）へ遷移し、以降の起動では認証画面を経由しない
- 設定画面からログアウトできる。ログアウトすると認証画面へ戻る
- 未認証状態から認証状態へ遷移したとき、前のアカウントに紐づくローカルデータを削除する
- トークンが失効してリフレッシュにも失敗した場合、認証画面へは戻さずエラーを表示する

### 非機能要件 / 制約

- リリースビルドに認証をスキップする導線を置かない
- ローカルフォールバック（未連携時にローカルでチケットを管理する仕組み）を削除する。`docs/specs/2026-05-30-local-ticket-fallback.md` は無効になる
- E2Eはモックサーバー（`test:mockserver`, PR #927）経由で認証を通す。CIのエミュレータにはAuth Tab要件のChrome 137+が存在しないため、実ブラウザによる認可はE2Eの対象外とする

## 3. 画面・UX

### 新規: 認証画面（`AuthScreen`）

- トップバー・ボトムバーなしの全画面
- 構成要素はアプリ名、「Todoistと連携して始める」ボタン、エラー表示のみ
- スキップ導線は置かない
- エラー文言は既存の `TodoistAuthError` をそのまま使う

### 変更: 設定画面（`SettingScreen`）

認証必須化により「未連携のまま設定画面に到達する」経路が消えるため、接続系のUIをすべて削除する。

- 削除: 接続ボタン、`ConnectionStatusCard` の未接続表示、接続時のエラー表示
- 残す: ログアウトボタン。押下時に確認ダイアログを表示する
- 文言を「連携解除」から「ログアウト」に変更する

### 変更: Todo一覧画面

- トークン失効時にSnackbarを表示する。文言は「Todoistとの連携が切れました。設定からログアウトして再連携してください」
- Snackbarに「設定を開く」アクションを付ける

### 操作フロー

1. アプリ起動 → クレデンシャルの有無を判定するまでスプラッシュを保持
2. 未連携なら認証画面、連携済みならホーム画面
3. 認証画面で「Todoistと連携して始める」→ Auth Tab → 認可 → トークン交換 → ローカルデータ削除 → ホーム画面
4. 設定 → ログアウト → 確認ダイアログ → 認証画面

## 4. ドメインへの影響

- 関係するエンティティ / Value Object: `TodoistCredential`, `ApiToken`, `RefreshToken`, `TokenError`
- 新規追加・変更するルール:
  - **Todoist連携済みであることがアプリ利用の前提**になる。未連携状態で到達できる機能は存在しない
  - チケットはRewardサーバーのみが管理する。ローカルのチケット残数という概念を廃止する
  - 未認証から認証への遷移時、前のアカウントに紐づくローカルデータ（Todo・チケット残数・ユーザーIDキャッシュ）を破棄する。Reward一覧はTodoistアカウントと無関係なため残す

### 新規例外

`TodoistUnauthorizedException : IOException` を `domain/todo` の exception パッケージに追加する。OkHttpのInterceptor内から投げるため `IOException` を継承する。

## 5. レイヤー別の変更方針

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Domain | domain/todo | `TodoistUnauthorizedException` 追加。`ITodoRepository.deleteAll()` 追加 |
| Domain | domain/reward | `IAccountCacheRepository` 追加（チケット残数とユーザーIDキャッシュの削除） |
| Application | application/todo | `ClearLocalDataUseCase` / `ClearLocalDataInteractor` 新設。`CompleteTodoistAuthInteractor` がクレデンシャル保存直後に呼ぶ |
| Data | data/todo | `TodoRepository.sync()` の未連携ガードを削除。`deleteAll()` 実装 |
| Data | data/ticket | `LocalTicketRepository` 削除。`TicketRepository` の委譲分岐を削除。`IAccountCacheRepository` 実装 |
| Feature | feature/auth（新設） | `AuthRoute` / `AuthScreen` / `AuthViewModel`。`feature:setting` から `TodoistAuthTabLauncher` / `TodoistAuthTabResult` / `TodoistAuthError` を移設 |
| Feature | feature/setting | 接続系を削除。`SettingViewModel` は `DisconnectTodoistUseCase` のみ。ログアウト確認ダイアログを追加 |
| Feature | feature/todo | 失効時のSnackbarと「設定を開く」アクション |
| App | app | `HomeActivity` で開始ルートを切り替え。SplashScreen導入。`MockTodoistAuthTabLauncher`（debugのみ） |
| DI | app/di | `ITicketRepository` を `NetworkTicketRepository` に直接バインド。`ClearLocalDataUseCase` / `IAccountCacheRepository` のバインド追加 |

### ゲートの実装方針

- `HomeActivity` の backStack 開始ルートをクレデンシャルの有無で切り替える。Activityは増やさない
- Activityスコープの ViewModel が `GetTodoistCredentialUseCase.execute()` を起動時に1回だけ呼ぶ
- `androidx.core.splashscreen` を導入し、判定完了まで `setKeepOnScreenCondition` で保持する
- プロセス再生成時は `rememberNavBackStack` の復元を優先する
- 認証成功・ログアウトは明示的なコールバックで通知し、`backStack.clear()` してから積み直す（Flowの継続観測はしない）

### ローカルデータ削除の方針

| 対象 | 保存先 | 扱い |
|------|--------|------|
| Todo | Room (`TodoEntity`) | 削除 |
| チケット残数 (`number_of_ticket`) | DataStore | 削除（フォールバック廃止により死にキーになるため） |
| `REWARD_USER_ID` | DataStore | 削除 |
| Reward一覧 | Room | **残す** |

- 発火条件は「未認証 → 認証への遷移のたび」。ログアウト後の再認証でも削除される。判定フラグは持たない
- 削除に失敗しても認証は成功扱いとし、Timberにログを残す

### 失効時の方針

- クレデンシャルは破棄しない。認証画面へも戻さない
- `GetValidAccessTokenUseCase` が null を返したら Interceptor が `TodoistUnauthorizedException` を投げる
- 対象はTodoist APIのみ。Rewardサーバー側は既存の `withRetryOn401` のままとする

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] 未連携の状態でアプリを起動すると認証画面が表示され、Todo / Reward / 設定のいずれにも到達できない
- [ ] 認証画面で連携を完了するとホーム画面へ遷移し、Todo一覧がTodoistの内容で同期される
- [ ] 連携済みの状態でアプリを再起動すると認証画面を経由せずホーム画面が表示される
- [ ] 起動直後にホーム画面が一瞬見えてから認証画面へ差し替わる、といったちらつきが起きない
- [ ] 設定画面にはログアウトのみが表示され、接続ボタンと未接続表示は存在しない
- [ ] ログアウトすると確認ダイアログが出て、確定すると認証画面へ戻る
- [ ] ログアウト後に別アカウントで認証すると、前のアカウントのTodoとチケット残数が残っていない
- [ ] ログアウト後に再認証してもReward一覧は残っている
- [ ] 未連携状態でチケットを取得・消費する経路がコード上に存在しない（`LocalTicketRepository` が削除されている）
- [ ] トークンが失効した状態でTodo一覧を更新すると、認証画面へ戻らずSnackbarが表示され、「設定を開く」で設定画面へ遷移できる

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | 新規: `AuthViewModel`（認可URL発行 / Auth Tab結果の各分岐 / エラーマッピング）、`ClearLocalDataInteractor`（3種のデータが消えること、失敗しても例外を投げないこと）<br>更新: `CompleteTodoistAuthInteractorTest`（削除が呼ばれること）、`SettingViewModelTest`（ログアウトのみに縮小）、`TodoListViewModelTest`（失効時に `result` へ例外が載ること）<br>削除: `TicketRepositoryTest`, `LocalTicketRepositoryTest` |
| Roborazzi | `AuthScreen` に `@Preview` を3つ追加（初期 / ローディング / エラー）。`SettingScreen` の未接続系Preview 2つを削除。`detekt-rules` の `NoPreviewNameRule` に従い `name` は付けない |
| Maestro E2E | 新規: 認証ゲートが表示されることを検証するフロー1本<br>更新: 既存13フローの冒頭にモックサーバー経由の認証プロローグを追加<br>`maestro-e2e.yml` に `-PuseMockServer=true` でのビルドと `:test:mockserver:run` の起動を追加 |

アサーションはKotestのmatcher（`shouldBe` 等）に統一する。

### E2Eで認証を通す方法

CIのエミュレータを実測した結果、Auth Tabの要件（Chrome 137+）を満たす環境が存在しないことが確認できた。

| イメージ | Chrome | Custom Tabsサービス |
|---------|--------|-------------------|
| `target: default`（現行CI） | 未インストール | 0件 |
| `target: google_apis` | 113.0.5672.136 | 1件 |

このため、debugソースセットに `MockTodoistAuthTabLauncher` を置き、`TodoistAuthTabLauncher` の実装を差し替える。認可URLから `state` を取り出して `https://kseito.github.io/rewardedtodo/oauth/callback?code=dummy&state=<state>` を合成し、ブラウザを介さずに `Succeeded` を返す。トークン交換以降はモックサーバーが応答するため、`CompleteTodoistAuthInteractor` からゲート遷移、各機能までの経路はE2Eで検証できる。

Auth Tabのブラウザ往復のみ手動確認の対象として残る。

## 8. 未決事項・リスク

- `IAccountCacheRepository` の配置を `domain/reward` と想定しているが、`docs/module-dependency.md` の依存方向と突き合わせて実装時に調整する可能性がある
- `complete-todo-flow` は「Todo完了でチケットを獲得する」を検証しているが、ローカル作成のTodoは `todoistId` を持たずTodoist APIを呼ばないため、モックサーバーがポイント加算を検知できない。管理API（`POST /__admin/points`）で事前条件を組み立てる必要がある
- ADRは作成しない。未連携フォールバックを持たない判断の根拠はPR説明文に残す

## 9. ドキュメント更新

- 新規: 本仕様書
- 削除: `docs/specs/2026-05-30-local-ticket-fallback.md`
- 更新: `docs/module-dependency.md`（`feature:auth` と `test:mockserver` の追加）、`docs/domain-model.md`（連携必須化とチケット管理の変更）、`maestro-tests/README.md`（モックサーバー前提の実行手順）
