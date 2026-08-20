# 報酬一覧プルリフレッシュ 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Implemented |
| 作成日 | 2026-08-19 |
| ブランチ | feature/reward-list-pull-to-refresh |
| 関連Issue/PR | なし |

## 1. 背景・目的

チケット（ポイント）の加算はサーバーサイドの Todoist Webhook が行うため、Todo完了後にアプリを開いたままだと画面上のチケット枚数が古いままになる。現状ユーザーが明示的に最新化する手段がなく、抽選を実行するまで残高が更新されない。報酬一覧画面（RewardListScreen）にプルリフレッシュを追加し、ユーザーが任意のタイミングでチケット残高を最新化できるようにする。

## 2. 要件

### 機能要件

- RewardListScreen の報酬リスト領域を下方向にスワイプするとリフレッシュが実行される
- リフレッシュ中は Material3 標準のインジケータが表示され、完了すると消える
- リフレッシュ時にチケット枚数（`rewardPoint`）がリポジトリから再取得され、画面のチケット表示が更新される
- リフレッシュ中に再度プルしても多重リフレッシュは実行されない

### 非機能要件 / 制約

- 報酬リスト自体は Room の Flow（`GetRewardsUseCase.executeAsFlow()`）を購読しており常に最新が流れてくるため、明示的な再取得処理は追加しない（プルリフレッシュの再取得対象はチケットのみ）
- チケット再取得は既存の `pointRefreshTrigger`（抽選後の残高更新に使用中）を再利用し、取得経路を増やさない
- 取得失敗時は既存のエラーハンドリング（`result` 経由の Snackbar 表示）に乗せ、インジケータは必ず解除する
- ネットワークモード（`NetworkTicketRepository`）・ローカルモードの両方で動作すること

## 3. 画面・UX

- 対象画面: RewardListScreen（`feature/reward`）
- UI変更点:
  - 報酬リスト（`RewardList` の `LazyColumn`）を Material3 の `PullToRefreshBox` でラップする
  - リスト領域全体でプル操作を受け付けるため、`LazyColumn` に `fillMaxSize` を適用する（`RewardList` に `modifier` 引数を追加し、既存Previewの見た目は変えない）
- 操作フロー:
  1. ユーザーが報酬リストを下方向にプルする
  2. インジケータが表示され、チケット残高の再取得が始まる
  3. 取得完了で最新のチケット枚数が「N tickets」表示に反映され、インジケータが消える
  4. 取得失敗時は Snackbar でエラーを表示し、インジケータが消える

## 4. ドメインへの影響

- 関係するエンティティ / Value Object: `NumberOfTicket`（チケット残高）、`Reward`（一覧表示のみ、変更なし）
- 新規追加・変更するルール: なし（既存の取得ロジックの再実行のみ。ドメイン層・ビジネスルールは変更しない）

## 5. レイヤー別の変更方針

チケット再取得は既存の `GetPointUseCase` → `ITicketRepository.getNumberOfTicket()` の経路をそのまま使うため、Domain / Application / Data / DI 層に変更はない。

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Feature | feature/reward | `RewardListViewModel`: `isRefreshing: StateFlow<Boolean>` と `refresh()` を追加。`refresh()` は `pointRefreshTrigger` を発火し、`rewardPoint` パイプラインの値 emit / エラー catch で `isRefreshing` を解除する |
| Feature | feature/reward | `RewardListScreen`: `PullToRefreshBox` で `RewardList` をラップし、`isRefreshing` と `refresh()` を接続する |

参照すべき既存ファイル:

- `feature/reward/src/main/java/.../feature/reward/list/RewardListViewModel.kt` — `pointRefreshTrigger` と `rewardPoint` パイプライン（抽選後の残高更新で同じ仕組みを使用中）
- `feature/reward/src/main/java/.../feature/reward/list/RewardListScreen.kt` — 対象画面
- `feature/reward/src/test/java/.../feature/reward/RewardListViewModelTest.kt` — `rewardPoint refreshes after single lottery` テストが再取得検証の参考になる

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] 報酬リストを下方向にプルするとリフレッシュインジケータが表示される
- [ ] リフレッシュによりチケット枚数が再取得され、表示が最新値に更新される
- [ ] リフレッシュ完了後（成功・失敗とも）インジケータが消える
- [ ] チケット取得失敗時に Snackbar でエラーが表示される
- [ ] リフレッシュ中の再プルで取得処理が多重実行されない
- [ ] 既存機能（単発抽選・一括抽選・報酬の追加/編集/削除）に影響がない

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `RewardListViewModelTest`: refresh() でチケットが再取得されること / isRefreshing が解除されること / 取得失敗時も isRefreshing が解除され result にエラーが流れること |
| Roborazzi | 静止状態のUIは変化しない想定のため `compareRoborazziDebug` で差分なしを確認（ゴールデン更新なし） |
| Maestro E2E | なし（プルリフレッシュのスワイプ操作とチケット枚数変化の検証はサーバー側の残高変更を伴い、E2Eで安定して再現できないため。既存フローの回帰は CI に委ねる） |

## 8. 未決事項・リスク

- ローカルモードのチケット残高は DataStore の Flow で常時最新が流れるため、プルリフレッシュの実益はネットワークモードで大きい（ローカルモードでは即時完了し、動作上の問題はない）
- `PullToRefreshBox` は ExperimentalMaterial3Api のため、将来の Compose BOM 更新で API 変更の可能性がある
