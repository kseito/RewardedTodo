# CI/CD ワークフロー一覧

`.github/workflows/` にある全14ワークフローの目的・トリガー・ゲート内容をまとめる。
[レビュー方針](review-policy.md) の「機械に任せる」を実際に担保しているのがこれらのワークフローであり、
fork や新しい開発環境で同じゲートを再現するために必要な Secrets もここに記載する。

---

## ワークフロー一覧

### PR で走るもの

| ワークフロー（表示名） | ファイル | トリガー | 目的・ゲート内容 |
|---|---|---|---|
| Build | `unit-test.yml` | PR（→ `main`） | `./gradlew testDebugUnitTest` で全モジュールのユニットテストを実行する。結果を artifact `test-results` に残す（失敗時も）|
| Android Lint | `android-lint.yml` | PR（全ブランチ宛） | `./gradlew lintDebug` |
| Detekt | `detekt.yml` | PR（全ブランチ宛） | `./gradlew detekt detektCustomRules`。`detekt-rules/` の独自ルールも併せて実行する |
| reviewdog-suggester | `reviewdog-suggester.yml` | PR（全ブランチ宛） | `spotlessKotlinApply` を実行し、生じた差分を reviewdog が修正提案コメントとして投稿する。最後に `spotlessKotlinCheck` で未整形を失敗として落とす |
| Gitleaks | `gitleaks.yml` | PR（全ブランチ宛）／ `main` への push | `fetch-depth: 0` で全履歴をスキャンし、コミットに混入したシークレットを検出する |
| screenshot-comparison | `screenshot-comparison.yml` | PR（→ `main` / `master` / `develop` / `feature/adjust-screenshot`）<br>`docs/**`・`**/*.md` のみの変更は除外 | base ブランチの基準画像（`upload_expected_image.yml` の artifact `expected_screenshots`）を取得し、`./gradlew compareRoborazziDebug` で VRT 差分を検出する。差分レポートを artifact `screenshot-diff-reports`、PR番号を artifact `pr` に残す |
| screenshot-comparison-comment | `screenshot-comparison-comment.yml` | `screenshot-comparison` の完了（`workflow_run`）<br>PR 起因かつ成功時のみ | 差分画像（`*_compare.png`）を orphan ブランチ `companion_<ブランチ名>` に push し、その raw URL を並べた比較結果コメントを PR に投稿・更新する。差分が無くなれば既存コメントを削除する。併せて、最終コミットから14日以上経過した `companion_` ブランチを削除する |
| maestro-e2e | `maestro-e2e.yml` | PR（→ `main`）<br>`docs/**`・`**/*.md` のみの変更は除外 | エミュレータ（API 34 / x86_64 / AOSP イメージ / `pixel_6` プロファイル）を起動し、`maestro-tests/` 配下のフローを Maestro 2.8.0 で一括実行する。実行結果を artifact `maestro-debug-output` に残す |
| vrt-coverage-check | `vrt-coverage-check.yml` | PR（全ブランチ宛） | `:app:jacocoVrtReport :app:jacocoVrtCoverageVerification` を実行し、VRT カバレッジが下限（INSTRUCTION 60%）を下回ったら失敗する。失敗時のみレポートを artifact `vrt-coverage-report` に残す |

### push / スケジュール / 手動で走るもの

| ワークフロー（表示名） | ファイル | トリガー | 目的・ゲート内容 |
|---|---|---|---|
| Upload expected image | `upload_expected_image.yml` | `main` / `master` への push | `./gradlew recordRoborazziDebug` で VRT の基準画像を生成し、artifact `expected_screenshots` として保存する。`screenshot-comparison` がこれを基準に比較するため、**このワークフローが成功していないと VRT 比較が成立しない** |
| vrt-coverage | `vrt-coverage.yml` | 週次 cron `0 0 * * 1`（月 09:00 JST）／手動実行 | `:app:jacocoVrtReport` でカバレッジレポートのみ生成し、artifact `vrt-coverage-report` に 90 日保持する。ゲートではなく推移の観測用 |
| CodeQL | `codeql.yml` | 週次 cron `0 2 * * 5`（金 11:00 JST） | `java-kotlin` を `security-extended` + `security-and-quality` クエリで解析し、結果を GitHub の Code scanning に送る |
| Release Debug APK | `release-debug-apk.yml` | 手動実行（入力 `bump`: `patch` / `minor` / `major`） | `debug-x.y.z` タグを採番し、debug APK を添付した GitHub Release を作成する。詳細は [リリース手順](release-process.md) を参照 |

