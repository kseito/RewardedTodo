# Navigation 3への移行 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Implemented |
| 作成日 | 2026-08-18 |
| ブランチ | feature/navigation3-migration |
| 関連Issue/PR | なし |

## 1. 背景・目的

現在のナビゲーションは Jetpack Navigation 2（2.9.8）の文字列ルートベースで実装されている。
Navigation 3 は安定版（1.1.6）に到達しており、Compose専用設計・型安全ルート（`NavKey`）・アプリ側で管理する宣言的バックスタックにより、状態管理の見通しが良くなる。
本移行では既存のナビゲーション挙動を一切変えずに、Navigation 2 → Navigation 3 へ置き換える。あわせて未使用の `navigation-fragment-ktx` / `navigation-ui-ktx` 依存を削除する。

移行手順は Google 公式の移行ガイド（`navigation-3` Androidスキル、2026-08-06版）に準拠する。

## 2. 要件

### 機能要件

- 既存のナビゲーション挙動を完全に維持する（機能追加・UI変更なし）:
  - 起動時にTodoタブが表示される
  - BottomBarでTodo⇄Rewardタブを切り替えられ、選択状態が同期する
  - タブ切り替え時に各タブの状態（スクロール位置・入力途中の状態）が保持される
  - Rewardタブで戻る操作をするとTodoタブへ戻り、BottomBar選択状態も同期する（既存修正 c4bafd16 の挙動維持）
  - TopBarの設定アイコンから設定画面へ遷移し、戻る操作でホームへ復帰する
  - Todoタブ表示中の戻る操作でアプリを終了する
- ルートは文字列定数から `NavKey` を実装した `@Serializable data object` に置き換える

### 非機能要件 / 制約

- Navigation 2 と Navigation 3 の併存はさせず、1ブランチで完全移行する（公式ガイドの前提に従う）
- `hiltViewModel()` のスコープは従来どおり「ナビゲーションエントリ単位」を維持する（`rememberViewModelStoreNavEntryDecorator` を使用）
- 構成変更（画面回転）・プロセスキル後もナビゲーション状態が復元されること（`rememberNavBackStack` / `rememberSerializable` による）
- 前提条件はすべて充足済み: compileSdk 37（要36+）、minSdk 31（要23+）、全destinationがComposable、ディープリンク等の未サポート機能は不使用

## 3. 画面・UX

- 対象画面: HomeActivity（外側ナビゲーション）、HomeScreen（タブナビゲーション）
- UI変更点: なし（内部実装のみの変更。`HomeScreenContent` などの見た目は不変）
- 操作フロー: 変更なし

## 4. ドメインへの影響

なし。domain / application / data 層は一切変更しない。

## 5. レイヤー別の変更方針

ナビゲーションはFeature層とappモジュールに閉じているため、変更はそこと依存定義のみ。

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| 依存定義 | gradle/libs.versions.toml | Nav3依存を追加（`androidx.navigation3:navigation3-runtime` / `navigation3-ui` = 1.1.6、`androidx.lifecycle:lifecycle-viewmodel-navigation3` = 2.11.0）。kotlinx-serializationプラグイン（`org.jetbrains.kotlin.plugin.serialization`、version.ref = kotlin）とランタイムを追加。`navigation-compose` / `navigation-fragment-ktx` / `navigation-ui-ktx`（Nav2）を削除 |
| Feature | feature/todo | `TodoListNavigation.kt`: `TODO_SCREEN` 定数 → `@Serializable data object TodoListRoute : NavKey`。`NavGraphBuilder.todoListScreen()` → `EntryProviderScope<NavKey>.todoListScreen()`。build.gradle.ktsにserializationプラグインと`navigation3-runtime`を追加 |
| Feature | feature/reward | `RewardListNavigation.kt`: 同様に `RewardListRoute : NavKey` 化。未使用の `navigation-fragment-ktx` 依存も削除 |
| Feature | feature/setting | `SettingNavigation.kt`: 同様に `SettingRoute : NavKey` 化 |
| App | app | 下記詳細 |

### appモジュールの変更詳細

公式移行ガイドのパターンに従い、2つの `NavHost` をそれぞれ `NavDisplay` に置き換える。

