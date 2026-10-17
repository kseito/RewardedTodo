# 外部連携仕様

本アプリは2つの外部システムと連携する。このドキュメントは、その**契約（エンドポイント・ペイロード・認証・エラー時の挙動）**を記録する。

| 連携先 | 役割 | 実装モジュール |
|---|---|---|
| **Todoist** | Todoの取得・完了。OAuthによる認証 | `data:todoist`, `data:todo` |
| **Reward Server** | チケット（ポイント）残高の管理 | `data:ticket` |

Reward Server は別リポジトリ **[kseito/RewardedTodoServer](https://github.com/kseito/RewardedTodoServer)**（Kotlin + Ktor / PostgreSQL / Cloud Run、private）で実装されている。

---

## 全体像

```
┌──────────────────────────────────────────────────────────────┐
│  Todoist                                                     │
└───┬──────────────────────────────────────┬───────────────────┘
    │ ① OAuth / タスク取得・完了            │ ② item:completed Webhook
    │    (アプリ → Todoist)                 │    (Todoist → サーバー)
    ▼                                      ▼
┌─────────────────────┐             ┌──────────────────────────┐
│  RewardedTodo (app) │ ─────────►  │  Reward Server           │
│                     │  ③ 残高照会  │  ・ポイント加算 (Webhook) │
│  加算はしない         │    / 消費    │  ・残高照会 / 消費 (REST) │
└─────────────────────┘             └──────────────────────────┘
```

チケットの**加算はサーバーがWebhookで行い、アプリは一切加算しない**（`TicketRepository.addTicket()` は意図的な空実装）。
アプリからは残高の**照会と消費のみ**を行う。

---

## Todoist連携

### 認証（OAuth 2.0 Authorization Code + PKCE）

`client_secret` を持たない**公開クライアント**として登録している。クライアントの識別には
[OAuth Client ID Metadata Document](https://developer.todoist.com/api/v1/#tag/Authorization) を使い、
**`client_id` はメタデータ文書のURLそのもの**になる。

設定値は `app/build.gradle.kts` の `buildConfigField` から `TodoistOAuthConfig` に注入される。

| 項目 | 値 |
|---|---|
| `client_id` | `https://kseito.github.io/rewardedtodo/oauth/client.json` |
| 認可エンドポイント | `https://todoist.com/oauth/authorize` |
| トークンエンドポイント | `https://api.todoist.com/oauth/access_token` |
| `redirect_uri` | `https://kseito.github.io/rewardedtodo/oauth/callback` |
| `scope` | `data:read_write` |
| PKCE | `code_challenge_method=S256` |

`redirect_uri` はHTTPSのみ受け付けられるため、認可後の戻り先とメタデータ文書は
`kseito/kseito.github.io` に公開する。公開ファイルの原本と配置手順は [docs/oauth/README.md](oauth/README.md) を参照。

#### フロー

1. `StartTodoistAuthInteractor` が `code_verifier` と `state` を生成し、メモリ上の
   `TodoistAuthSessionStore` に保持したうえで認可URLを組み立てる
2. Auth Tab（Custom Tabs）で認可画面を開く。HTTPSリダイレクトをアプリに戻すため
   Digital Asset Links（`assetlinks.json`）の検証を通す必要がある
3. コールバックで受け取った `code` を `state` の一致確認後にトークンへ交換する

```
POST https://api.todoist.com/oauth/access_token
Content-Type: application/x-www-form-urlencoded

client_id={metadata document URL}
grant_type=authorization_code
code={認可コード}
redirect_uri={redirect_uri}
code_verifier={PKCE verifier}
```

```json
{
  "access_token": "...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "refresh_token": "...",
  "scope": "data:read_write"
}
```

本アプリはリフレッシュを有効にしているため `expires_in` は常に返る（リフレッシュ未有効のアプリには
10年の互換値 `315360000` が返る）。`refresh_token` は消費済みリフレッシュトークンを
グレース期間（60秒）内に再送した場合に**省略され**、そのとき `access_token` は元の再発行分と同じ値が返る。
省略されたフィールドは呼び出し側で既存の値とmergeする。

リフレッシュは `grant_type=refresh_token` に `refresh_token` を添えて同じエンドポイントを叩く。

#### トークンの保持と更新

- 保存先は平文のDataStore。判断の根拠は [docs/adr/0001-plaintext-token-storage.md](adr/0001-plaintext-token-storage.md)
- `expires_in`（相対秒）は**絶対時刻に変換**して `TODOIST_TOKEN_EXPIRES_AT` に保持する
- OkHttpの `Interceptor` が期限切れを先回りしてリフレッシュし、`Authenticator` が401を受けたときに
  1度だけ再送する（Todoist側で連携を取り消された場合の受け皿）

### 使用エンドポイント

baseUrl は `https://api.todoist.com`。認証は `Authorization: Bearer {access_token}`。

| メソッド | パス | 用途 | 呼び出し元 |
|---|---|---|---|
| `GET` | `/api/v1/tasks/filter?query=today\|overdue` | 同期対象タスクの取得 | `TodoRepository.sync()` |
| `POST` | `/api/v1/tasks/{id}/close` | タスクを完了にする | `TodoRepository.complete()` |
| `POST` | `/oauth/access_token` | トークン交換・リフレッシュ | `TodoistAuthRepository` |
| `GET` | `/api/v1/user` | アクセストークンからユーザーID解決 | **Reward Server**（アプリからは呼ばない） |

`/api/v1/tasks/filter` のレスポンスは `results` 配列。アプリが参照するフィールドは以下のみ。

```json
{
  "results": [
    { "id": "...", "content": "...", "checked": false, "due": { "is_recurring": false } }
  ]
}
```

`checked=true` のタスクを除外し、next_cursorがある間は全ページを取得する。タスクを獲得数設定と結合してメモリに保持する。完了API成功後に一覧から除き、再取得時にはTodoistの現在の状態を表示する。

---

## Reward Server連携

### 接続設定

| 項目 | 内容 |
|---|---|
| baseUrl | `BuildConfig.REWARD_SERVER_URL`（`local.properties` の `reward.server.url` 由来） |
| 認証 | `Authorization: Bearer {Todoistのaccess_token}` |
| シリアライズ | Moshi（JSONのキーは `snake_case`） |

設定手順は [docs/setup.md](setup.md) を参照。URL自体は秘匿値として `local.properties` と
CIのRepository Secretsで管理し、リポジトリにはコミットしない。

### 認証モデル

Reward Server は独自の認証基盤を持たず、**Todoistのアクセストークンを身分証として扱う**。

1. アプリが `GET /api/users/me` を叩く
2. サーバーが受け取ったトークンで Todoist の `GET /api/v1/user` を呼び、ユーザーIDを得る
3. サーバーはトークンの **SHA-256ハッシュ**をユーザーレコードに保存し、`user_id` を返す
4. 以降のリクエストは、送られたトークンのハッシュが `user_id` に紐づくものと一致するかで認可する

アプリは解決済みの `user_id` を `REWARD_USER_ID` としてDataStoreにキャッシュし、以降のパスに使う。

### エンドポイント

#### `GET /api/users/me` — ユーザーID解決

```json
{ "user_id": "..." }
```

トークンが無効な場合は `401`。

#### `GET /api/points/{userId}` — 残高照会

```json
{ "user_id": "...", "total_points": 42, "available_points": 12, "task_count": 42 }
```

アプリが使うのは `available_points` のみ（`getNumberOfTicket()` の戻り値）。

#### `POST /api/points/{userId}/consume` — チケット消費

```json
{ "count": 1 }
```

レスポンスは消費後の `PointsInfo`（上と同じ形）。抽選1回で1枚、一括抽選ではまとめて消費する。

#### `POST /webhook/todoist` — Todoistからのタスク完了通知

**アプリは呼ばない。** Todoistがサーバーに直接送る。→ [チケット加算Webhookの仕様](#チケット加算webhookの仕様)

#### `GET /` — ヘルスチェック

`RewardedTodoServer is running!` を返す。

### エラーレスポンスとアプリ側の扱い

エラー時のボディは `{ "error": "..." }`。

| ステータス | 意味 | `TicketRepository` の挙動 |
|---|---|---|
| `401` | トークンのハッシュが `user_id` に紐づかない | キャッシュした `REWARD_USER_ID` を破棄して再解決し、**1度だけリトライ** |
| `422` | 残高不足（`INSUFFICIENT_POINTS`） | `LackOfTicketsException` に変換して上位へ |
| `400` | `userId` 欠落、`count <= 0` | そのまま伝播 |

`401` のリトライは、サーバーを作り直してユーザーレコードが消えた場合などに、キャッシュ済み
`user_id` のまま照会し続けて復帰できなくなるのを防ぐためのもの。

### チケット加算Webhookの仕様

Todoistのタスク完了イベントを受けて、サーバーがポイントを加算する。

**リクエスト**

```
POST /webhook/todoist
Content-Type: application/json
X-Todoist-Hmac-SHA256: {署名}

{
  "event_name": "item:completed",
  "user_id": "...",
  "event_data": { "id": "...", "content": "...", "parent_id": null }
}
```

**署名検証**

`Base64(HMAC-SHA256(client_secret, リクエストボディ))` が `X-Todoist-Hmac-SHA256` と一致するかを
定数時間比較で検証する。不一致・ヘッダ欠落はいずれも `401`。
`client_secret` はサーバーの環境変数 `TODOIST_CLIENT_SECRET` にのみ存在し、**アプリは保持しない**。

**加算ルール**

| 条件 | 挙動 |
|---|---|
| `event_name` が `item:completed` 以外 | 何もしない |
| `event_data.parent_id` が非null（サブタスク） | 加算しない |
| 上記以外 | `total_points` と `available_points` にタスクの設定枚数（未設定は1）を加算、`task_count` を1増やす |

加算と消費はいずれも履歴テーブルに記録される（`type` が `earn` / `spend`）。

完了イベントは `event_data.id` と `X-Todoist-Delivery-ID` が必須。配信IDを記録し、同じ通知を再加算しない。加算・履歴・配信記録をDBトランザクションで確定後に200を返す。処理失敗時は非200で再送を促す。

---

## Todoごとの獲得数

設定の正本はReward Server。`GET /api/task-rewards/{userId}`、`PUT /api/task-rewards/{userId}/{taskId}`、初回移行用の `POST /api/task-rewards/{userId}/import` を使う。詳細なペイロードと移行方針は [計画書](specs/2026-10-17-server-todo-rewards.md) を参照。

取得失敗と未設定を区別する。未設定のみ1枚、保存値は1〜100。後から保存された値で設定を上書きする。保存成功まで画面を閉じない。Todo用Roomを廃止し、通常操作はメモリにのみ保持する。初回に旧DBから既存設定を移行し、成功後にTodo用DBを削除する。

## Todoist未連携時のふるまい

認証画面から先へ進めず、Todo・チケットを扱わない。ローカルTodoやローカル残高への切り替えはない。
