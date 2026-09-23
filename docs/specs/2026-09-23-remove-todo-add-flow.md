# アプリ内Todo追加導線の削除 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Draft |
| 作成日 | 2026-09-23 |
| ブランチ | feature/remove-todo-add-flow |
| 関連Issue/PR | Issue: #938 / 前提PR: #934 (Todoist認証の必須化) |

## 1. 背景・目的

Todo一覧の「Add」から作ったTodoは `todoistId` を持たないため、完了してもTodoist APIへ通知されず、サーバーのWebhookによるチケット加算が起きない。さらに次の同期で黙って消える。UI上は他のTodoと区別が付かず、ユーザーから見ると「完了したのにチケットが増えない」導線になっている。

Todoist連携はすでにアプリ利用の前提（#934）なので、Todoの追加はTodoist側で行うものと割り切り、アプリ内の追加導線を削除する。これにより `todoistId` を持たないTodoがローカルに生まれる経路が無くなる。

## 2. 要件

### 機能要件

- Todo一覧からTodo追加用のFloatingActionButtonを削除する
- TodoDetailBottomSheetは既存Todoの編集専用にする（新規作成モードを削除）
- 既存Todoのタイトル・チケット枚数の編集、および削除は従来どおり動作する
- Todo一覧が空のときは、Todoist側でタスクを作る導線であることが伝わる文言を表示する

### 非機能要件 / 制約

- Todoist APIへの書き込み（タスク作成）は行わない。本変更はUI導線の削除のみで、同期方向は「Todoist → アプリ」の片方向を維持する
- `TodoRepository.update()` / `delete()` などデータ層のAPIは変更しない

## 3. 画面・UX

- 対象画面: `TodoListScreen`（`feature/todo`）、`TodoDetailBottomSheet`
- UI変更点:
  - `TodoListContent` からFAB（`Icons.Rounded.Add`）を削除
  - `TodoDetailBottomSheet` の `todo` 引数を `Todo?` から `Todo` に変更し、新規作成時の初期値設定（`title = ""` / チケット1枚）を削除
  - `TodoListScreenWithBottomSheet` の `showBottomSheet: Boolean` を廃止し、`selectedTodo != null` でシートの表示を決める
  - 空状態の文言 `todo_empty_message` を "No tasks yet" から、Todoistでタスクを作るよう促す文言に変更する
- 操作フロー:
  1. Todoistで作ったタスクが一覧に同期される
  2. 一覧の項目をタップするとBottomSheetが開き、タイトルとチケット枚数を編集できる
  3. チェックボックスで完了するとTodoist APIへ通知され、サーバーがチケットを加算する

## 4. ドメインへの影響

- 関係するエンティティ / Value Object: `Todo`, `EditingTodo`
- 新規追加・変更するルール:
  - `EditingTodo` は「既存Todoの編集中の状態」のみを表すようになる。`id: Long?` を `id: Long` に変更し、未保存を表すnullを廃止する（`toTodo()` の `id ?: 0` フォールバックも削除）
  - `Todo.todoistId` は実質的に常に非nullになるが、Room Entityとマイグレーションに波及するため本変更では `String?` のまま残す（§8参照）

## 5. レイヤー別の変更方針

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Domain | domain/todo | `EditingTodo.id` を非null化し、`toTodo()` のフォールバックを削除 |
| Feature | feature/todo | `TodoListScreen` からFABと `onTodoAddClicked` を削除。`TodoDetailBottomSheet` を編集専用にし、`showBottomSheet` を廃止。新規作成用Previewを削除。`strings.xml` の空状態文言を更新 |

Application / Data / DI レイヤーの変更はない。

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] Todo一覧にFloatingActionButtonが表示されない
- [ ] Todo一覧の項目をタップするとBottomSheetが開き、そのTodoのタイトルとチケット枚数が初期表示される
- [ ] BottomSheetでタイトルとチケット枚数を変更してSaveすると、一覧に反映される
- [ ] BottomSheetのDeleteで一覧から削除できる
- [ ] Todo一覧が空のとき、Todoistでタスクを作るよう促す文言が表示される
- [ ] `todoistId` を持たないTodoが新たに作られる経路がコード上に存在しない

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `EditingTodoTest`（`id` 非null化に追従）、`TodoListViewModelTest`（既存のまま通ること） |
| Roborazzi | `TodoListContent*` 系PreviewからFABが消えるためゴールデン画像を再記録。`TodoDetailBottomSheetContentPreview`（新規作成状態）は削除 |
| Maestro E2E | `add-todo-flow.yaml` / `add-todo-with-multiple-tickets-flow.yaml` を削除。`edit-todo-flow.yaml` / `delete-todo-flow.yaml` はアプリ内追加でTodoを用意していたため、Todoistから同期したタスクを使う形に書き換える（新規スタブ `stubs/one-todo.js` を追加）。`maestro-tests/README.md` のフロー一覧を更新 |

## 8. 未決事項・リスク

- Todoist由来のTodoをBottomSheetから削除しても、削除されるのはローカルDBのみで、次の同期で復活する。本Issueの対象外だが、追加導線が無くなったことで「削除」の意味が分かりにくくなるため、別Issueで扱う
- `Todo.todoistId` / `TodoEntity.todoistId` の非null化はRoomのマイグレーションを伴うため本変更には含めない
- `EditingTodo` は編集専用になったため名称の再検討余地があるが、リネームは影響範囲が広いので見送る

## 9. ドキュメント更新

- 新規: 本仕様書
- 更新: `docs/domain-model.md`（Todoの生成元がTodoistのみになること、`EditingTodo` のバリデーション記述）、`maestro-tests/README.md`（Todoフロー一覧）
