// 前のフローが登録したスタブとシナリオ状態を持ち越さない。
// 起動時にファイルから読み込まれた既定スタブは残る。
http.post('http://localhost:8080/__admin/reset', {
  headers: { 'Content-Type': 'application/json' },
  body: '{}',
})
