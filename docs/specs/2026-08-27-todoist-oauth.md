# Todoist ブラウザベース認証（OAuth 2.0 + PKCE）仕様書

| 項目 | 内容 |
|------|------|
| ステータス | Implemented |
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
- 「連携を解除」で端末からトークンを削除する（Todoist側の失効APIは公開クライアントから呼べないため、Todoist側の取り消しはユーザーがTodoistの設定画面から行う）
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
| Application | `application/todo` | `TodoistAuthSessionStore` を追加（`state` / `code_verifier` のメモリ保持。永続化しないため domain のIFと data の実装は持たない） |
| Data | `common/kvs` | `TODOIST_REFRESH_TOKEN` / `TODOIST_TOKEN_EXPIRES_AT` キーを追加。未使用の `TODOIST_API_TOKEN` を削除し `TODOIST_ACCESS_TOKEN` に一本化 |
| Feature | `feature/setting` | `SettingScreen` から入力系UIを削除し連携ボタンへ置換。`SettingViewModel` を OAuth フローに合わせて書き換え。`TodoistAuthTabLauncher` インターフェースと `TodoistAuthTabResult` を定義（`androidx.browser` には依存させない） |
| App | `app` | `androidx.browser:browser` を追加。`HomeActivity` で `AuthTabIntent.registerActivityResultLauncher` を登録し `TodoistAuthTabLauncher` を実装、`settingScreen()` へ受け渡す。`BuildConfig` に `TODOIST_CLIENT_ID` / `TODOIST_REDIRECT_URI` / `TODOIST_AUTHORIZE_URL` / `TODOIST_SCOPE` を追加 |
| DI | `app/di/auth` | `TodoistAuthModule` を新規作成（`TodoistAuthApi` の `@Provides`、`ITodoistAuthRepository` の `@Binds`）。`TodoistApiModule` の Interceptor を `GetValidAccessTokenUseCase` 経由に変更し、401 時にリフレッシュする `Authenticator` を追加。`RepositoriesModule` / `UseCaseModule` のバインドを更新 |
| Docs | `docs/oauth` | `kseito.github.io` へ配置する `client.json` / `assetlinks.json` / コールバックページと配置手順の README を成果物として追加 |

依存の方向は `docs/module-dependency.md` を維持する（`feature:setting` は `application:todo` / `domain:todo` のみに依存し、`data` や `androidx.browser` には依存しない）。

## 6. 受け入れ条件 (Acceptance Criteria)

- [x] 設定画面に APIトークンの入力欄・表示切替・クリアボタンが存在しない
- [ ] 未接続時に「Todoistと連携」ボタンが表示され、タップすると Auth Tab で Todoist の認可画面が開く
- [ ] 認可を完了すると Auth Tab が自動で閉じ、カードが「接続済み」に変わる
- [ ] 認可完了後に Todo 一覧が Todoist のタスクを取得できる
- [x] 認可をキャンセルすると「未接続」のままエラーメッセージが表示される
- [x] コールバックの `state` が一致しない場合はトークン交換を行わず認証失敗になる
- [x] トークン交換リクエストに `client_secret` が含まれず、`code_verifier` が含まれる
- [ ] アクセストークン期限切れ後に API を呼ぶと、自動リフレッシュされてリクエストが成功する
- [x] リフレッシュ応答に `refresh_token` が無い場合、直前のリフレッシュトークンが保持される
- [x] 「連携を解除」で端末からトークンが削除されて「未接続」に戻る
- [x] Auth Tab 非対応端末では専用のエラーメッセージが表示される
- [x] `client_secret` がリポジトリ内・ビルド成果物のどこにも存在しない

## 7. テスト方針

