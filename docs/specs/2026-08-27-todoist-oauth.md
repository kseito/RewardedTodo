# Todoist ブラウザベース認証（OAuth 2.0 + PKCE）仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Draft |
| 作成日 | 2026-08-27 |
| ブランチ | feature/todoist-oauth |
| 関連Issue/PR | （なし） |

## 1. 背景・目的

現在の Todoist 連携は、ユーザーが Todoist の開発者ページから40桁のAPIトークンを自力でコピーし、設定画面へ直接貼り付ける必要がある。開発者以外には手順が不透明で不親切なうえ、権限スコープを絞れず、アプリ側から失効させることもできない。

Chrome Custom Tabs の **Auth Tab** を使ったブラウザベースの OAuth 2.0 認証へ置き換え、「Todoistと連携」ボタン1つで完了する導線にする。

## 2. 要件

### 機能要件

- 設定画面の「Todoistと連携」ボタンから Auth Tab が開き、Todoist の認可画面が表示される
- ユーザーが認可すると Auth Tab が自動で閉じ、アプリがアクセストークンを取得・保存して「接続済み」になる
- ユーザーが認可を拒否／キャンセルした場合、アプリは元の未接続状態のままエラーを表示する
- アクセストークンの期限が切れた場合、リフレッシュトークンを使って自動的に再取得する（ユーザー操作不要）
- 「連携を解除」でトークンを Todoist 側で失効（revoke）させたうえで端末から削除する
- APIトークンの手入力UIは完全に削除する

### 非機能要件 / 制約

