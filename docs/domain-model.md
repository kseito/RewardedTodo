# ドメインモデル

## 概要

RewardedTodoは「Todoを完了するとチケット（ポイント）を獲得し、チケットを使って報酬の抽選ができる」アプリ。
Todoの管理にはTodoist APIを活用しており、チケットの加算はサーバーサイドのWebhookが行う。

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

**ビジネスルール**
- `RewardCollection` で管理できる報酬数の上限は7つ
- `probability` は抽選時にチケット枚数へ変換される（後述）

---

### Todo（タスク）

```
domain/todo/Todo.kt
domain/todo/EditingTodo.kt
```

| フィールド | 型 | 説明 |
|-----------|---|------|
| `id` | `Long` | ローカルDB上の識別子 |
| `todoistId` | `String?` | Todoistのタスクid（同期前はnull） |
| `name` | `String` | タスク名 |
| `numberOfTicketsObtained` | `Int` | 完了時に獲得するチケット枚数（デフォルト: 1） |
| `isRepeat` | `Boolean` | リピートタスクか |

**バリデーション**（EditingTodo.validate()）
- 名前: 空文字不可、500文字以内
- `numberOfTicketsObtained`: 1〜100の範囲

**isRepeatの挙動**
- `false`: 完了時にローカルDBから削除
- `true`: 完了時に「完了済み」状態に変更（次の同期まで残る）

---

### Ticket（チケット/ポイント）

```
domain/reward/Ticket.kt
domain/reward/NumberOfTicket.kt
domain/reward/RewardCollection.kt
domain/reward/LotteryBox.kt
```

抽選で使うポイントの概念。sealed classで当選/ハズレを表現する。

```kotlin
sealed class Ticket {
    data class Prize(val rewardId: RewardId) : Ticket()  // 当選（どの報酬か紐づき）
    object Miss : Ticket()                                // ハズレ
}
```

**チケットプールの生成ロジック** (`RewardCollection.createTickets()`)
- 総枚数は常に10,000枚（`Ticket.ISSUE_LIMIT`）
- 各Rewardの `probability` を `(100 × probability).roundToInt()` でチケット枚数に変換
- 余った枚数を `Ticket.Miss` で埋める

例：Reward（確率50%）が1つの場合
```
Prize(reward1) × 5,000枚
Miss           × 5,000枚
合計            10,000枚
```

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

## エンティティ間の関係

```
┌─────────────────────────────────────────────────────────────┐
│  Todoist (外部サービス)                                       │
│  ・今日/期限切れタスクを提供                                   │
│  ・タスク完了をWebhookでサーバーに通知                         │
└──────────────────┬──────────────────────────────────────────┘
                   │ sync (3-way)
                   ▼
┌──────────────────────────────┐
│  Todo                        │
│  numberOfTicketsObtained: Int│──┐ 完了するとサーバーが
└──────────────────────────────┘  │ チケットを加算
                                  ▼
                   ┌──────────────────────────────┐
                   │  チケット残高 (DataStore)     │
                   │  consumeTicket() で1枚消費    │
                   └──────────────┬───────────────┘
                                  │ 消費後に抽選
                                  ▼
┌──────────────────────────────────────────────────┐
│  RewardCollection                                │
│  └── createTickets() でチケットプール生成          │
│       ├── Prize(rewardId) × probability分         │
│       └── Miss × 残り分                           │
└──────────────────────────────────────────────────┘
                   │
                   ▼ ランダムドロー
              ┌─────────┐
              │ Ticket  │
              ├─────────┤
   当選 →     │ Prize   │ → Reward を返す
   ハズレ →   │ Miss    │ → null を返す
              └─────────┘
```

---

## 主要なビジネスロジック

### 1. 抽選（LotteryUseCase）

1. チケット残高を1枚消費（不足時: `LackOfTicketsException`）
2. `RewardCollection.createTickets()` でチケットプールを生成
3. `LotteryBox.draw()` でランダムに1枚引く
4. `Prize` なら対応する `Reward` を返す、`Miss` なら `null`

### 2. 一括抽選（BatchLotteryUseCase）

デフォルト10回の抽選を実施し、`BatchLotteryResult(wonRewards, missCount)` にまとめて返す。

### 3. Todo完了（CompleteTodoUseCase）

1. `todoRepository.complete(todo)` を呼ぶ
2. Todoist APIで完了マーク
3. チケット加算はサーバーサイドのWebhookが担当（クライアントは加算しない）

### 4. Todoist同期（FetchTodoListUseCase）

3-way sync:
- Todoistにあってローカルにある → 名前を更新
- Todoistにあってローカルにない → 新規追加
- ローカルにあってTodoistにない → `isRepeat=true` なら完了状態へ、`false` なら削除

---

## 未実装・将来の変更点

- `IPointRepository`: インターフェースのみ定義、実装なし（auth モジュール連携を予定）
- `UsePointUseCase`: ユースケースのインターフェースのみ存在、Interactorは未実装
- チケット加算のクライアント側実装 (`NetworkTicketRepository.addTicket()`) は空実装
