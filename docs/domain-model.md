# ドメインモデル

## 概要

RewardedTodoは「Todoを完了するとチケット（ポイント）を獲得し、チケットを使って報酬の抽選ができる」アプリ。
Todoの管理にはTodoist APIを活用しており、チケットの加算はサーバーサイドのWebhookが行う。

**Todoist連携はアプリ利用の前提**。未連携では認証画面から先へ進めず、Todoもチケットも扱えない。
チケット残数はRewardサーバーが一元管理する。
外部システムとの契約（エンドポイント・認証・Webhookの仕様）は [external-integration.md](external-integration.md) を参照。

---

## エンティティ

### Reward（報酬）

```
domain/reward/Reward.kt
domain/reward/RewardId.kt
domain/reward/RewardName.kt
domain/reward/RewardDescription.kt
domain/reward/Probability.kt
```

| フィールド | 型 | 説明 |
|-----------|---|------|
| `rewardId` | `RewardId` (inline: Int) | 一意識別子 |
| `name` | `RewardName` (inline: String) | 報酬名 |
| `probability` | `Probability` (inline: Float) | 当選確率 (0〜100) |
| `description` | `RewardDescription` (inline: String?) | 説明文（任意） |
| `needRepeat` | `Boolean` | 繰り返し取得可能か |
| `version` | `Int` | 設定更新・削除の競合検知用 |

**ビジネスルール**
- `RewardCollection` で管理できる報酬数の上限は7つ
- 設定はサーバーが正本。ローカル報酬DBは移行せず削除する

---

### Todo（タスク）

```
domain/todo/Todo.kt
domain/todo/EditingTodo.kt
```

| フィールド | 型 | 説明 |
|-----------|---|------|
| `todoistId` | `String` | Todoistのタスクid |
| `rewardVersion` | `Int` | サーバー設定の更新バージョン |
| `name` | `String` | タスク名 |
| `numberOfTicketsObtained` | `Int` | 完了時に獲得するチケット枚数（デフォルト: 1） |
| `isRepeat` | `Boolean` | リピートタスクか |

**生成元**
- TodoはTodoistから取得する。ローカルTodoとTodo用Room DBは廃止。
- 獲得枚数はサーバー設定と結合する。未設定は1。設定の読み書きには `ITaskRewardRepository` を使う。
- タスク名はTodoistを正本とし、詳細画面では読み取り専用。

**バリデーション**（EditingTodo.validate()）
- 名前: 空文字不可、500文字以内
- `numberOfTicketsObtained`: 1〜100の範囲

**isRepeatの挙動**
- 繰り返しかどうかにかかわらず、完了API成功後はメモリ上の一覧から取り除く。
- 次回取得時にはTodoistの状態に従う。獲得数設定は同じTodoistタスクIDで引き継ぐ。

---

### 抽選と獲得報酬

抽選はReward Serverが行う。1回でチケット1枚を消費し、消費・抽選履歴・獲得台帳を同一トランザクションで確定する。確率はサーバーでは0.01%単位の整数（合計10,000以下）、Androidの編集表示では百分率として扱う。

`AcquiredReward` は獲得1回分の権利で、台帳ID、元の設定ID、獲得時の名前・説明、獲得日時、使用日時を持つ。使用日時がnullなら未使用。設定を編集・削除しても獲得内容は変えない。使用済みの記録も残す。

繰り返し不可の設定は1回当たった時点で無効化し、一括抽選の残りではその確率をハズレにする。他の報酬の確率は変えない。有効設定は最大7件、台帳件数は無制限。

`LotteryOutcome` は回数・獲得報酬・ハズレ数。`ILotteryRepository` は抽選と保留要求のPOST再送を扱う。台帳一覧・使用は `IAcquiredRewardRepository` を使う。

---

### RewardUser（ユーザーの報酬情報）

```
domain/reward/RewardUser.kt
```

| フィールド | 型 | 説明 |
|-----------|---|------|
| `id` | `Long` | ローカルID |
| `todoistId` | `Long` | TodoistのユーザーID |
| `point` | `Int` | 現在の所持チケット枚数 |

---

### ApiToken（Todoist APIトークン）

```
domain/todo/ApiToken.kt
domain/todo/TokenError.kt
```

- フォーマット: 40文字の16進数文字列
- `ApiToken.create(token)` でバリデーションし、不正な場合は `TokenError` を返す

```kotlin
sealed class TokenError {
    object InvalidFormat : TokenError()
    object EmptyToken : TokenError()
    data class NetworkError(val error: Throwable) : TokenError()
    object Timeout : TokenError()
}
```

---

## 主要なビジネスロジック

### 1. 抽選

Todoist完了Webhook → サーバーでチケット加算 → アプリから抽選要求 → サーバーで消費・抽選・獲得記録を確定 → アプリで結果表示。

AndroidのLotteryInteractorはサーバー結果を表示用に変換する。端末では乱数生成や当選判定をしない。通信失敗時はユーザー別に保持した要求IDでPOSTを再送する。

### 2. 一括抽選

UIでは10回。開始時の残高が足りなければ全体を拒否する。途中で全対象を獲得済みになった場合は残りをハズレとして実行する。繰り返し可は当選回数分の台帳行を追加する。

### 3. Todo完了（CompleteTodoUseCase）

1. `todoRepository.complete(todo)` を呼ぶ
2. Todoist APIで完了マーク
3. チケット加算はサーバーサイドのWebhookが担当（クライアントは加算しない）

### 4. Todoist同期（FetchTodoListUseCase）

Todoistから対象タスクを全ページ取得し、Reward Serverの獲得枚数設定と結合してメモリに保持する。旧Todo用Roomの設定は移行しない。取得失敗時は既存メモリ状態を維持し、エラーを通知する。

---

## 未実装・将来の変更点

- `IPointRepository`: インターフェースのみ定義、実装なし（auth モジュール連携を予定）
- `UsePointUseCase`: ユースケースのインターフェースのみ存在、Interactorは未実装
- チケット加算のクライアント側実装 (`TicketRepository.addTicket()`) は空実装
