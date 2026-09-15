---
# MADR Minimal の front matter。任意項目は削除してよい。
status: "proposed | rejected | accepted | deprecated | superseded by [ADR-0002](0002-example.md)"
date: YYYY-MM-DD # 最後に更新した日
decision-makers: 決定に関わった人
---

# <短いタイトル: 解いた課題と採った解決策がわかるもの>

<!--
本リポジトリのADRは MADR (Markdown Any Decision Records) の Minimal template に従う。
https://adr.github.io/madr/

- ファイル名は `NNNN-title-with-dashes.md`（連番4桁 + 英小文字ケバブケース）
- 見出し（Context and Problem Statement / Considered Options / Decision Outcome）は
  MADR の英語表記をそのまま使い、本文は日本語で書く
- 必要なら MADR の full template にある任意セクション
  （`### Consequences`、`## Decision Drivers`、`## Pros and Cons of the Options` 等）を追加してよい
-->

## Context and Problem Statement

何を決める必要があったか。前提となる状況・制約を2〜3文で。
関連するIssue/PR・コード上の該当箇所へのリンクがあれば添える。

## Considered Options

* <選択肢1のタイトル>
* <選択肢2のタイトル>
* <選択肢3のタイトル>

## Decision Outcome

Chosen option: "<選択肢1のタイトル>", なぜならば<採用理由>。

### Consequences <!-- 任意セクション。トレードオフや再評価条件があれば書く -->

* 受け入れたリスク・制約
* 再評価が必要になる条件（前提が崩れる条件）
