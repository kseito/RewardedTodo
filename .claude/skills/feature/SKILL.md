---
name: feature
description: 仕様プロンプトから「仕様書作成→実装→動作確認→Draft PR作成」までを一貫実行する開発プロセス。/feature <仕様> で呼び出して使用
user-invocable: true
---

# 機能開発オーケストレーションスキル (/feature)

自然言語の仕様を受け取り、**仕様書 → 実装 → 動作確認 → Draft PR** までを一貫して進める。
このスキルは新しい規約を作らず、既存の資産（`docs/` と `/implement` `/maestro-e2e` `/code-review`）を束ねるオーケストレーターとして振る舞う。

## 入力

`/feature` に続けて渡された自然言語の仕様プロンプト（例: `/feature 報酬一覧に検索機能を追加`）。
プロンプトが曖昧で仕様書が書けない場合は、Phase 1の前に AskUserQuestion で必要最小限だけ確認する。

## 全体フロー（5フェーズ / 承認ゲート2箇所）

```
Phase 0 準備      ─ ブランチ作成・ドキュメント読込・タスク登録
Phase 1 仕様書作成 ─ docs/specs/ に仕様書を作成
        ★ゲート1: 仕様書をユーザーがレビュー・承認するまで停止
Phase 2 実装      ─ /implement のコミット規約でレイヤー順に実装
Phase 3 動作確認   ─ ユニットテスト / Roborazzi / Maestro E2E
Phase 4 品質チェック ─ spotless / lint・仕様書ステータス更新
        ★ゲート2: 変更サマリをユーザーが承認するまで停止
Phase 5 PR作成    ─ push して Draft PR を作成
```

**進捗管理**: 開始時に TaskCreate で5フェーズをタスク化し、各フェーズを `in_progress` / `completed` に更新する。

---

## Phase 0: 準備

1. 関連ドキュメントを読む（実装方針の根拠になる）:
   - `docs/domain-model.md` — ドメインモデルとビジネスルール
   - `docs/module-dependency.md` — モジュール依存と禁止ルール
   - `docs/how-to-add-new-feature.md` — レイヤー順の追加手順
   - `docs/di-setup.md` — Hilt DI構成
2. ブランチを確認する。`main` または非featureブランチにいる場合は、仕様から導いたスラッグで `feature/<slug>` を作成して切り替える（例: `feature/reward-search`）。すでにfeatureブランチ上なら、その意図がこの仕様と一致するか一言確認する。
3. 仕様プロンプトを分析し、影響範囲（変更するモジュール・レイヤー）を Grep/Glob で特定する。

---

## Phase 1: 仕様書作成

1. `docs/specs/_template.md` を雛形として読む。
2. `docs/specs/<YYYY-MM-DD>-<slug>.md` を作成する（日付は今日、slugはブランチ名と揃える）。
   - 仕様書はテンプレートの全セクションを埋める。特に **5. レイヤー別の変更方針** と **6. 受け入れ条件** は後工程の根拠になるため具体的に書く。
   - 既存コードを調査し、参照すべき既存ファイル（類似UseCase・ViewModelなど）を明記する。
3. 仕様書を1コミットで追加する: `Add <機能名>の仕様書`
4. **★承認ゲート1**: 仕様書の要点（目的・要件・レイヤー別変更方針・受け入れ条件）を要約してユーザーに提示し、AskUserQuestion で承認を求める。
   - 修正要望があれば仕様書に反映して再提示する。
   - **承認されるまで Phase 2 に進まない。** 仕様書のステータスを `Approved` に更新してから進む。

---

## Phase 2: 実装

`/implement` スキルのガイドラインに**厳密に**従う（細粒度コミット・プレフィックス規約）。要点:

- **1ファイル1コミット**を原則とし、機能実装・DI設定・テスト・spotlessは必ず別コミットにする。
- コミットメッセージは日本語の動詞プレフィックス（`Add` / `Update` / `Remove` / `Fix` / `Clean` / `Rename`）。
- Clean Architecture のレイヤー順（Domain → Application → Data → Feature → DI）で積む。

実装の具体手順は `docs/how-to-add-new-feature.md` と `docs/di-setup.md` のチェックリストに従うこと。
仕様書の「レイヤー別の変更方針」を実装タスクの分解単位として使う。

---

## Phase 3: 動作確認

仕様書の「テスト方針」と「受け入れ条件」に沿って、以下を実行する。失敗は**別コミット**で `Fix` する。

1. **ユニットテスト**: 変更したモジュールに対して `./gradlew :<module>:testDebugUnitTest` を実行。
   - 新規Interactor/ViewModelにはテストを追加する（`Add <Xxx>Test` / `Update テストを追加`）。テストデータは `test/` の `DummyCreator` を活用。
2. **Roborazzi（UI変更がある場合のみ）**: `./gradlew compareRoborazziDebug` で差分を確認。
   - 差分が仕様通りの意図的な変更なら `./gradlew recordRoborazziDebug` でゴールデン画像を更新し、別コミットで記録する。
3. **Maestro E2E**: `/maestro-e2e` スキルを使い、`maestro-tests/<name>-flow.yaml` に受け入れ条件を検証するフローを追加（または既存フローを更新）して実行する。
   - `appId` は `jp.kztproject.rewardedtodo.debug`。既存フロー（例: `single-lottery-flow.yaml`）の書き方に倣う。

各確認の結果（pass/fail・実行コマンド）は Phase 5 のPR本文に転記できるよう記録しておく。

---

## Phase 4: 品質チェック

1. `./gradlew spotlessKotlinApply` を実行し、整形を**別コミット** `Clean spotless適用` で記録する。
2. `./gradlew lintDebug` を実行し、新規warningがあれば解消する。
3. 仕様書のステータスを `Implemented` に更新してコミットする。

---

## Phase 5: PR作成

1. **★承認ゲート2**: 次をまとめてユーザーに提示し、AskUserQuestion でPR作成の承認を求める。
   - コミット一覧（`git log --oneline main..HEAD`）
   - 動作確認の結果（ユニット / Roborazzi / Maestro）
   - 仕様書へのリンクと未決事項
   - **承認されるまで push しない。**
2. 承認後、ブランチを push する: `git push -u origin <branch>`
3. Draft PR を作成する: `gh pr create --draft`
   - タイトル: 仕様の機能名（Conventional Commits の `feat:` 等を使用）
   - 本文: 以下を含める
     - `## 概要` — 目的の要約
     - `## 仕様書` — `docs/specs/<file>.md` へのリンク
     - `## 受け入れ条件` — 仕様書のチェックリストを転記（達成済みはチェック）
     - `## 動作確認` — 実行したテストと結果
     - `🤖 Generated with [Claude Code](https://claude.com/claude-code)`
4. 作成されたPRのURLをユーザーに伝える。最終レビュー後にユーザー自身がReady化する旨を添える。

---

## 原則

- **承認ゲートでは必ず停止する。** ゲートを飛ばして先に進まない。
- コミットは `/implement` の細粒度規約を最優先する。「と」「および」を含むコミットは分割のサイン。
- 仕様にない実装上の判断が必要になったら、勝手に決めずユーザーに確認する。
- 各フェーズの根拠は `docs/` のドキュメントに置く。このスキルはそれらを参照するだけで規約を重複させない。