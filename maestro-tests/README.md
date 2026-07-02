# Maestro UIテスト

[Maestro](https://docs.maestro.dev/) による E2E UIテスト。全機能の正常系を網羅する。

## 実行方法

```bash
# デバッグアプリをインストール
./gradlew installDebug

# 全フロー実行
maestro test maestro-tests/

# 単一フロー実行
maestro test maestro-tests/add-todo-flow.yaml
```

> **警告**: 各フローは `clearState` でアプリデータ（Todo・報酬・チケット・APIトークン）を全消去する。
> 実機の `jp.kztproject.rewardedtodo.debug` に残したいデータがある場合は事前にバックアップすること。

## テスト一覧

### Todo

| フロー | 検証内容 |
|-------|---------|
| `add-todo-flow` | Todoを追加できる |
| `add-todo-with-multiple-tickets-flow` | チケット枚数を増やしてTodoを追加できる |
| `edit-todo-flow` | 既存Todoのタイトルとチケット枚数を変更できる |
| `complete-todo-flow` | Todo完了でリストから消え、チケットを獲得する |
| `delete-todo-flow` | Todoを削除できる |

### Reward

| フロー | 検証内容 |
|-------|---------|
| `add-reward-flow` | 報酬を追加できる |
| `edit-reward-flow` | 既存報酬のRepeatフラグを変更でき、一覧にRepeatアイコンが表示される |
| `delete-reward-flow` | 報酬を削除できる |

### 抽選（Lottery）

| フロー | 検証内容 |
|-------|---------|
| `single-lottery-flow` | 単発抽選でチケットが1枚消費される |
| `batch-lottery-flow` | 10連抽選で結果ダイアログが表示され、チケットが減る |
| `batch-lottery-insufficient-tickets-flow` | チケット不足時にエラーが表示される |
| `delete-non-repeat-reward-flow` | 非リピート報酬は当選後にリストから消える |
| `keep-repeat-reward-flow` | リピート報酬は当選後もリストに残る |

### 設定（Setting）

| フロー | 検証内容 |
|-------|---------|
| `setting-todoist-token-flow` | Todoist APIトークンの接続・解除ができる |

### 未カバー（意図的に対象外）

- **Todoist同期（Pull-to-Refresh）**: 実際のTodoistアカウントとネットワークが必要なため対象外
- **タブ切り替え**: 各フロー内でTodo⇔Reward間の遷移を暗黙的に検証済み

## テスト作成時の注意

- **セレクタはテキスト/contentDescription優先**。座標（`point`）指定は以下のやむを得ない箇所のみ:
  - 設定画面の歯車アイコン（contentDescription未設定）: `point: "93%,9%"`
- **ロケール依存の文言**: settingモジュールのみ `values-ja` があるため、正規表現で日英両対応にする（例: `"Not Connected|未接続"`）
- **日本語IME端末でのテキスト入力**: パスワード系フィールド（トークン入力欄）は入力がIMEのローマ字変換を通るため、小文字・数字は全角化される。大文字A-Fのみのトークンを使うこと
- **テストを通すためにアプリ側のソースコードを修正しない**
