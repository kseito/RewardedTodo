# View SystemのAlertDialog削除 仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Implemented |
| 作成日 | 2026-06-13 |
| ブランチ | clean/remove-view-system-alertdialog |
| 関連Issue/PR | （なし） |

## 1. 背景・目的

UIはCompose（`material3.AlertDialog` をラップした `CommonAlertDialog`）への移行が完了しているが、View System時代の名残として `androidx.appcompat.app.AlertDialog` を使った `Fragment.showDialog` 拡張関数が `feature/reward` に残っている。どこからも呼ばれていないデッドコードであり、混乱の元になるため削除する。

## 2. 要件

### 機能要件
- `Fragment.showDialog` 拡張関数（View SystemのAlertDialogを表示する関数）を削除する。
- 関数のみを持つ `FragmentExtensions.kt` および空になる `helper` パッケージディレクトリを削除する。

### 非機能要件 / 制約
- 削除対象は未使用であること（プロダクション・テストの双方から参照されていないこと）を確認済み。
- アプリの挙動・UIには一切変化がないこと。

## 3. 画面・UX

- 対象画面: なし（UI変更なし）
- UI変更点: なし
- 操作フロー: なし

## 4. ドメインへの影響

なし（ドメインモデル・ビジネスルールに関係しない、Feature層のユーティリティ削除）。

## 5. レイヤー別の変更方針

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Feature | feature/reward | `helper/FragmentExtensions.kt` を削除（`Fragment.showDialog` 拡張関数） |

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] `androidx.appcompat.app.AlertDialog` を参照するコードがプロダクションコードから消えている。
- [ ] `feature/reward/.../helper/` ディレクトリが存在しない。
- [ ] `feature/reward` モジュールのビルドとユニットテストが成功する。

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `feature/reward` モジュールの既存テスト（リグレッション確認のみ。新規テストなし） |
| Roborazzi | なし（UI変更なし） |
| Maestro E2E | なし（UI変更なし） |

## 8. 未決事項・リスク

- 特になし。完全な未使用コードの削除のため、リスクは低い。