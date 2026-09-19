# Maestro UIテスト

[Maestro](https://docs.maestro.dev/) による E2E UIテスト。全機能の正常系を網羅する。

## 実行方法

テストは**英語ロケール（en_US）のエミュレータ**で実行することを前提とする。
文言アサートは英語のみで書かれているため、日本語ロケールの端末では
setting 系フローが失敗する。

```bash
# 英語ロケールのエミュレータを起動（Maestro管理のエミュレータを作成・起動）
maestro start-device --platform android --device-locale en_US

# フェイクバックエンドを起動（詳細は wiremock/README.md）
bash .github/scripts/start-wiremock.sh

# フェイクを向いたデバッグアプリをインストール
./gradlew installDebug -PuseMockServer=true

# 全フロー実行
maestro test maestro-tests/

# 単一フロー実行
maestro test maestro-tests/add-todo-flow.yaml
```

各フローはTodoist連携済みを前提とする。`-PuseMockServer=true` でビルドすると認可がブラウザを介さず完了するため、`subflows/authenticate.yaml` を呼ぶだけで連携済みの状態を作れる。

`subflows/` と `stubs/` はフォルダ指定の実行対象に含まれない（Maestroはサブフォルダを再帰しない）。

英語ロケールであれば手元のAVDでもよい（`adb shell am get-config` で `en-rUS` を確認できる）。

> **警告**: 各フローは `clearState` でアプリデータ（Todo・報酬・チケット・APIトークン）を全消去する。
> 実機の `jp.kztproject.rewardedtodo.debug` に残したいデータがある場合は事前にバックアップすること。

## テスト一覧

### Todo

| フロー | 検証内容 |
|-------|---------|
| `add-todo-flow` | Todoを追加できる |
| `add-todo-with-multiple-tickets-flow` | チケット枚数を増やしてTodoを追加できる |
| `edit-todo-flow` | 既存Todoのタイトルとチケット枚数を変更できる |
| `complete-todo-flow` | Todoist由来のTodoを完了するとリストから消え、サーバーがチケットを加算する |
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

## フロー命名規約

- ファイル名は小文字のkebab-caseで、必ず `-flow.yaml` を末尾に付ける
- `-flow` より前には、検証するユーザー操作またはシナリオを簡潔に表す名前を付ける
- 通常フローの派生ケースは、対象フロー名の後ろに条件を追加する

例: `add-todo-flow.yaml`、`batch-lottery-insufficient-tickets-flow.yaml`、`keep-repeat-reward-flow.yaml`

## テスト作成時の注意

- **セレクタはテキスト/contentDescription優先**。座標（`point`）指定は画面サイズ・解像度に依存してFlakyになるため使わない
- **文言アサートは英語のみで書く**。ロケールは実行環境（en_USエミュレータ）側で固定する。Maestroのロケール指定（`--device-locale`）はエミュレータ専用で、実機やフローYAML内では指定できない
- **日本語IME端末（実機）で実行する場合の注意**: パスワード系フィールド（トークン入力欄）は入力がIMEのローマ字変換を通るため、小文字・数字は全角化される。トークンは大文字A-Fのみで構成している
- **テストを通すためにアプリ側のビジネスロジックやデザインを変更しない**。ただし、テストから要素を特定するための識別子（`contentDescription`）の追加は可（例: `repeat_checkbox`, `setting_button`）
