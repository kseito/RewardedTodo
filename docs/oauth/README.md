# Todoist OAuth 用の公開ファイル

TodoistのブラウザベースOAuth認証には、**アプリの外（HTTPSで公開されたドメイン）に置くファイル**が3つ必要になる。
このディレクトリはその配置用ファイルの原本で、実際の公開は `kseito/kseito.github.io` リポジトリへのコピーで行う。

これらのファイルが公開されるまで、実機での認証は完走しない。

## なぜ外部ホスティングが必要か

- Todoistは `redirect_uris` に **HTTPSしか受け付けない**（カスタムスキームは `invalid_client_metadata` で拒否される）
- `client_secret` をアプリに埋め込まずに済ませるため、[OAuth Client ID Metadata Document](https://developer.todoist.com/api/v1/#tag/Authorization) フローを使う。この方式では **`client_id` がメタデータ文書のURLそのもの**になるため、URLを公開する必要がある
- Auth TabがHTTPSリダイレクトをアプリに返すには **Digital Asset Links の検証**を通す必要があり、`assetlinks.json` の公開が要る

## 配置手順

`kseito/kseito.github.io` リポジトリのルートを基準に、次のとおりコピーする。

| このリポジトリ | 公開先パス | 公開URL |
|---|---|---|
| `docs/oauth/rewardedtodo/oauth/client.json` | `rewardedtodo/oauth/client.json` | `https://kseito.github.io/rewardedtodo/oauth/client.json` |
| `docs/oauth/rewardedtodo/oauth/callback/index.html` | `rewardedtodo/oauth/callback/index.html` | `https://kseito.github.io/rewardedtodo/oauth/callback` |
| `docs/oauth/well-known/assetlinks.json` | `.well-known/assetlinks.json` | `https://kseito.github.io/.well-known/assetlinks.json` |

```bash
# kseito.github.io リポジトリで実行する例
REWARDED_TODO=~/AndroidStudioProjects/RewardedTodo
mkdir -p rewardedtodo/oauth/callback .well-known
cp "$REWARDED_TODO/docs/oauth/rewardedtodo/oauth/client.json"          rewardedtodo/oauth/client.json
cp "$REWARDED_TODO/docs/oauth/rewardedtodo/oauth/callback/index.html"  rewardedtodo/oauth/callback/index.html
cp "$REWARDED_TODO/docs/oauth/well-known/assetlinks.json"              .well-known/assetlinks.json
```

> Jekyll を使っている場合、`.well-known/` はアンダースコア始まりではないため既定で公開されるが、
> `_config.yml` に `include: [".well-known"]` が必要なケースがある。公開後に必ず疎通確認すること。

## 公開後の確認

```bash
# いずれも 200 と、想定どおりのJSONが返ること
curl -sS -w "\n%{http_code}\n" https://kseito.github.io/rewardedtodo/oauth/client.json
curl -sS -w "\n%{http_code}\n" https://kseito.github.io/.well-known/assetlinks.json
curl -sS -o /dev/null -w "%{http_code}\n" https://kseito.github.io/rewardedtodo/oauth/callback

# Googleのアセットリンク検証APIでも確認できる
curl -sS "https://digitalassetlinks.googleapis.com/v1/statements:list\
?source.web.site=https://kseito.github.io\
&relation=delegate_permission/common.handle_all_urls"
```

`client.json` の `client_id` は **文書の取得URLと完全一致**していなければならない（不一致だと `invalid_client`）。
配置パスを変える場合は、`client.json` の `client_id` / `redirect_uris` と
`app/build.gradle.kts` の `TODOIST_CLIENT_ID` / `TODOIST_REDIRECT_HOST` / `TODOIST_REDIRECT_PATH` /
`TODOIST_REDIRECT_URI` を揃えて更新すること。

## assetlinks.json の署名鍵について

登録しているのは署名証明書の **SHA-256フィンガープリント**であって秘密鍵ではない。
APKから誰でも算出できる公開情報のため、公開して問題はない（keystore本体は `.gitignore` 済み）。

現在登録しているのは debug 鍵の1エントリのみ。
ローカルの `~/.android/debug.keystore` とCIの `DEBUG_KEYSTORE_BASE64` が同一鍵であることは、
GitHub Releaseで配布中のAPKで検証済み。

```bash
# フィンガープリントの再確認
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android | grep SHA256:

# 配布済みAPKから確認する場合
gh release download debug-0.1.1 --repo kseito/RewardedTodo --pattern "*.apk"
apksigner verify --print-certs app-debug.apk | grep "SHA-256"
```

staging (`jp.kztproject.rewardedtodo.beta`) や release ビルドで認証を使う場合は、
それぞれの `package_name` とフィンガープリントをエントリとして追加する必要がある。

## メタデータ文書の更新について

Todoistは取得したメタデータ文書をサーバー側でキャッシュする。明示的な更新エンドポイントは無く、
キャッシュミス時に自動で再取得される。すぐに反映したい場合は
[App Management Console](https://app.todoist.com/app/settings/integrations/app-management) から
クライアントを失効させると、次回の認可時に新しい内容が取得される。
