# Toast の SnackBar 置き換え 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Draft |
| 作成日 | 2026-06-06 |
| ブランチ | feature/replace-toast-with-snackbar |
| 関連Issue/PR | （なし） |

## 1. 背景・目的

メッセージ表示の手段がアプリ内で混在している。`RewardListScreen` では報酬の抽選失敗・保存失敗をユーザーに通知しているが、保存失敗時のみ `Toast` を使い、抽選失敗時は既存の `SnackbarHost` を使っている。表示手段を SnackBar に統一することで、見た目・挙動の一貫性を保ち、Compose 管理下の UI に揃える。

## 2. 要件

### 機能要件
- `Toast` でメッセージを表示している箇所を SnackBar に置き換える。
- 置き換え後も、これまで Toast で表示していたのと同じエラーメッセージが表示されること。

### 非機能要件 / 制約
- 既存の SnackBar 表示（抽選失敗時）と同じ仕組み（`SnackbarHostState` / `ErrorSnackBar`）を再利用し、新たな表示機構を増やさない。
- ドメイン・データ層への影響はない（Feature層のUIのみの変更）。

## 3. 画面・UX

- 対象画面: `RewardListScreen`（`feature/reward`）
- UI変更点: 報酬保存失敗時の通知を `Toast.makeText(...).show()` から `snackbarHostState.showSnackbar(...)` に変更。
- 操作フロー:
  1. 報酬を保存（追加・更新）する。
  2. 保存がバリデーション等で失敗する。
  3. 画面上部の SnackBar（`ErrorSnackBar`）にエラーメッセージが表示される。

## 4. ドメインへの影響

UI表示手段の変更のみで、ドメインモデル・ビジネスルールへの影響はない。

- 関係するエンティティ / Value Object: なし
- 新規追加・変更するルール: なし

## 5. レイヤー別の変更方針

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Feature | feature/reward | `RewardListScreen.kt` の `result` 失敗時の Toast を SnackBar 表示へ変更。`android.widget.Toast` の import を削除。 |

- メッセージ文字列は、既存の SnackBar 実装に倣い、リソースIDから解決して `showSnackbar` に渡す。`LaunchedEffect` のコルーチン内のため、`context.getString(errorMessageId)` で文字列化する。

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] `RewardListScreen.kt` から `android.widget.Toast` の import と `Toast.makeText(...)` 呼び出しが消えている。
- [ ] 報酬保存失敗時に、既存の `SnackbarHostState` を通じて同じエラーメッセージが SnackBar で表示される。
- [ ] リポジトリ全体に `Toast` の使用箇所が残っていない。
- [ ] 既存のユニットテスト・Roborazzi が通る。

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | 既存の `feature/reward` テストが緑であること（ロジック変更はないため新規テストは追加しない） |
| Roborazzi | `RewardListScreen` 系のゴールデン画像に差分が出ないこと（既存表示の見た目は変えない） |
| Maestro E2E | なし（失敗系のSnackBar表示はバリデーション失敗の再現が必要なため、本スコープでは対象外） |

## 8. 未決事項・リスク

- 特になし。表示先（画面上部の `ErrorSnackBar`）は既存の抽選失敗時と同じものを再利用する。
