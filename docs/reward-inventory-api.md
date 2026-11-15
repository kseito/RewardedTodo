# 報酬・抽選・獲得台帳API

すべて既存のTodoist Bearer認証を使用する。`userId` は `/api/users/me` の戻り値。リソースの所有者もサーバーで検証する。

## 設定

- `GET /api/rewards/{userId}`: 有効な設定の配列。
- `POST /api/rewards/{userId}`: 設定を作成（201）。
- `PUT /api/rewards/{userId}/{id}`: 設定を更新（200）。
- `DELETE /api/rewards/{userId}/{id}?version=1`: 論理削除（204）。

作成・更新リクエスト:

```json
{"name":"ケーキ","description":null,"probability_bps":2500,"need_repeat":false,"version":1}
```

作成時のversionは省略可。更新は取得時のversionを必須とし、不一致は409 `VERSION_CONFLICT`。レスポンスは同じ項目に整数のidを加えたもの。更新後versionは1増える。

名前は空白のみ不可・255文字まで、説明はnull可・1000文字まで。確率は0〜10000（10000=100%）。有効設定は最大7件で、確率合計は10000以下。

## 抽選

`POST /api/lotteries/{userId}`:

```json
{"request_id":"a89b72fc-6361-4c75-adad-ded6563a5663","count":1}
```

request_idは小文字の正規形UUID。countは1〜10で、UIでは1または10を使用する。

```json
{
  "lottery_id":"debd6e87-20db-45e6-8262-eb52a6a2c55f",
  "request_id":"a89b72fc-6361-4c75-adad-ded6563a5663",
  "count":1,
  "consumed_points":1,
  "available_points_after":12,
  "executed_at":"2026-11-07T00:00:00Z",
  "results":[{
    "draw_number":1,
    "type":"win",
    "acquired_reward":{
      "id":"6a9d7e27-5d29-481a-9509-75ef277215d0",
      "reward_definition_id":1,
      "name":"ケーキ",
      "description":null,
      "acquired_at":"2026-11-07T00:00:00Z",
      "used_at":null
    }
  }]
}
```

ハズレは `{"draw_number":1,"type":"miss"}`。省略可能なnull項目は省略される場合がある。resultsは抽選順でcount件。台帳ID・抽選IDはUUID文字列、設定IDは整数。日時はUTCのISO 8601。

同じユーザー・request_id・countの再送には確定済みの同じ結果を返す。countが異なる場合は409 `REQUEST_CONFLICT`。確定時の残高・used_atはスナップショットであり、最新値は残高/台帳APIから取得する。

開始時に有効設定がなければ422 `NO_ACTIVE_REWARDS`、残高不足は422 `INSUFFICIENT_POINTS`。いずれも消費しない。一括抽選中に全対象が無効になった場合、残りはハズレとして全回数分を消費する。処理途中の失敗は全体をロールバックする。

## 台帳

`GET /api/acquired-rewards/{userId}?status=unused&limit=50&cursor=...`

- statusは `unused` / `used`。既定unused。
- limitは1〜100。既定50。
- `{"items":[獲得報酬...],"next_cursor":"..."}` を返す。終端ではnext_cursorがnullまたは省略。
- 獲得日時とIDの降順。cursorは不透明な文字列として扱い、同じstatusでのみ使用する。

`PUT /api/acquired-rewards/{userId}/{id}/use` は本文なし。使用済みにした獲得報酬を返す。再送しても使用日時を変えない。他人の獲得報酬や不明なIDは404。使用取り消し・削除APIはない。

## エラー

`{"error":"コード"}`。401は認証、400は入力不正、404はリソースなし、409は競合、422は業務上実行不可。設定件数超過は `REWARD_LIMIT`、確率合計超過は `PROBABILITY_TOTAL_EXCEEDED`。

ポイント消費は `POST /api/lotteries/{userId}` が抽選と同じトランザクションで行う。
