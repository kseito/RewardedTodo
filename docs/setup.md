# 開発環境セットアップ

新しいマシンでこのリポジトリをクローンしてからビルド・開発できるようになるまでの手順。

## 前提条件

| ツール | バージョン / 備考 |
|-------|------------------|
| JDK | 17（CI は Temurin 17 を使用。Kotlin/Android のツールチェーンも `jvmToolchain(17)` 指定） |
| Android Studio | 最新安定版を推奨。同梱の JetBrains Runtime でも可 |
| Android SDK | Android Studio 経由でインストール（`compileSdk` は `gradle/libs.versions.toml` を参照） |
| Homebrew | lefthook / gitleaks のインストールに使用（macOS） |

## 1. リポジトリのクローン

```bash
git clone https://github.com/kseito/RewardedTodo.git
cd RewardedTodo
```

## 2. local.properties の設定

`local.properties`（リポジトリルート、Git管理外）に以下を記述する。

```properties
# Android SDK のパス（Android Studio でプロジェクトを開くと自動生成される）
sdk.dir=/Users/<あなたのユーザー名>/Library/Android/sdk

# リワードサーバーのベースURL
reward.server.url=https://<リワードサーバーのホスト>
```

- `reward.server.url` は `app/build.gradle.kts` で `BuildConfig.REWARD_SERVER_URL` に注入され、チケット取得API（Retrofit の baseUrl）に使われる。
- 未設定でも**ビルドは通る**（空文字がデフォルト）が、実行時にリワードサーバー連携の初期化で失敗するため、アプリを動かす場合は設定が必要。
- CI（GitHub Actions）では Repository Secrets の `REWARD_SERVER_URL` から `local.properties` に注入している（例: `.github/workflows/unit-test.yml`）。

## 3. Git フックの設定（lefthook）

コミット前の spotless 適用・push 前の detekt 実行を自動化するため、[lefthook](https://github.com/evilmartians/lefthook) を導入する。

```bash
brew install lefthook
lefthook install
```

フックの内容は `lefthook.yml` を参照。

- **pre-commit**: `spotlessKotlinApply`（整形して自動re-stage）、gitleaks によるシークレット検査
- **pre-push**: `detekt` / `detektCustomRules`、`spotlessKotlinCheck`

## 4. gitleaks のインストール（任意）

pre-commit フックでステージ済み差分のシークレット混入を検査する。未インストールでもフックはスキップされる（CI 側の gitleaks が最終防壁）。

```bash
brew install gitleaks
```

## 5. ビルド・テストの確認

セットアップ完了の確認として以下を実行する。

```bash
# ビルド
./gradlew assembleDebug

# 単体テスト
./gradlew testDebugUnitTest

# 端末へのインストール
./gradlew installDebug
```

## トラブルシューティング

- **`Illegal URL` 等で Retrofit の初期化に失敗する**: `local.properties` の `reward.server.url` が未設定または不正。手順2を確認する。
- **JDK バージョン起因のビルドエラー**: `java -version` で 17 を使っているか確認する。Android Studio 使用時は Gradle JDK 設定（Settings > Build Tools > Gradle）も 17 にする。
- **コミット時にフックが動かない**: `lefthook install` を実行済みか確認する（クローン直後は未設定）。
