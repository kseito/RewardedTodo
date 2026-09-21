# E2E用のフェイクバックエンド

[WireMock](https://wiremock.org/) を別プロセスで立て、TodoistとRewardサーバーの両方を代替する。

Maestroはアプリを外側から操作するツールなので、フェイクもアプリの外側（別プロセス）に置く。

## 起動

```bash
bash .github/scripts/start-wiremock.sh
```

`build/wiremock/wiremock-standalone.jar` を配置してポート8080で起動し、応答が返るまで待つ。

到達経路は2つある。

| 誰から | URL |
|--------|-----|
| エミュレータ上のアプリ | `http://10.0.2.2:8080` |
| Maestroのスクリプト | `http://localhost:8080` |

アプリをこのサーバーに向けるには `e2e` ビルドタイプを使う。

```bash
./gradlew installE2e
```

URLは `app/build.gradle.kts` の `e2e` ビルドタイプで定義している。`debug` / `release` は本番のURLを向く。

## パスの割り当て

| プレフィックス | 代替する相手 |
|--------------|------------|
| `/todoist/...` | Todoist OAuth + REST API |
| `/reward/...` | Rewardサーバー |
| `/__admin/...` | WireMockの管理API |

## スタブの二層構造

### 既定スタブ（`mappings/*.json`）

起動時に読み込まれ、全フローに効く。バックエンドを気にしないフロー（報酬のCRUDなど）は、これだけで動く。

- Todoistのタスク一覧は空
- ポイント残数は0
- ポイント消費は422
- トークン交換とユーザーID解決は常に成功

タスク一覧・ポイント系は `priority: 10` を付けて優先度を下げてある。フローが登録するスタブ（既定の優先度5）が勝ち、そのスタブが効かない状態に移ると既定へ戻る。

### フロー固有のスタブ（`../stubs/*.js`）

バックエンドの振る舞いが必要なフローは、冒頭で `runScript` を使って自分用のスタブを登録する。

```yaml
- runScript: stubs/complete-todo.js
- clearState
- launchApp
```

状態の変化は WireMock の Scenario で表現する。「タスクを完了すると、一覧から消えてポイントが増える」であれば、`close` を受けたスタブが `newScenarioState` で状態を進め、一覧とポイントのスタブが状態ごとに違う応答を返す。

## リクエストの検証

WireMockはリクエストを記録しているので、「タップした結果として本当にAPIが呼ばれたか」をフローから確認できる。

```js
const res = http.post('http://localhost:8080/__admin/requests/count', {
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ method: 'POST', urlPath: '/todoist/api/v1/tasks/task-1/close' }),
})
```