| 種別 | 対象 |
|------|------|
| ユニットテスト | `TodoistAuthSessionStore`（Interactorテストでモックせず実物を使う）、`CodeVerifier` / `CodeChallenge`（S256 の既知ベクタで検証）、`TodoistCredential`（期限判定）、`StartTodoistAuthInteractor`（authorize URL の組み立て）、`CompleteTodoistAuthInteractor`（state 不一致 / 成功 / 交換失敗）、`RefreshTodoistTokenInteractor`（ローテーション / `refresh_token` 省略時の保持）、`DisconnectTodoistInteractor`、`GetValidAccessTokenInteractor`、`TodoistCredentialRepository`、`SettingViewModel` |
| Roborazzi | `SettingScreen` の Preview を差し替え。旧6枚（`SettingScreenTokenInputPreview` / `SettingScreenValidationErrorPreview` / `SettingScreenVerifyingPreview` など）を削除し、未接続・接続済み・認証中・認証エラーの4枚を記録し直す |
| Maestro E2E | 既存の `setting-todoist-token-flow.yaml` はトークン手入力を前提としているため、そのままでは必ず失敗する。**実ブラウザでの Todoist ログインは E2E で完走できない**という制約があるため、置き換え方針（縮小したフローにするか削除するか）は**実装完了後に別途決める**。それまでは既存フローを残したままにする |

## 8. 未決事項・リスク

- **Todoist側のトークン失効(revoke)はアプリから行えない。** 実装中に判明した制約で、Todoistの失効エンドポイントは
  `DELETE /api/v1/access_tokens`（`client_secret` がクエリで必須）と `POST /api/v1/revoke`（HTTP Basic
  `client_id:client_secret` が必須）のいずれも `client_secret` を要求し、秘密鍵を持たない公開クライアントからは
  呼び出せない。そのため「連携を解除」は端末側のクレデンシャル削除のみとし、Todoist側の取り消しは
  設定画面の説明文でTodoistの「設定 › 連携」へ誘導する
- **認証セッションはメモリ保持とする（永続化しない）。** `state` と `code_verifier` は `TodoistAuthSessionStore`
  （`@Singleton` / `@Volatile`）がメモリ上に持つ。Auth Tabからは通常すぐ戻るため、バックグラウンド中の
  プロセス終了で失われる確率は低く、失われても state 照合に失敗して「もう一度お試しください」と表示される
  だけで復帰できる。永続化のコストに見合わないと判断した。使い切りの値のため成功・失敗いずれの場合も破棄する

- **`kseito.github.io` への反映がユーザー作業になる。** `client.json` / `assetlinks.json` / コールバックページが公開されるまで、実機での認証は完走できない。`docs/oauth/README.md` に配置手順をまとめる
- **Digital Asset Links に登録する署名鍵は debug のみとする。** 対象は `jp.kztproject.rewardedtodo.debug` / SHA-256 `A0:D4:D3:50:EC:C0:54:AF:A2:12:A8:24:DC:7A:4F:F1:E5:FA:F0:BC:EE:AD:4A:1D:F8:B7:2E:F6:C5:B5:54:98` の1エントリ。GitHub Release で配布中の `debug-0.1.1` の APK を `apksigner verify --print-certs` で検証し、CI の `DEBUG_KEYSTORE_BASE64` がローカルの `~/.android/debug.keystore` と同一鍵であることを確認済みのため、エントリを分ける必要はない。staging / release は今回の対象外
- **フィンガープリントの公開は問題ない。** `assetlinks.json` に載るのは署名証明書の SHA-256 であって秘密鍵ではなく、APK から誰でも算出できる公開情報。keystore 本体は `.gitignore` 済みで、CI は GitHub Secrets 経由
- **Auth Tab のフォールバック。** Chrome 137 未満では `isAuthTabSupported()` が false となり連携できない。手入力の代替導線は設けない方針のため、該当端末は実質的に Todoist 連携を利用できない
- **API のバージョン差異。** `TodoistApi` は `api/v1/*` を使うが、認可・トークンエンドポイントは `oauth/*`（バージョン無し）。Retrofit の baseUrl は `https://api.todoist.com/` のまま両方に対応できる
