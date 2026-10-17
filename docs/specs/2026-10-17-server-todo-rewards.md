# Todoごとの獲得数をサーバーへ移す計画・仕様

- 作成日: 2026-10-17
- 状態: 実装済み・ローカル検証完了（未デプロイ）
- ブランチ: 両リポジトリとも `codex/server-todo-rewards`
- サーバー: https://github.com/kseito/RewardedTodoServer

## 1. 目的と範囲
Todoistを唯一のタスク管理元とし、完了時の獲得枚数だけをReward Serverへ保存する。ローカルTodoは対象外。Todo用Room Entity/DAO/DB/DI/依存を削除する。報酬用Roomと認証用DataStoreは維持する。既存の一覧・詳細画面は残す。

## 2. データと契約
- タスク名・完了・繰り返し: Todoist。アプリ詳細では名前を読み取り専用とし、獲得枚数のみ編集する。ローカルだけを削除する旧Delete操作を廃止する（Todoistタスクを誤って削除しない）。
- 設定: `task_reward_settings(user_id, todoist_task_id, points_per_completion, updated_at)`。複合主キー、枚数1〜100。未登録は1。
- `GET /api/task-rewards/{userId}`: 当該ユーザーの登録済み設定の一覧。
- `PUT /api/task-rewards/{userId}/{taskId}`: `{points_per_completion}`。成功時に保存した設定を返し、後から保存された値で上書きする。
- 全APIは既存Bearer認証を利用。PUTはTodoistへのアクセス権も検証する。
- 加算: 署名検証済みの完了Webhookだけで行う。処理時点の設定値を採用。サブタスクは従来どおり対象外。
- `processed_task_completions` にユーザー、配信ID、タスクID、付与枚数を記録。同じ配信IDは再加算しない。タスクIDだけでは重複判定しない。
- 設定取得失敗を未登録扱いにしない。保存成功前に画面を閉じない。競合時は再取得して再編集する。

## 3. クライアント構成
DomainにTodoistTaskIdと設定リポジトリIF。data:ticketが設定APIを実装し、data:todoはdomainのIF経由で利用。TodoRepositoryはTodoist取得結果と設定を結合し、StateFlowにメモリ保持する。通常操作ではDBを使わない。成功した完了だけ一覧から取り除く。操作を直列化し連打による重複を防ぐ。全ページ取得して一覧の取りこぼしを防ぐ。

初回のみ旧SQLite DBを読み取り専用で開き、既存設定を移行する（Room依存は不要）。移行失敗時はDBを保存し一覧取得/完了を止める。移行成功後にTodo用DBを削除する。アカウント変更・ローカルデータ消去では旧DBも削除し別ユーザーへ持ち越さない。移行で残高を増やさない。

## 4. 実装順序
1. 計画書・ADR・API契約を記録。
2. サーバー: テーブル/API、所有権確認、Webhook可変加算、同時更新・重複対策、テスト。
3. クライアント: domain → application → data → feature → DI。Todo用Roomの撤去、旧設定移行。
4. テスト・ビルド・静的検査と画面検証。関連ドキュメントを更新。
5. サーバー先行でリリース、その後アプリ。本セッションでは本番DB変更・デプロイ・pushは行わない。

## 5. 受け入れ条件
- [x] 設定3のタスク完了で3枚付与、完了件数は1増える。
- [x] 未設定は1、0/101を拒否、他ユーザーの読み書きを拒否。
- [x] 同じ配信は1回のみ、同じ繰り返しタスクの別配信は別途加算。
- [x] 設定の競合409、同一更新の再送は安全。
- [x] 初回移行は既存サーバー設定を上書きせず、失敗時は旧DBを保持。
- [x] 一覧はTodoist+サーバーから取得。Todo用Room依存がない。
- [x] 保存中の連打を防ぎ、失敗時入力を保持。完了失敗時に一覧を消さない。
- [x] ログアウト/データ消去でメモリ・旧DBを消し、別アカウントへ漏らさない。

