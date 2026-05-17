# Claude Code ハーネス構成 星取表

このプロジェクトで Claude Code 向けに整備されているハーネス（足場）の現状を、構成要素ごとに整理する。

凡例: **◯** 導入済み / **△** 部分的 / **✗** 未導入 / **➖** このプロジェクトには不要

## 1. コンテキスト層（Claude に渡す前提情報）

| 要素 | 状況 | 備考 |
|------|:----:|------|
| `CLAUDE.md`（ルート） | ◯ | アーキテクチャ・命名規則・ビルドコマンド |
| サブディレクトリ別 `CLAUDE.md` | ✗ | モジュール毎の文脈付与は未活用 |
| 専用ドキュメント `docs/` | ◯ | domain-model / how-to-add-new-feature / module-dependency / di-setup |
| 他 AI 向けドキュメント | ◯ | `GEMINI.md`, `.junie/guidelines.md` |
| ADR（Architecture Decision Records） | ✗ | 設計判断の履歴記録なし |

## 2. 設定層（settings）

| 要素 | 状況 | 備考 |
|------|:----:|------|
| `.claude/settings.json`（チーム共有） | ✗ | 未作成 |
| `.claude/settings.local.json`（個人） | ◯ | 51 項目 |
| `permissions.allow` | ◯ | gradlew / gh / git / adb / MCP 等カバー |
| `permissions.deny` | △ | 空（明示的禁止なし） |
| `permissions.additionalDirectories` | ✗ | 別ディレクトリ参照許可なし |
| 環境変数 `env` | ✗ | プロジェクト固有の env なし |
| `statusLine` | ✗ | デフォルト |
| `model` デフォルト | ✗ | CLI デフォルト |

## 3. 拡張機能（プロジェクト固有の能力）

| 要素 | 状況 | 備考 |
|------|:----:|------|
| Slash Commands `.claude/commands/` | ✗ | ディレクトリ空 |
| Sub-agents `.claude/agents/` | ✗ | ディレクトリ非存在 |
| Skills `.claude/skills/` | ✗ | ディレクトリ非存在 |
| Output Styles | ✗ | デフォルトのみ |
| Plugins | ✗ | 未導入 |

## 4. Hooks（イベント駆動）

| 要素 | 状況 | 想定用途 |
|------|:----:|------|
| `PreToolUse` | ✗ | git commit 前の spotlessCheck 等 |
| `PostToolUse` | ✗ | Edit 後の自動 format 等 |
| `UserPromptSubmit` | ✗ | プロンプトに context 自動注入 |
| `SessionStart` | ✗ | ブランチ・CI 状況の表示 |
| `Stop` | ✗ | 終了時のリマインダー |
| `SubagentStop` | ✗ | サブエージェント結果の後処理 |
| `Notification` | ✗ | 通知カスタマイズ |
| `PreCompact` | ✗ | 文脈圧縮前の重要情報保存 |

## 5. MCP（外部システム連携）

| 要素 | 状況 | 備考 |
|------|:----:|------|
| プロジェクト `.mcp.json` | ✗ | チーム共有 MCP 設定なし |
| Android MCP | ◯ | adb 操作・スクリーンショット（ユーザー設定経由） |
| Todoist MCP | ◯ | アプリドメインと一致 |
| 他カスタム MCP | ✗ | Gradle / Roborazzi 専用 MCP 等は未検討 |

## 6. 品質ゲート（Claude と独立した検証）

| 要素 | 状況 | 備考 |
|------|:----:|------|
| GitHub Actions CI | ◯ | 9 workflows |
| spotless / detekt 設定 | ◯ | gradle plugin 導入済み |
| Git pre-commit hooks（lefthook / husky） | ✗ | CI 失敗で初めて発覚 |
| Conventional Commits 自動強制 | ✗ | CLAUDE.md で言及のみ |
| commit-msg 検証 | ✗ | なし |

## 7. 自動化・スケジューリング

| 要素 | 状況 | 備考 |
|------|:----:|------|
| `/schedule`（cron 定期実行） | ✗ | 未活用 |
| `/loop`（自己ペース反復） | ✗ | 利用場面なし |
| Background tasks | ➖ | アドホック使用は可 |
| Remote triggers | ✗ | 外部起動なし |

## 8. メモリ・永続化

| 要素 | 状況 | 備考 |
|------|:----:|------|
| ファイルメモリ（`~/.claude/projects/.../memory/`） | △ | 仕組みはあるが蓄積実績未確認 |
| プラン履歴（`~/.claude/plans/`） | ◯ | 自動蓄積 |
| メモリ運用ルール明文化 | ✗ | 命名規則・トリガーなし |

## 9. ユーザーレベルで既に使える資産（プロジェクト共通）

| 要素 | 状況 | 備考 |
|------|:----:|------|
| `implement` skill | ◯ | feature ブランチ + 小コミット |
| `maestro-e2e` skill | ◯ | E2E テスト作成 |
| `fix-ci` skill | ◯ | CI 失敗の自動診断 |
| `review` / `security-review` skill | ◯ | PR レビュー |
| `simplify` skill | ◯ | コード簡略化 |
| `claude-api` / `blog-writer` 等 | ➖ | このプロジェクトと無関係 |

---

## サマリー

**強い領域**
- コンテキスト層（CLAUDE.md + docs/ 4 本）
- CI/CD と Lint/Format 設定
- permissions allowlist

**弱い領域**
- Hooks ゼロ
- Commands / Agents / Skills ゼロ（プロジェクト固有）
- 共有 `settings.json` なし
- git pre-commit hooks なし
- ADR / メモリ運用ルールなし
