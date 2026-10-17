// Read the value sent by the UI and make the subsequent fetch return that saved setting.
const ADMIN = 'http://localhost:8080/__admin'
const response = http.post(ADMIN + '/requests/find', {
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ method: 'PUT', urlPath: '/reward/api/task-rewards/mock-user-id/synced-task' }),
})
const requests = JSON.parse(response.body).requests
const saved = JSON.parse(requests[requests.length - 1].body)
if (saved.points_per_completion < 2 || saved.points_per_completion > 100) {
  throw new Error('Unexpected reward update: ' + JSON.stringify(saved))
}
output.savedReward = String(saved.points_per_completion)
http.post(ADMIN + '/mappings', {
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    priority: 1,
    request: { method: 'GET', urlPath: '/reward/api/task-rewards/mock-user-id' },
    response: { status: 200, jsonBody: [{ todoist_task_id: 'synced-task', points_per_completion: saved.points_per_completion }] },
  }),
})
