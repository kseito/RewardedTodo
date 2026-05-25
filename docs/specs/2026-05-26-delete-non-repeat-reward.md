# 非繰り返し報酬の当選時自動削除 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Approved |
| 作成日 | 2026-05-26 |
| ブランチ | feature/delete-non-repeat-reward |
| 関連Issue/PR | （なし） |

## 1. 背景・目的

`Reward` には「繰り返し取得可能か」を表す `needRepeat` フラグが存在するが、抽選フローでは未使用になっている。
本来 `needRepeat=false`（1回限りの報酬）は一度当選したら消費され、再度抽選で当たらないようにすべき。
本機能では、RewardListScreen での抽選で当選した報酬の `needRepeat` を確認し、OFFの場合はその報酬を削除して一覧から消えるようにする。

## 2. 要件

### 機能要件
- 単発抽選（`startLottery`）で報酬に当選したとき、当選報酬の `needRepeat` が `false` ならその報酬を削除する。
- 10連抽選（`startBatchLottery`）で当選した報酬のうち、`needRepeat` が `false` のものを削除する。同一報酬が複数回当選した場合も、削除は1回（重複排除）で行う。
- `needRepeat=true` の報酬は当選しても削除されず、一覧に残る（従来どおり）。
- ハズレ（`Ticket.Miss`）の場合は何も削除しない。
- チケット消費に失敗した場合（`LackOfTicketsException`）は抽選自体が成立しないため、削除も行わない。

### 非機能要件 / 制約
- 削除は当選報酬の `Result` 返却前に行い、`getRewardsUseCase.executeAsFlow()` 経由で一覧が自動更新される（追加のUI更新処理は不要）。
- 既存の抽選結果ダイアログ（当選報酬名の表示・10連結果集計）の表示内容は変更しない。当選した事実は従来どおり表示され、その後に一覧から消える挙動とする。

## 3. 画面・UX

- 対象画面: `RewardListScreen`（`RewardListViewModel` 経由）
- UI変更点: なし（Composable・ダイアログの変更なし）。表示後に一覧の `StateFlow` が更新されることで報酬が消える。
- 操作フロー:
  1. ユーザーが単発/10連の抽選ボタンを押す
  2. `needRepeat=false` の報酬に当選する
  3. 当選ダイアログ（または10連結果ダイアログ）が表示される
  4. ダイアログを閉じると、その報酬が一覧から消えている

## 4. ドメインへの影響

`docs/domain-model.md` 参照。

- 関係するエンティティ / Value Object: `Reward`（`needRepeat: Boolean`）, `RewardCollection`, `Ticket`
- 新規追加・変更するルール:
  - 「`needRepeat=false` の報酬は当選すると消費（削除）される」という抽選時のビジネスルールを application 層（Interactor）に追加する。
  - これは Todo の `isRepeat=false` で完了時に削除する既存パターンと整合する。
- 既知の制約（10連抽選）: 抽選プール（`LotteryBox`）は抽選開始時に `RewardCollection` から生成されるため、同一の非繰り返し報酬が同一バッチ内で複数回当選しうる。当選回数の集計（`BatchLotteryResult.wonRewards`）は従来どおり複数回分カウントされるが、削除は当選報酬を重複排除して実施する。

## 5. レイヤー別の変更方針

`docs/module-dependency.md` の依存ルールを遵守。`IRewardRepository` は `domain:reward` にあり、`application:reward` は既に `domain:reward` に依存しているため新規モジュール依存は発生しない。

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Application | application/reward | `LotteryInteractor` / `BatchLotteryInteractor` に `IRewardRepository` を注入し、当選報酬が `needRepeat=false` なら削除する処理を追加 |
| DI | app/di/reward | 変更なし（両Interactorは `@Inject constructor` で `@Binds` 済み、`IRewardRepository` は `SingletonComponent` で提供済みのため自動解決される） |

> Domain / Data / Feature 層の変更はなし。

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] 単発抽選で `needRepeat=false` の報酬に当選すると、`rewardRepository.delete(reward)` が呼ばれる
- [ ] 単発抽選で `needRepeat=true` の報酬に当選しても削除は呼ばれない
- [ ] 単発抽選でハズレた場合は削除が呼ばれない
- [ ] 10連抽選で `needRepeat=false` の報酬が当選すると削除される（同一報酬の複数当選時は1回だけ削除）
- [ ] 10連抽選で `needRepeat=true` の報酬は削除されない
- [ ] チケット不足で `LackOfTicketsException` が返る場合は削除が呼ばれない
- [ ] 既存のユニットテスト（当選/ハズレ/チケット不足）が引き続き通る

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `LotteryInteractorTest`・`BatchLotteryInteractorTest` に削除呼び出しの検証ケースを追加（needRepeat ON/OFF × 当選/ハズレ）。`IRewardRepository` をMockKでモック化 |
| Roborazzi | なし（UI変更なし） |
| Maestro E2E | `maestro-tests/single-lottery-flow.yaml` を参考に、`needRepeat=OFF` の報酬を作成→当選確率100%で当選→一覧から消えることを検証するフローを追加（既存フローは確率40%のため、新規フロー `delete-non-repeat-reward-flow.yaml` を作成） |

## 8. 未決事項・リスク

- 10連抽選で同一の非繰り返し報酬が複数回当選した場合、結果ダイアログ上は複数回当選した表示になるが、実体は1つしかないため削除は1回。表示と在庫の不整合をユーザーが目にする可能性があるが、本仕様では当選表示は従来どおりとし、削除のみを重複排除する方針とする（抽選プールから動的に除外する変更は本スコープ外）。
</content>
</invoke>