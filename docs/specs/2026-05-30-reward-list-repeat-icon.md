# 報酬一覧の繰り返しアイコン表示 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Draft |
| 作成日 | 2026-05-30 |
| ブランチ | feature/reward-list-repeat-icon |
| 関連Issue/PR | #719（needRepeat=false の当選時自動削除） |

## 1. 背景・目的

PR #719 で `needRepeat=false`（1回限り）の報酬は当選時に一覧から削除される挙動が入った。
しかし現状の `RewardItem` は `name` / `description` / `probability` のみを表示し、`needRepeat` のON/OFFを示すUIが存在しない。
そのためユーザーは「報酬が当選で消えた」事実は観測できるが、事前に「どれが1回限りか／繰り返しか」を判別できず、UXに分かりにくさが残る。
本機能では、`needRepeat=true`（繰り返し可能）の報酬にアイコンを表示し、視覚的に識別できるようにする。

## 2. 要件

### 機能要件
- `RewardListScreen` の `RewardItem` で、`reward.needRepeat == true` の場合のみリピートアイコンを表示する。
- `needRepeat == false`（1回限り）には何も追加しない（特別感を出す方針=B案）。
- アイコンは Material アイコン `Icons.Filled.Repeat` を使用する。
- アイコンは報酬名と同じ行の名前の右隣に、小さめのサイズ（headlineMediumのテキストに対して見やすい範囲）で表示する。
- アクセシビリティのため `contentDescription` を付与する（既存の `Icons.Filled.Add` / `Icons.Filled.Done` と同様にハードコード文字列。例: "Repeat"）。

### 非機能要件 / 制約
- 既存のレイアウト幅配分（`Column weight=8` / `Text(probability) weight=2`）は変更しない。
- アイコンの色は前景色から派生（例: `MaterialTheme.colorScheme.onSurfaceVariant`）として、報酬名を圧迫しない。
- 既存のクリック挙動（行タップでBottomSheet表示）は変更しない。

## 3. 画面・UX

- 対象画面: `RewardListScreen` 内の `RewardItem`
- UI変更点:
  - 報酬名の右にリピートアイコン（needRepeat=true時のみ）
- Preview への影響:
  - `RewardItemPreview` に needRepeat=true / false の両バリアントを用意する（Showkase経由でRoborazziのスクショテスト対象になる）。

操作フロー:
1. 報酬一覧を開く
2. needRepeat=true の項目は名前の右に 🔁 アイコンが見える
3. needRepeat=false の項目は無印で表示される

## 4. ドメインへの影響

`docs/domain-model.md` 参照。

- 関係するエンティティ / Value Object: `Reward.needRepeat: Boolean`
- 新規追加・変更するルール: なし（既存フラグの可視化のみ）。

## 5. レイヤー別の変更方針

`docs/module-dependency.md` 参照。表示変更のみのため Feature 層内に閉じる。

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Feature | feature/reward | `RewardItem` Composable にアイコン表示を追加（`Icons.Filled.Repeat` をimport）。`RewardItemPreview` に needRepeat=true / false の両バリアントを用意。文字列リソース追加なし（contentDescriptionはハードコード） |
| テスト | feature/reward | `RewardItemTest` を新規追加（Compose UI test、`createComposeRule` + `onNodeWithContentDescription`）。build.gradle.kts に Compose UI test 依存を追加 |
| テスト | app | 既存の `ShowkaseParameterizedTest` が新Preview を自動拾いするため、`./gradlew :app:recordRoborazziDebug` で needRepeat 各バリアントのゴールデン画像を追加 |

> Domain / Application / Data / DI 層の変更はなし。

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] `needRepeat=true` の報酬の `RewardItem` に Material のリピートアイコンが表示される
- [ ] `needRepeat=false` の報酬の `RewardItem` にはアイコンが表示されない
- [ ] アイコンに `contentDescription` が設定されている
- [ ] 既存のレイアウト幅（名前/説明 8、確率 2）と行タップ動作は変更されない
- [ ] Preview Composable（`RewardListPreview` / `RewardItemPreview`）がアイコン表示状態で正常にレンダリングできる

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `RewardItemTest`（Compose UI test）を新規追加。`createComposeRule()` + `onNodeWithContentDescription("Repeat")` で needRepeat=true 時にアイコン存在 / false 時に非存在を検証。Robolectric 配下で実行 |
| Roborazzi | app モジュールの既存 `ShowkaseParameterizedTest` が `@Preview` を自動キャプチャするため、`RewardItemPreview` の needRepeat=true / false 両バリアントについて `./gradlew :app:recordRoborazziDebug` でゴールデン画像を記録、`:app:compareRoborazziDebug` で回帰検知 |
| Maestro E2E | 新規フローは追加しない。既存 `delete-non-repeat-reward-flow.yaml` で needRepeat=false の挙動は担保済み |

## 8. 未決事項・リスク

- needRepeat=false（1回限り）には明示マークを置かない方針（B案）のため、初見ユーザーには「アイコンが付いている=繰り返し」というルールが伝わりにくい可能性がある。将来的にツールチップやオンボーディングで補完するのは別スコープ。