1. **`NavigationState.kt` / `Navigator.kt`（新規）** — 移行ガイド掲載のトップレベルルート＋タブ別バックスタックの状態ホルダーとイベントハンドラをコピーして配置（パッケージ `jp.kztproject.rewardedtodo.presentation`）。タブ状態の保持・復元とプロセスキル耐性を担う。`toEntries` のdecoratorには `rememberSaveableStateHolderNavEntryDecorator`（状態保持）に加え `rememberViewModelStoreNavEntryDecorator`（ViewModelスコープ）を含める。
2. **`HomeScreen.kt`** — 内側 `NavHost`（Todo/Rewardタブ）を `NavDisplay` に置換。`rememberNavigationState(startRoute = TodoListRoute, topLevelRoutes = setOf(TodoListRoute, RewardListRoute))` + `Navigator` でタブ切替を管理。BottomBar選択状態は `navigationState.topLevelRoute` から導出（`currentBackStackEntryAsState` を置換）。`navigateHome`（popUpTo + saveState/restoreState）は `Navigator.navigate` に置換。
3. **`HomeNavigation.kt`** — `HOME_SCREEN` 定数 → `@Serializable data object HomeRoute : NavKey`。`NavGraphBuilder.homeScreen()` → `EntryProviderScope<NavKey>.homeScreen()`。
4. **`HomeActivity.kt`** — 外側 `NavHost`（Home/Setting）を `rememberNavBackStack(HomeRoute)` + `NavDisplay` に置換。設定遷移は `backStack.add(SettingRoute)`、`onBack` は `removeLastOrNull()`。
5. **build.gradle.kts** — Nav2依存3件を削除し、`navigation3-runtime` / `navigation3-ui` / `lifecycle-viewmodel-navigation3` とserializationプラグインを追加。

### 戻る操作の設計

- 内側 `NavDisplay`: Reward選択時はentriesが `[Todo, Reward]` となり、戻る操作で `Navigator.goBack()` → Todoタブへ復帰。Todo選択時はentriesが `[Todo]` の1件のため内側では戻るを消費せず、外側へ伝播する。
- 外側 `NavDisplay`: Setting表示中はバックスタック `[Home, Setting]` で戻るとHomeへ。Home単独時は戻るを消費せず、Activityが終了する（既存挙動と同一）。

### 参照した既存コード・資料

- 既存実装: `app/.../HomeActivity.kt`, `HomeScreen.kt`, `HomeNavigation.kt`, 各featureの `*Navigation.kt`
- 公式移行ガイド・レシピ: `~/.claude/skills/navigation-3/`（migration-guide.md, multiple-backstacks.md, passingarguments.md）

## 6. 受け入れ条件 (Acceptance Criteria)

- [x] `./gradlew assembleDebug` が成功する
- [x] コードベースに `androidx.navigation.`（Nav2）への参照が残っていない（`androidx.navigation3` のみ）
- [x] 既存ユニットテスト（`./gradlew testDebugUnitTest`）が全モジュールでパスする
- [x] Roborazziスクリーンショット比較で意図しない差分がない（mainのゴールデンと比較し28件すべてunchanged）
- [x] Maestro E2E: `complete-todo-flow` / `single-lottery-flow` パス。`setting-todoist-token-flow` はエミュレータの日本語ロケール起因で "Setting" 表記アサートに失敗（移行起因ではない。遷移自体はadbで確認済み）
- [x] エミュレータで以下を確認:
  - [x] Todo⇄Rewardタブ切替でBottomBar選択状態が同期する
  - [x] Rewardタブで戻る→Todoタブへ復帰しBottomBarも同期する
  - [x] 設定画面への遷移・復帰ができる
  - [x] 画面回転後もタブ選択状態が維持される

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | 新規ロジックなし（`NavigationState`/`Navigator` は公式ガイドのコピー）。既存全テストの回帰確認のみ |
| Roborazzi | `compareRoborazziDebug` で全画面の差分なしを確認（UI変更なしのため差分ゼロが期待値） |
| Maestro E2E | 既存フローを流用: `complete-todo-flow.yaml`（Todoタブ操作）、`single-lottery-flow.yaml`（Rewardタブ操作＝タブ切替を含む）、`setting-todoist-token-flow.yaml`（設定画面遷移）。新規フロー追加なし |

## 8. 未決事項・リスク

- `lifecycle-viewmodel-navigation3` 2.11.0 の追加により lifecycle 系ライブラリのバージョンが引き上がる可能性がある（Compose BOM 2026.06.01 との整合はビルドで検証する）
- `hiltViewModel()` は既存の `androidx.hilt:hilt-navigation-compose` 1.4.0 のまま使用する。`rememberViewModelStoreNavEntryDecorator` が提供する `ViewModelStoreOwner` 経由で動作する想定だが、問題が出た場合は `androidx.hilt:hilt-lifecycle-viewmodel-compose` への切り替えを検討する
