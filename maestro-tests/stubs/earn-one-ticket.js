// フェイクが "Earn Ticket" タスクを1件配信し、完了するとサーバーが 1 ポイント加算する。
// 抽選で消費すると 0 に減る。

const ADMIN = 'http://localhost:8080/__admin'
const SCENARIO = 'earn-one-ticket'

const TASK_ID = 'earn-ticket-task'
const TASK_LIST = { method: 'GET', urlPath: '/todoist/api/v1/tasks/filter' }
const POINTS = { method: 'GET', urlPathPattern: '/reward/api/points/[^/]+' }
const CONSUME = { method: 'POST', urlPathPattern: '/reward/api/points/[^/]+/consume' }

function post(path, payload) {
  http.post(ADMIN + path, {
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

function stub(mapping) {
  post('/mappings', mapping)
}

function pointsBody(available) {
  return {
    status: 200,
    jsonBody: {
      user_id: 'mock-user-id',
      total_points: 1,
      available_points: available,
      task_count: 1,
    },
  }
}

post('/reset', {})

// 獲得前: タスクが1件、ポイントは0
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'Started',
  request: TASK_LIST,
  response: { status: 200, jsonBody: { results: [
    { id: TASK_ID, content: 'Earn Ticket', checked: false, due: { is_recurring: false } }] } },
})
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'Started',
  request: POINTS,
  response: { status: 200, jsonBody: {
    user_id: 'mock-user-id', total_points: 0, available_points: 0, task_count: 0 } },
})

// 完了を受けたら状態を進める
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'Started',
  newScenarioState: 'earned',
  request: { method: 'POST', urlPath: '/todoist/api/v1/tasks/' + TASK_ID + '/close' },
  response: { status: 204 },
})

// 獲得後: タスクは消え、ポイントが 1
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'earned',
  request: TASK_LIST,
  response: { status: 200, jsonBody: { results: [] } },
})
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'earned',
  request: POINTS,
  response: pointsBody(1),
})

// 抽選で消費すると 0 に減る
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'earned',
  newScenarioState: 'spent',
  request: CONSUME,
  response: pointsBody(0),
})
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'spent',
  request: TASK_LIST,
  response: { status: 200, jsonBody: { results: [] } },
})
stub({
  scenarioName: SCENARIO,
  requiredScenarioState: 'spent',
  request: POINTS,
  response: pointsBody(0),
})
