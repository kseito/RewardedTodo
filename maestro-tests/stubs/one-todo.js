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
