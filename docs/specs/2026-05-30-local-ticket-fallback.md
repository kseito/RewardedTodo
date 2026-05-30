# Todoist未連携時のローカルチケットフォールバック 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Approved |
| 作成日 | 2026-05-30 |
| ブランチ | feature/local-ticket-fallback |
| 関連Issue/PR | （なし。背景となるPRは #666） |

## 1. 背景・目的

PR #666 でチケット管理は完全にサーバ側に集約され、TodoistのWebhookで加算する設計になった。
結果としてTodoist未連携のユーザーは Todo を完了してもチケットを得られず、抽選機能が事実上機能停止状態になっている（Reward画面のloadPoint通信失敗によるクラッシュは PR #713 で軽減済みだが、ポイントは0のまま操作不能）。
本機能では、Todoist連携の有無でローカル/サーバを自動的に切り替えるルーティング層を導入し、未連携ユーザーでも抽選を遊べるようにする。

## 2. 要件

### 機能要件
- Todoist未連携時（Todoist APIトークン未保存）: Todo完了でローカル(DataStore)のチケットが `todo.numberOfTicketsObtained` 分加算され、Reward画面ではローカルのチケットが表示・消費される。
- Todoist連携時: 従来通り、サーバ（Webhook加算 / REST消費）でチケットが管理される。
- 連携状態の判定はチケット操作ごとに `UserPreferencesKeys.TODOIST_API_TOKEN` の有無で行う。中央集権なルータがこの判定を担う。
- 既存の `ITicketRepository` インターフェースは変更しない。
- 既存の `NetworkTicketRepository` の挙動は変更しない（旧 `TicketRepository` も内部実装として活用）。

### 非機能要件 / 制約
- `NetworkTicketRepository` のリトライ/エラーマップは変更しない。
- ローカルチケット用のDataStoreキー (`number_of_ticket`) は旧実装と同一を維持する（過去保存値があれば引き継がれる）。

## 3. 画面・UX

- 対象画面: RewardListScreen（ポイント表示・抽選）、Todo画面（完了でチケット獲得）
- UI変更点: なし（データ層の切替のみ）
- 操作フロー（未連携ケース）:
  1. ユーザーがTodoist未設定のままTodoを追加して完了
  2. Reward画面に戻るとローカルで `+numberOfTicketsObtained` 加算されたチケット数が表示される
  3. 抽選ボタンを押すとローカルから消費される

## 4. ドメインへの影響

`docs/domain-model.md` 参照。

- 関係するエンティティ / Value Object: `Ticket`, `NumberOfTicket`, `ApiToken`
- ルール変更:
  - チケット加算: Todo完了時、未連携ならローカル増加、連携済みなら従来どおりWebhook（クライアント側 no-op）
  - チケット消費・取得: 未連携ならローカル、連携済みならサーバ

## 5. レイヤー別の変更方針

`docs/module-dependency.md` 参照。`data:ticket` は既に `common:kvs` 経由でDataStoreにアクセスしておりトークンキーが参照可能なため、`domain:todo` への新規依存は発生しない。

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Data | data/ticket | 旧 `TicketRepository` を `LocalTicketRepository` にリネーム（実装は同一）。新規 `RoutingTicketRepository` を追加し、DataStoreでTodoistトークン有無を判定して `Local` / `Network` に委譲する。 |
| Application | application/todo | `CompleteTodoInteractor` に `ITicketRepository` を再注入し、完了時に `addTicket(todo.numberOfTicketsObtained)` を呼び戻す。 |
| DI | app/di/RepositoriesModule.kt | `ITicketRepository` のバインド先を `NetworkTicketRepository` から `RoutingTicketRepository` に変更。 |

> Domain / Feature 層の変更はなし。

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] Todoist未連携で Todo を完了すると、ローカルのチケット枚数が `todo.numberOfTicketsObtained` 分増える
- [ ] Todoist未連携でReward画面を開くとローカルチケット枚数が表示される
- [ ] Todoist未連携で抽選するとローカルチケットが消費される（不足時に `LackOfTicketsException`）
- [ ] Todoist連携時は `RoutingTicketRepository` が `NetworkTicketRepository` に委譲し、従来動作を維持する
- [ ] `LocalTicketRepository` の既存ユニットテスト（旧 `TicketRepositoryTest`）が引き続き通る
- [ ] `RoutingTicketRepository` のユニットテストでトークン有無による分岐が検証される

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | 新規 `RoutingTicketRepositoryTest`（トークン有無で正しいリポジトリに委譲）、既存 `TicketRepositoryTest` → `LocalTicketRepositoryTest` にリネーム追従 |
| Roborazzi | なし（UI変更なし） |
| Maestro E2E | 既存の `single-lottery-flow.yaml` 等が未連携でも通ることを期待する。本仕様では新規フローは追加せず、別ブランチ `feature/delete-non-repeat-reward` の `delete-non-repeat-reward-flow.yaml` を当該ブランチで実行する。 |

## 8. 未決事項・リスク

- 連携状態が途中で変わる（トークン保存/削除）と、すでに `collect` 中の `Flow<NumberOfTicket>` は古いソースを参照し続ける可能性がある。MVPでは画面再表示で切り替わる前提とし、`flatMapLatest` での動的化は将来課題とする。
- ローカルとサーバのチケットは独立して管理される。連携前にローカルで貯めた分を連携後にサーバへ持ち込むマイグレーションは本スコープ外。
