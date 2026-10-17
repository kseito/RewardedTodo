// Todoistが未完了タスクを1件だけ配信する状態を作る。
// アプリ内にTodo追加の導線が無いため、編集・削除フローはこのスタブでTodoを用意する。

const ADMIN = 'http://localhost:8080/__admin'

// 前のフローが登録したスタブとシナリオ状態を持ち越さない
http.post(ADMIN + '/reset', {
  headers: { 'Content-Type': 'application/json' },
  body: '{}',
})

http.post(ADMIN + '/mappings', {
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    request: { method: 'GET', urlPath: '/todoist/api/v1/tasks/filter' },
    response: {
      status: 200,
      jsonBody: {
        results: [
          { id: 'synced-task', content: 'Synced Task', checked: false, due: { is_recurring: false } },
        ],
      },
    },
  }),
})

// Echo the exact selected amount; a long swipe can move more than one step.
http.post(ADMIN + '/mappings', {
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    request: { method: 'PUT', urlPath: '/reward/api/task-rewards/mock-user-id/synced-task' },
    response: {
      status: 200,
      headers: { 'Content-Type': 'application/json' },
      body: `{ "todoist_task_id": "synced-task", "points_per_completion": {{jsonPath request.body '$.points_per_completion'}}, "version": 1 }`,
      transformers: ['response-template'],
    },
  }),
})
