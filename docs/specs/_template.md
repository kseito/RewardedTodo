# <機能名> 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Draft / Approved / Implemented |
| 作成日 | YYYY-MM-DD |
| ブランチ | feature/xxx |
| 関連Issue/PR | （あれば） |

## 1. 背景・目的

なぜこの機能が必要か。解決したい課題やユーザー価値を1〜3文で。

## 2. 要件

### 機能要件
- （箇条書きで「何ができるようになるか」）

### 非機能要件 / 制約
- （パフォーマンス・既存仕様との整合・対応端末など。なければ「特になし」）

## 3. 画面・UX

- 対象画面: （例: RewardListScreen）
- UI変更点: （追加・変更するコンポーネント）
- 操作フロー: （ユーザーの操作手順を箇条書き）

## 4. ドメインへの影響

`docs/domain-model.md` を参照し、関係するドメインモデル・ビジネスルールを記載する。

- 関係するエンティティ / Value Object:
- 新規追加・変更するルール:

## 5. レイヤー別の変更方針

`docs/module-dependency.md` の依存ルールを守ること。変更しないレイヤーは行ごと削除してよい。

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Domain | domain/xxx | エンティティ / リポジトリIF |
| Application | application/xxx | UseCase IF + Interactor |
| Data | data/xxx | Repository実装 / DAO / API |
| Feature | feature/xxx | ViewModel / Compose Screen |
| DI | app/di/xxx | `@Binds` 追加 |

## 6. 受け入れ条件 (Acceptance Criteria)

PRのDoneを判定する条件。テスト・動作確認の対象になる。

- [ ] （観測可能な条件で記述する）
- [ ]

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | （Interactor / ViewModel など） |
| Roborazzi | （対象Composable。UI変更がなければ「なし」） |
| Maestro E2E | （`maestro-tests/<name>-flow.yaml`。新規フローか既存更新か） |

## 8. 未決事項・リスク

- （設計判断が必要な点、後続で対応する事項。なければ「特になし」）
