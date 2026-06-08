# モジュール依存関係

## モジュール一覧

| モジュール | 役割 |
|-----------|------|
| `app` | エントリーポイント、Hilt DIモジュールの集約 |
| `feature:reward` | 報酬画面（ViewModel + UI） |
| `feature:todo` | Todo画面（ViewModel + UI） |
| `feature:setting` | 設定画面（ViewModel + UI） |
| `application:reward` | 報酬ユースケース（インターフェース + Interactor） |
| `application:todo` | TodoユースケースとTodoistトークン管理 |
| `domain:reward` | 報酬エンティティ、リポジトリインターフェース |
| `domain:todo` | Todoエンティティ、リポジトリインターフェース |
| `data:reward` | 報酬リポジトリ実装（Room DB） |
| `data:todo` | Todoリポジトリ実装（Room DB + Todoist API） |
| `data:ticket` | チケット（ポイント）リポジトリ実装（DataStore + Reward Server API） |
| `data:todoist` | Todoist APIクライアント |
| `common:kvs` | DataStore / 暗号化ストレージ共通基盤 |
| `common:database` | Room DB共通基盤 |
| `common:ui` | 共通UIコンポーネント |
| `test:reward` | テスト用ダミーデータ（DummyCreator） |

---

## 依存関係マップ

```
app
├── feature:reward ──────────── application:reward ── domain:reward
│                  └────────── domain:reward          └── (no deps)
│                  └────────── common:ui
│
├── feature:todo ────────────── application:todo ───── domain:todo
│                  └────────── domain:todo             └── (no deps)
│                  └────────── common:ui
│
├── feature:setting ─────────── application:todo
│                  └────────── domain:todo
│
├── data:reward ─────────────── domain:reward
├── data:todo ───────────────── domain:todo
│                  └────────── common:database
│                  └────────── common:kvs
│                  └────────── data:todoist ────────── domain:todo
│
├── data:ticket ─────────────── domain:reward
│                  └────────── common:kvs
│
├── application:reward ──────── domain:reward
│
├── application:todo ────────── domain:todo
│                  └────────── domain:reward
│
├── common:kvs ──────────────── (依存なし)
├── common:database ─────────── (依存なし)
└── common:ui ───────────────── (依存なし)
```

---

## 依存の方向ルール

```
feature → application → domain
data → domain
feature → domain (直接参照も可)
app → 全モジュール (DIバインドのため)
```

**禁止されている依存方向**
- `domain` → 他モジュール（Androidフレームワーク依存も禁止）
- `feature` → `data`（featureがdata実装に直接依存してはいけない）
- `application` → `data`（applicationはdomain層のインターフェース経由でdataを利用する）

---

## common モジュールの使われ方

| モジュール | 主な利用者 |
|-----------|----------|
| `common:kvs` | `data:todo`, `data:ticket` — DataStore アクセス |
| `common:database` | `data:todo` — Room DB 基盤 |
| `common:ui` | `feature:reward`, `feature:todo` — 共通UIコンポーネント |
