// complete-todo-flow 用のスタブ。
// Todoistが1件タスクを返し、それを完了するとサーバー側のWebhookがポイントを1加算する状況を作る。

const ADMIN = 'http://localhost:8080/__admin'
const SCENARIO = 'complete-todo'

const TASK_ID = 'task-1'
const TASK_LIST = { method: 'GET', urlPath: '/todoist/api/v1/tasks/filter' }
const POINTS = { method: 'GET', urlPathPattern: '/reward/api/points/[^/]+' }

function post(path, payload) {
  http.post(ADMIN + path, {
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

function stub(mapping) {
  post('/mappings', mapping)
}

function tasks(state, results) {
  stub({
    scenarioName: SCENARIO,
    requiredScenarioState: state,
    request: TASK_LIST,
    response: { status: 200, jsonBody: { results: results } },
  })
}

function points(state, available) {
  stub({
    scenarioName: SCENARIO,
    requiredScenarioState: state,
    request: POINTS,
    response: {
      status: 200,
      jsonBody: {
        user_id: 'mock-user-id',
        total_points: available,
        available_points: available,
        task_count: available,
      },
    },
  })
}

// 前のフローが登録したスタブとシナリオ状態を持ち越さない
post('/reset', {})

// 完了前: タスクが1件、ポイントは0
tasks('Started', [{ id: TASK_ID, content: 'Task to Complete', checked: false, due: { is_recurring: false } }])
points('Started', 0)

// 完了を受けたら状態を進める
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'Started',
  newScenarioState: 'task-closed',
  request: { method: 'POST', urlPath: '/todoist/api/v1/tasks/' + TASK_ID + '/close' },
  response: { status: 204 },
})

// 完了後: タスクは消え、ポイントが1
tasks('task-closed', [])
points('task-closed', 1)