- **client_secret を一切アプリに埋め込まない。** Todoist の [OAuth Client ID Metadata Document](https://developer.todoist.com/api/v1/#tag/Authorization) フローを使い、`token_endpoint_auth_method: "none"` の公開クライアントとして PKCE (S256) で保護する
- Todoist は `redirect_uris` に **HTTPS を必須**とする（カスタムスキームは `invalid_client_metadata` で拒否される）。よって redirect URI は HTTPS とし、Auth Tab の HTTPS モード（Digital Asset Links 検証）で受け取る
- `client_id`（メタデータ文書）・`redirect_uri`・コールバックページ・`assetlinks.json` は **`kseito.github.io`（別リポジトリ）にホストする**。本リポジトリには配置用のファイルを `docs/oauth/` に成果物として用意し、反映はユーザーが行う
- Auth Tab は Chrome 137 以降が必要。非対応端末では `CustomTabsClient.isAuthTabSupported()` が false を返すため、その旨をエラーとして表示する（手入力へのフォールバックは設けない）
- minSdk 31 のまま。`androidx.browser:browser` を新規依存として追加する

### 移行方針

既存の手入力トークン（`todoist_api_token`）は **移行せず破棄する**。開発用APIトークンは refresh も revoke もできず OAuth のクレデンシャル管理と両立しないため、アップデート後は再連携を求める。

## 3. 画面・UX

- 対象画面: `SettingScreen`（`feature:setting`）
- UI変更点:
  - **削除**: `OutlinedTextField`（トークン入力）、表示/非表示トグル、クリアボタン、「接続を確認」ボタン、`TokenValidationError` の入力系エラー
  - **変更**: 未接続時のアクションボタンを「Todoistと連携」に置き換える
  - **維持**: `ConnectionStatusCard`（接続済み/未接続）、「連携を解除」ボタン、ローディング表示
  - **追加**: 認証失敗時のエラーメッセージ表示（キャンセル / 検証失敗 / 交換失敗 / Auth Tab非対応）
- 操作フロー:
  1. 設定画面を開く → 「未接続」カードと「Todoistと連携」ボタンが表示される
  2. ボタンをタップ → ViewModel が `code_verifier` と `state` を生成し authorize URL を発行
  3. Auth Tab が開き Todoist の認可画面が表示される
  4. ユーザーが認可 → Auth Tab が自動で閉じ、redirect URI がアプリへ返る
  5. `state` を照合し、`code` + `code_verifier` をトークンエンドポイントへ送ってアクセストークンを取得・保存
  6. カードが「接続済み」に変わり、ボタンが「連携を解除」になる

## 4. ドメインへの影響

`docs/domain-model.md` の「Todoの管理には Todoist API を活用」という前提は変わらず、その**認証手段のみ**が変わる。抽選・チケットのビジネスルールへの影響はない。

- 関係するエンティティ / Value Object:
  - `ApiToken`（既存・変更）— 40桁hex固定の正規表現を持つが、OAuth で発行されるアクセストークンの書式に将来依存しないよう「空白でないこと」のみを検証する形に緩和する
  - `TodoistCredential`（新規）— `accessToken: ApiToken` / `refreshToken: RefreshToken?` / `expiresAt: Long?`(epoch millis) を保持するエンティティ
  - `RefreshToken`（新規 Value Object）
  - `AuthorizationCode`（新規 Value Object）
  - `CodeVerifier` / `CodeChallenge`（新規 Value Object）— PKCE。`SecureRandom` + `MessageDigest` のみで生成するため Android 非依存を保てる
  - `OAuthState`（新規 Value Object）— CSRF 対策の `state`
- 新規追加・変更するルール:
  - アクセストークンは `expiresAt` を持ち、期限の60秒前を過ぎたら失効扱いとする
  - リフレッシュトークンは毎回ローテーションされる。レスポンスに `refresh_token` が含まれない場合（60秒のグレース期間内の再試行）は**直前の値を保持する**
  - コールバックの `state` が発行時の値と一致しない場合は認証失敗として扱い、トークン交換を行わない

## 5. レイヤー別の変更方針

| レイヤー | モジュール | 変更内容 |
|---------|-----------|---------|
| Domain | `domain/todo` | `TodoistCredential` / `RefreshToken` / `AuthorizationCode` / `CodeVerifier` / `CodeChallenge` / `OAuthState` を追加。`ApiToken` の書式検証を緩和。`ITodoistAuthRepository` を追加。`IApiTokenRepository` を `ITodoistCredentialRepository` にリネームし credential 単位のAPIへ変更。`TokenError` に OAuth 用のケースを追加 |
| Application | `application/todo` | `SaveApiTokenUseCase` / `ValidateApiTokenUseCase` とその Interactor を**削除**。`StartTodoistAuthUseCase`・`CompleteTodoistAuthUseCase`・`RefreshTodoistTokenUseCase`・`DisconnectTodoistUseCase`・`GetValidAccessTokenUseCase` とその Interactor を追加。`GetApiTokenUseCase` は credential 参照へ変更 |
| Data | `data/todoist` | `TodoistAuthApi`（`POST oauth/access_token` / `POST api/v1/revoke`）と `TokenResponse` DTO を追加 |
| Data | `data/todo` | `ApiTokenRepository` を `TodoistCredentialRepository` にリネームし access/refresh/expiresAt を保存。`TodoistAuthRepository`（`ITodoistAuthRepository` 実装）を追加 |
| Data | `common/kvs` | `TODOIST_REFRESH_TOKEN` / `TODOIST_TOKEN_EXPIRES_AT` キーを追加。未使用の `TODOIST_API_TOKEN` を削除し `TODOIST_ACCESS_TOKEN` に一本化 |
| Feature | `feature/setting` | `SettingScreen` から入力系UIを削除し連携ボタンへ置換。`SettingViewModel` を OAuth フローに合わせて書き換え。`TodoistAuthTabLauncher` インターフェースと `TodoistAuthTabResult` を定義（`androidx.browser` には依存させない） |
| App | `app` | `androidx.browser:browser` を追加。`HomeActivity` で `AuthTabIntent.registerActivityResultLauncher` を登録し `TodoistAuthTabLauncher` を実装、`settingScreen()` へ受け渡す。`BuildConfig` に `TODOIST_CLIENT_ID` / `TODOIST_REDIRECT_URI` / `TODOIST_AUTHORIZE_URL` / `TODOIST_SCOPE` を追加 |
| DI | `app/di/auth` | `TodoistAuthModule` を新規作成（`TodoistAuthApi` の `@Provides`、`ITodoistAuthRepository` の `@Binds`）。`TodoistApiModule` の Interceptor を `GetValidAccessTokenUseCase` 経由に変更し、401 時にリフレッシュする `Authenticator` を追加。`RepositoriesModule` / `UseCaseModule` のバインドを更新 |
| Docs | `docs/oauth` | `kseito.github.io` へ配置する `client.json` / `assetlinks.json` / コールバックページと配置手順の README を成果物として追加 |

依存の方向は `docs/module-dependency.md` を維持する（`feature:setting` は `application:todo` / `domain:todo` のみに依存し、`data` や `androidx.browser` には依存しない）。

## 6. 受け入れ条件 (Acceptance Criteria)

- [ ] 設定画面に APIトークンの入力欄・表示切替・クリアボタンが存在しない
- [ ] 未接続時に「Todoistと連携」ボタンが表示され、タップすると Auth Tab で Todoist の認可画面が開く
- [ ] 認可を完了すると Auth Tab が自動で閉じ、カードが「接続済み」に変わる
- [ ] 認可完了後に Todo 一覧が Todoist のタスクを取得できる
- [ ] 認可をキャンセルすると「未接続」のままエラーメッセージが表示される
- [ ] コールバックの `state` が一致しない場合はトークン交換を行わず認証失敗になる
- [ ] トークン交換リクエストに `client_secret` が含まれず、`code_verifier` が含まれる
- [ ] アクセストークン期限切れ後に API を呼ぶと、自動リフレッシュされてリクエストが成功する
- [ ] リフレッシュ応答に `refresh_token` が無い場合、直前のリフレッシュトークンが保持される
- [ ] 「連携を解除」で revoke が呼ばれ、端末からトークンが削除されて「未接続」に戻る
- [ ] Auth Tab 非対応端末では専用のエラーメッセージが表示される
- [ ] `client_secret` がリポジトリ内・ビルド成果物のどこにも存在しない

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `CodeVerifier` / `CodeChallenge`（S256 の既知ベクタで検証）、`TodoistCredential`（期限判定）、`StartTodoistAuthInteractor`（authorize URL の組み立て）、`CompleteTodoistAuthInteractor`（state 不一致 / 成功 / 交換失敗）、`RefreshTodoistTokenInteractor`（ローテーション / `refresh_token` 省略時の保持）、`DisconnectTodoistInteractor`、`GetValidAccessTokenInteractor`、`TodoistCredentialRepository`、`SettingViewModel` |
| Roborazzi | `SettingScreen` の Preview を差し替え。旧6枚（`SettingScreenTokenInputPreview` / `SettingScreenValidationErrorPreview` / `SettingScreenVerifyingPreview` など）を削除し、未接続・接続済み・認証中・認証エラーの4枚を記録し直す |
| Maestro E2E | `setting-todoist-token-flow.yaml` を `setting-todoist-oauth-flow.yaml` に置き換える。**実ブラウザでの Todoist ログインは E2E で完走できない**ため、検証範囲は「設定画面を開く → 未接続表示 → 『Todoistと連携』ボタンが存在する → 戻る」までに縮小する。認可完了以降は手動確認で担保する |

## 8. 未決事項・リスク

- **`kseito.github.io` への反映がユーザー作業になる。** `client.json` / `assetlinks.json` / コールバックページが公開されるまで、実機での認証は完走できない。`docs/oauth/README.md` に配置手順をまとめる
- **Digital Asset Links に登録する署名鍵。** debug（`jp.kztproject.rewardedtodo.debug`, ローカルの `~/.android/debug.keystore`）と staging（`jp.kztproject.rewardedtodo.beta`）の SHA-256 を登録する。CI が配置する `debug.keystore` は手元に無いため CI 上での認証完走は対象外
- **release ビルドの署名設定が未定。** `app/build.gradle.kts` の release buildType に `signingConfig` が無く、フィンガープリントが確定していないため assetlinks.json には含めない
- **Auth Tab のフォールバック。** Chrome 137 未満では `isAuthTabSupported()` が false となり連携できない。手入力の代替導線は設けない方針のため、該当端末は実質的に Todoist 連携を利用できない
- **API のバージョン差異。** `TodoistApi` は `api/v1/*` を使うが、認可・トークンエンドポイントは `oauth/*`（バージョン無し）。Retrofit の baseUrl は `https://api.todoist.com/` のまま両方に対応できる
