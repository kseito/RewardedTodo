# リリース手順

デバッグAPKをGitHub Releaseとして配布する手順と、バージョン番号の付け方をまとめる。

## 配布経路の現状

現在、動くアプリを外部に渡す経路は **GitHub Release に debug APK を添付する1つだけ**である。

| 経路 | 状態 |
|------|------|
| GitHub Release（debug APK） | 運用中。`.github/workflows/release-debug-apk.yml` |
| Google Play | 未整備 |
| release ビルドの配布 | 未整備（署名設定が無く、`assembleRelease` は `app-release-unsigned.apk` を出力する） |

## デバッグAPKをリリースする

### 手順

1. 配布したいコミットが `main` に入っていることを確認する。
2. GitHub の **Actions > Release Debug APK > Run workflow** を開く。
3. **Use workflow from** で `main` を選ぶ。タグは選択したブランチの先頭コミットに付くため、ここを間違えると `main` に無いコミットが配布される。
4. **bump** で上げる桁を選ぶ（`patch` / `minor` / `major`、既定は `patch`）。
5. 実行する。完了すると GitHub Release に `app-debug.apk` が添付され、リリースノートが自動生成される。

### ワークフローがやっていること

| ステップ | 内容 |
|---------|------|
| タグ採番 | `git ls-remote --tags` で `debug-x.y.z` 形式のタグを集め、最大のものを `bump` の桁だけ進める。1つも無ければ `0.1.0` から始める |
| 重複チェック | 採番したタグが既に存在すれば異常終了する（リリース未作成のタグも検出する） |
| ビルド | `local.properties` に `REWARD_SERVER_URL` を注入し、`debug.keystore` を配置してから `./gradlew assembleDebug` |
| リリース作成 | `gh release create <tag> app/build/outputs/apk/debug/app-debug.apk --generate-notes --target <実行時のSHA>` |

タグ採番とリリース作成の間に別の実行が割り込むと同じ番号を取り合うため、`concurrency` グループ `release-debug-apk` で直列化している（`cancel-in-progress: false`）。

## バージョニング規則

バージョン番号は**2系統あり、現状は連動していない**。

| 系統 | 実体 | 現在値 | 誰が更新するか |
|------|------|-------|--------------|
| リリースタグ | Gitタグ `debug-x.y.z` | `debug-0.1.2` | Release Debug APK ワークフローが自動採番 |
| アプリバージョン | `gradle/libs.versions.toml` の `versionName` / `versionCode` | `0.0.3` / `1` | 手動。現在は更新されていない |

### タグの採番

`debug-` 接頭辞付きの `x.y.z` のみが採番対象。桁の使い分けは以下を目安とする。

- `patch`: バグ修正・内部変更のみ
- `minor`: 機能追加
- `major`: 破壊的変更（データ移行を伴う変更など）

リポジトリには `0.0.2` / `0.0.3` や `day1` / `first-tag` といった古いタグも残っているが、これらは旧運用の名残であり採番の対象外である。

## 関連ドキュメント

- [`docs/setup.md`](setup.md) - 開発環境セットアップ
- [`docs/oauth/README.md`](oauth/README.md) - Todoist OAuth と Digital Asset Links の運用