## 6. 検証・運用
サーバーはAPI/DB統合テスト、Androidは設定API・TodoRepository・移行・ViewModelのユニットテスト、assembleDebug、spotless、lint、可能な環境でスクリーンショット比較を実行する。PostgreSQL固有の並行更新は本番と同種のDBで確認する。切り戻しはクライアント公開を止め、サーバーテーブルと付与履歴を保存する。旧版アプリに戻すとサーバー設定を編集できないためサーバーを固定1加算に戻してはならない。

## 7. 留意点
既存DBにはユーザーIDがないため、アップデート時に連携中のユーザーのデータとして移行する。旧版でアカウント切替時の削除が行われる前提。削除済み・アクセス不能タスクの設定は引き継がない。Webhook遅延中は残高が即時反映されない。複数端末の異なる初期設定は先に移行した値を優先する。

Webhook再送識別の根拠: https://developer.todoist.com/api/v1/ のWebhooks / X-Todoist-Delivery-ID。

## 8. 実装結果（2026-10-17）

### 作業場所
- Android: `/Users/kazutoseito/AndroidStudioProjects/RewardedTodo`
- Server: `/Users/kazutoseito/AndroidStudioProjects/RewardedTodoServer`
- 両方とも `codex/server-todo-rewards`。変更はファイル単位でコミットする。未push。本番API呼び出し、DB変更、デプロイは行っていない。

### 検証結果
- Server: `./gradlew test` 成功（21件、H2）。設定APIの認可・範囲検証・競合・初回移行、署名付きWebhook、重複通知、繰り返し完了、並行した加算/消費を検証。
- Android: `testDebugUnitTest` 全体成功（167件）。変更後の対象モジュールも再実行して成功。
- `assembleDebug` / `assembleE2e` 成功。
- `lintDebug` / `spotlessCheck` / `detektCustomRules` 成功。既存のRoom非推奨API、Hilt注釈プロセッサ、Gradle非推奨機能などの警告は残る。既存data:todo manifestにもExtraText警告がある。
- Roborazzi比較: 意図したTodo詳細画面2枚のみ差分（Deleteボタン撤去）。目視確認後、ローカル基準画像を更新し `verifyRoborazziDebug` 成功。`app/screenshots/` は既存方針でGit管理対象外。
- Maestro / Pixel_9エミュレータ: `edit-todo-flow.yaml` と `complete-todo-flow.yaml` の2件成功。編集した枚数が保存レスポンスで表示され、アプリ再起動後も取得・表示できることを確認。バックエンドはWireMock。実機のデータは変更していない。

### 公開前に残る確認
- PostgreSQLでの同時更新検証は未実施（ローカルDocker daemonが停止中）。サーバー `docs/task-rewards-api.md` に使い捨てPostgreSQLでの実行手順を記載。
- 実際のTodoist・Reward Serverを通した検証は未実施。ステージングで設定保存→完了→Webhook→残高/履歴、および既存端末DBからの初回移行を確認してから公開する。
- サーバーを先行デプロイし、新APIが利用可能になってからAndroidを配布する。旧DBを削除済みの端末で旧版アプリに戻す場合は、サーバーの設定を正本として保持する。

## 9. リリースTODO

- [x] サーバーのPRを作成し、テーブル定義・Webhookの冪等性・API契約をレビューする（[#3](https://github.com/kseito/RewardedTodoServer/pull/3)）。
- [ ] 本番PostgreSQLをバックアップし、復元手順と対象時刻を記録する。
- [x] 使い捨てPostgreSQLで `TaskRewardTest` の並行更新テストを実行する。本番DBには接続しない（2026-10-17、成功）。
- [ ] サーバーを先行デプロイし、ヘルスチェックと既存の残高照会・消費APIを確認する。
- [ ] テスト用Todoで「3枚に保存 → 完了 → 3枚付与」を確認し、同じ `X-Todoist-Delivery-ID` の再送で二重付与されないことを確認する。
- [ ] AndroidのPRを作成し、Room撤去・初回移行・保存失敗時の画面挙動をレビューする。
- [ ] 既存アプリが入ったテスト端末で、旧Roomの設定がサーバーへ一度だけ移行されることを確認する。
- [ ] Androidをテスト配布し、設定の変更・再起動後の再取得・完了後の残高反映を確認する。
- [ ] 本配布後、サーバーログの401・409・Webhook失敗・移行失敗を監視する。