### コメント起動

| ワークフロー（表示名） | ファイル | トリガー | 目的・ゲート内容 |
|---|---|---|---|
| Claude Code | `claude.yml` | Issue / PR レビューコメントの作成 | 本文に `@claude` を含み、かつ投稿者が `OWNER` / `MEMBER` / `COLLABORATOR` のときだけ Claude Code を起動する（公開リポジトリのため、第三者による API クレジット消費を防ぐガード）|

---

## マージをブロックするチェック

`main` のブランチ保護で必須チェックに設定されているのは以下の5件のみ。

| 必須チェック名 | 実体 |
|---|---|
| `build` | `unit-test.yml` |
| `Run Android Lint` | `android-lint.yml` |
| `Run Detekt` | `detekt.yml` |
| `Run Gitleaks` | `gitleaks.yml` |
| `runner / suggester / spotless` | `reviewdog-suggester.yml` |

`screenshot-comparison` / `maestro-e2e` / `vrt-coverage-check` は PR で実行されるが必須チェックには入っていないため、
赤でもマージは機械的にはブロックされない。結果を見て判断する運用になっている。

なお `main` は force push とブランチ削除を禁止し、管理者にも保護を適用する（`enforce_admins`）設定になっている。
必須レビュー数は 0 で、レビュー承認はマージの条件になっていない。

---

## 必要な Secrets

| Secret | 使うワークフロー | 未設定時に起きること |
|---|---|---|
| `REWARD_SERVER_URL` | `unit-test.yml`, `android-lint.yml`, `codeql.yml`, `screenshot-comparison.yml`, `vrt-coverage.yml`, `vrt-coverage-check.yml`, `maestro-e2e.yml`, `release-debug-apk.yml` | `local.properties` に空の値が書かれ、`BuildConfig.REWARD_SERVER_URL` が空文字になる。ビルド自体は通るが Retrofit の baseUrl が `/` だけになり、チケット取得APIを触る経路が実行時に失敗する（`app/src/main/java/jp/kztproject/rewardedtodo/di/ticket/TicketNetworkModule.kt`）|
| `DEBUG_KEYSTORE_BASE64` | `release-debug-apk.yml` | `debug.keystore` が生成されず APK ビルドが失敗する。base64 エンコードした debug keystore を入れる |
| `CLAUDE_CODE_OAUTH_TOKEN` | `claude.yml` | `@claude` コメントに反応しなくなる。CI のゲートには影響しない |

`GITHUB_TOKEN` は GitHub Actions が自動発行するため設定不要。

`REWARD_SERVER_URL` はローカル開発では `local.properties` に直接書く。詳細は [セットアップ](setup.md) を参照。

---

## レビュー方針との対応

[レビュー方針](review-policy.md) の「機械に任せる」表と、それを担保するワークフローの対応は以下の通り。

| 観点（レビュー方針） | 担保する仕組み | 実体 |
|---|---|---|
| フォーマット・スタイル・命名 | spotless / detekt | `reviewdog-suggester.yml`（spotless）／ `detekt.yml` |
| シークレット漏洩 | gitleaks（CI）／ GitHub Secret Scanning + Push Protection | `gitleaks.yml`（CI分）。Secret Scanning と Push Protection は GitHub のリポジトリ設定側 |
| 既知脆弱性・危険API | CodeQL | `codeql.yml`（週次） |
| 依存ライブラリの更新・脆弱性 | Renovate | `renovate.json5`（GitHub App。ワークフローではない） |
| リグレッション | ユニットテスト / Roborazzi / Maestro E2E | `unit-test.yml` ／ `screenshot-comparison.yml` + `upload_expected_image.yml` + `vrt-coverage-check.yml` ／ `maestro-e2e.yml` |

「リグレッション」の担保だけはワークフローが複数に分かれている。VRT は
「基準画像の生成（push時）→ 比較（PR時）→ コメント（比較の完了後）」の3段構成で、
さらに VRT が UI をどれだけ網羅しているかを `vrt-coverage-check.yml` が見張っている。
各テストの書き方・運用は [テスト戦略・VRT運用ガイド](testing.md) にまとめている。

---

## 共通アクションと実行コストの制御

### 共通アクション

JDK と Gradle のセットアップは composite action に切り出し、必要なワークフローから共有している。

| アクション | 内容 |
|---|---|
| `.github/actions/setup-java` | Temurin JDK 21 をセットアップし、Gradle のキャッシュを有効化する（`java-version` / `distribution` / `cache` は入力で上書きできる。`reviewdog-suggester.yml` は `cache: ''` を渡してキャッシュを無効化している）|
| `.github/actions/setup-gradle` | `gradle/actions/setup-gradle` を Gradle home キャッシュのクリーンアップ有効で実行する |

外部アクションは（`codeql.yml` の `actions/checkout@v7` を除き）すべてコミットSHAでピン留めしている。

### 実行コストの制御

| 仕組み | 対象 | 内容 |
|---|---|---|
| `paths-ignore` | `maestro-e2e.yml`, `screenshot-comparison.yml` | `docs/**` と `**/*.md` のみの変更ではエミュレータ／VRT を動かさない |
| `concurrency`（キャンセルあり）| `maestro-e2e.yml`, `upload_expected_image.yml`, `screenshot-comparison-comment.yml` | 同一 ref で新しい実行が始まったら古い実行をキャンセルする |
| `concurrency`（直列化）| `release-debug-apk.yml` | タグ採番とリリース作成が割り込まれないよう `cancel-in-progress: false` で直列化する |
| `timeout-minutes` | `maestro-e2e.yml`（60分）, `upload_expected_image.yml`（60分）, `screenshot-comparison-comment.yml`（2分） | ハングした実行を打ち切る |

### E2E 実行の注意点

`maestro-e2e.yml` はエミュレータ固有の不安定さに対処するため、実行を `.github/scripts/run-maestro-e2e.sh` に切り出している。

- フローは個別実行ではなく `maestro-tests/` のフォルダ一括実行にする（cold start が重く、個別実行だと毎回 cold になって `launchApp` 直後の操作がタイムアウトする）
- 一括実行が失敗したらスイート全体を1回だけリトライする
- `hide_error_dialogs=1` でクラッシュ／ANR ダイアログを抑止する（ランチャーの ANR ダイアログが最前面に残ると全フローが失敗する）
- エミュレータイメージは `google_apis` ではなく AOSP（`default`）を使う。本アプリは GMS に依存しないため、軽量な AOSP イメージのほうが安定する

---

## fork・新環境で再現する

1. Secrets に `REWARD_SERVER_URL` を設定する。空のままでもビルドは通るが、チケット取得APIを触る経路が実行時に失敗するため VRT / E2E が通らない。
2. `main` に一度 push して `upload_expected_image.yml` を走らせ、基準画像 artifact を作る。これが無いと `screenshot-comparison.yml` は基準画像のダウンロードで失敗する。
3. 必須チェックを揃えるなら、`main` のブランチ保護に上記5件のチェック名を登録する。
4. `@claude` を使うなら `CLAUDE_CODE_OAUTH_TOKEN`、debug APK を配布するなら `DEBUG_KEYSTORE_BASE64` を追加する。
5. Renovate は GitHub App のインストールが別途必要（設定は `renovate.json5`）。

---

## 関連ドキュメント

- [`docs/review-policy.md`](review-policy.md) - レビュー方針（どこを機械に任せるか）
- [`docs/testing.md`](testing.md) - テスト戦略・VRT運用ガイド
- [`docs/release-process.md`](release-process.md) - リリース手順とバージョニング規則
- [`docs/setup.md`](setup.md) - 開発環境セットアップ
