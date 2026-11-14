# 報酬のサーバー移行と獲得台帳の電子化計画

| 項目 | 内容 |
|---|---|
| 状態 | Implemented（2026-11-07。ローカル検証済み、公開前） |
| 作成日 | 2026-11-07 |
| ブランチ | `codex/server-reward-migration-plan` |
| サーバー | https://github.com/kseito/RewardedTodoServer |
| 調査対象 | Androidの作業ツリー、サーバーmain `6bc97525984a30190caec1f018e7069db0600953` |

## 1. 目的・確定要件

- 報酬の設定を端末のRoomからサーバーへ移す。ローカルの報酬設定は引き継がず、サーバーで再登録する。
- 抽選で獲得した報酬をサーバーの台帳に保存する。未使用一覧から使用済みに変更でき、獲得・使用履歴を残す。
- 繰り返し不可の報酬は、一括抽選でも1回だけ当選し、その後は抽選対象から外す。
- 手元の台帳は取り込まない。新方式への切り替え後に獲得した分から電子管理する。
- 抽選結果が不明な場合の復旧は、同じrequest_id・要求内容での抽選POST再送に統一する。抽選結果再取得専用のGETは設けない。
- 使用取り消し、分割利用、有効期限、過去分の手入力・一括取込は初回の対象外。
- 破棄対象はローカルの報酬DB。認証DataStore、既存サーバーのユーザー・ポイント残高・履歴・Todo獲得枚数設定は維持する。

## 2. 現状と移行方針

Androidの `data:reward/RewardRepository` はRoomを使用する。`LotteryInteractor` / `BatchLotteryInteractor` は先にサーバーのポイントを消費し、端末で抽選する。当選結果は永続化されず、繰り返し不可の設定は当選後に削除される。一括抽選は同じ抽選箱を使うため、現在は同じ繰り返し不可報酬が複数回当たり得る。

サーバーはKtor / Exposed / PostgreSQLで、残高・ポイント履歴・Todo獲得枚数設定を管理する。既存の `rewards` はcost/categoryを持つ共通カタログ、`reward_purchases` は購入記録の構造で、ユーザー別の確率・繰り返し・使用状態を持たない。調査したソースにはそれらの操作APIがない。ただし本番データの有無は未確認。

**提案: 設定・抽選・獲得台帳をサーバーへ集約する。** チケット消費、抽選結果、獲得台帳への追加、繰り返し不可報酬の無効化を同じDBトランザクションで確定させる。Androidは表示・入力・結果演出を担当し、当選結果を決定しない。

既存の購入用2テーブルは変更・削除せず、新しいテーブルを追加する。これにより、未確認の既存サーバーデータを壊さずに進められる。

## 3. データ設計案

| テーブル | 役割・主な項目 |
|---|---|
| `reward_definitions` | 報酬設定。id、user_id、名前、説明、probability_bps、need_repeat、is_active、version、作成・更新日時 |
| `lottery_executions` | 抽選要求・確定結果。id、user_id、request_id、要求内容、回数、結果、消費ポイント履歴ID、確定時の残高、日時 |
| `acquired_rewards` | 獲得1回につき1行。id、user_id、reward_definition_id、lottery_execution_id、抽選内番号、獲得時の名前・説明、acquired_at、used_at |

- `used_at = null` を未使用とする。使用済みでも行を削除しない。同じ報酬を3回獲得したら3行とし、1件ずつ使用できる。
- 名前・説明は獲得時点のスナップショットを保存する。報酬設定を編集・無効化しても台帳は変わらない。
- 設定の削除は論理削除とし、参照を維持する。獲得台帳の削除は初回APIに含めない。
- ユーザーと要求IDの一意制約、抽選と抽選内番号の一意制約で重複記録を防ぐ。外部キーは別ユーザーの設定・抽選を参照できない構成にする。
- 未使用一覧・履歴の検索用にuser_id、used_at、acquired_at、idのインデックスを設計する。履歴はカーソル方式でページ取得する。
- 新規日時はUTCの時刻として保存・返却し、端末でローカル時刻に変換する。

## 4. 抽選ルール案

- 現行の「有効な報酬設定は最大7件」「抽選1回につきチケット1枚」「通常1回／一括10回」を維持する。台帳件数には7件制限を適用しない。
- 確率は0.01%単位の整数（basis points、100%=10,000）で保存する。各値0〜10,000、合計10,000以下をサーバーで検証する。浮動小数点の丸めに依存しない。
- 余った確率はハズレ。繰り返し不可の報酬が当たったら、その回以降の抽選対象から外し、その確率をハズレにする。他の報酬の確率は変えない。
- 一括抽選は全回成功か全回失敗。残高不足なら1枚も消費しない。開始時に有効報酬がない場合は拒否し、途中で全て当選済みになった場合は残りをハズレとして実行する。この挙動は画面にも説明する。
- 設定変更・抽選・既存のポイント加算/消費は、ユーザー単位の同じロック順序で整合させる。設定の上限・確率合計の検証も同じトランザクション内で行う。
- 設定編集はversionによる競合検知を提案する。古い画面からの更新は409として再読込を促す。

### 通信失敗と再試行

抽選前にAndroidでrequest_idを生成し、ユーザーID・回数とともにDataStoreへ保存してから送信する。同じ操作のタイムアウト、401後の再送、再起動後の復旧には同じIDを使う。

サーバーは同一ユーザー・同一IDの確定済み要求に保存済みの同じ結果を返す。同じIDで異なる回数などの要求は409とする。要求の登録と抽選結果の保存も消費と同じトランザクションに含め、並行送信時も二重消費・二重獲得させない。ハズレのみの抽選も記録する。

結果不明の間は新しい抽選を始めず、同じrequest_id・要求内容で `POST /api/lotteries/{userId}` を再送して確定させる。先の要求が確定済みなら保存済みの結果を返し、未確定ならその要求を実行する。確定後の残高表示は再取得し、過去の再送レスポンスに含まれる残高で最新表示を上書きしない。要求IDは少なくとも再送可能な期間を越えて保持し、初回は抽選履歴とともに永続保持する。

使用処理は未使用から使用済みへの条件付き更新とし、繰り返し送信しても使用時刻を変更しない。使用取り消しは対象外なので、使用前に確認を出す。

## 5. API案

以下は提案であり、現行APIではない。既存Bearer認証・ユーザーID解決を再利用する。

| 操作 | API案 |
|---|---|
| 有効報酬設定一覧・作成 | `GET / POST /api/rewards/{userId}` |
| 設定更新・削除 | `PUT / DELETE /api/rewards/{userId}/{rewardId}` |
| 抽選・結果不明時の復旧 | `POST /api/lotteries/{userId}`、body: `{request_id, count}`。復旧時は同じID・要求内容を再送 |
| 獲得台帳 | `GET /api/acquired-rewards/{userId}?status=unused|used&cursor=...` |
| 使用 | `PUT /api/acquired-rewards/{userId}/{acquiredRewardId}/use` |

抽選レスポンスには抽選ID、順番付き結果（当選の台帳IDまたはハズレ）、消費枚数、確定時残高を含める。アプリから報酬名や確率を抽選要求へ送らず、サーバーの最新設定を使う。

全APIでユーザー認証に加えリソース所有者を検証する。401は認証、404は対象なし、409は競合、422は残高不足等の業務エラーとして機械判定可能なコードを定義する。IDの型、文字数、確率精度、ページサイズ、日時形式をAPI契約書で固定する。

## 6. Androidの変更

| レイヤー | 変更 |
|---|---|
| domain:reward | 獲得報酬、抽選結果・要求ID、台帳リポジトリ、抽選リポジトリの型・IFを追加。Android依存は入れない |
| application:reward | 抽選をサーバー呼び出しへ変更。獲得一覧取得・使用UseCaseを追加。抽選に画面のRewardCollectionを渡す契約を廃止 |
| data:reward | Room実装をAPI実装へ差し替え。設定・台帳はメモリに保持し、明示的に再取得 |
| 通信共通基盤 | data:ticket内のユーザーID解決・認証再送を共有可能な通信モジュールへ切り出す案。業務Repository同士の依存は避ける |
| feature:reward | 既存の設定・抽選画面を維持し、BottomNavigationの3番目にギフト箱アイコン付きの「獲得済み」タブを配置し、未使用／使用済み一覧、使用確認、獲得日時・使用日時を追加 |
| app / DI | API実装のバインドを追加。報酬RoomのDB・DAO・DIを撤去 |

初回表示・画面復帰・プル更新・書き込み成功後に必要な情報を再取得する。通信失敗を空一覧と区別し、メモリの既存表示や編集入力を保持する。保存成功まで編集画面を閉じない。オフラインでの抽選・使用・設定更新は行わない。

ログアウト時はユーザー別の表示状態を破棄する。保留抽選のrequest_idはユーザーに紐づけ、別アカウントで再送しない。

旧DB名 `reward` は一度限りのクリーンアップで削除し、通常の起動時に再作成しない。Room利用箇所がなくなったことを確認して `common:database` と関連依存を整理する。DB削除とアプリ全データ消去は区別する。

## 7. 実装・リリース順序

1. **仕様・契約確定**: 本計画の設計提案をレビューし、API契約・ADRを作成する。Androidの現作業ブランチと最新mainの差分、サーバー本番の稼働版・DB状態を確認する。
2. **サーバー基盤**: 新テーブル、制約・インデックス・RLSを追加する。最新mainに導入済みのFlywayを使用し、適用済みSQLは編集せず次のバージョンを追加する。SQL生成・検証は使い捨てPostgreSQLで行う。
3. **サーバー業務処理**: 設定CRUD、抽選トランザクション・冪等性、台帳取得・使用API、認可とエラー契約を実装・検証する。
4. **Android移行**: domain → application → data → feature → DIの順に実装。台帳画面、保留抽選の復旧、Room撤去を含める。
5. **結合検証**: テスト環境で設定登録→ポイント獲得→単発/一括抽選→未使用台帳→使用済み→再起動後の表示まで確認する。
6. **切り替え**: サーバーDBのバックアップ・復元手順を確認し、追加スキーマと新APIを先行公開する。その後Androidを更新し、報酬設定を再登録して切り替え確認する。
7. **運用確認**: 二重要求、409/422、抽選失敗、台帳記録失敗、認証再解決を監視する。ポイント支出と抽選結果・獲得台帳が対応していることを確認する。

分割PRの目安は「仕様/ADR」「サーバーDB・API」「Android通信・抽選移行」「Android台帳UI・Room撤去」。Androidは一式が揃ってから公開し、サーバー抽選へ切り替えたのに台帳を閲覧できない中間版を配布しない。

切り戻しは新規抽選・更新の停止と前方修正を基本とする。既存残高・獲得台帳・抽選履歴を削除しない。

## 8. 受け入れ条件・テスト

- 設定は同じユーザーで再取得でき、別ユーザーは読み書きできない。名前・確率・7件上限をサーバーで検証できる。
- 単発・一括で消費枚数、ハズレ数、獲得台帳件数が一致する。全ハズレでも抽選履歴が残る。
- 繰り返し不可は一括内・複数端末の同時抽選を通じて1回しか獲得されない。繰り返し可は当選回数分の行が残る。
- 確率0%、100%、合計超過、境界値、非繰り返し当選後のハズレ枠を固定乱数によるテストで検証する。
- 残高不足・DB障害で部分消費や部分記録が残らない。Webhook加算と抽選の同時実行で残高を失わない。
- コミット後の応答消失・同じ要求の並行送信・再起動・認証再送でも、同じrequest_id・要求内容のPOST再送で二重消費/二重記録されず元の結果を回収できる。初回要求がサーバーへ届いていない場合は、再送で1回だけ実行される。
- 使用済み化の連打・再送は一度の使用として扱われる。設定編集・削除後も獲得時点の内容・使用履歴が残る。
- 履歴の複数ページを取りこぼさず取得できる。通信失敗・アカウント切り替え・旧DBがある更新インストールを検証する。
- サーバーは単体/APIテストに加え実PostgreSQLでトランザクション・並行実行・Flyway適用・RLSを確認する。RLSに加えAPI層でもユーザー分離を検証する。
- AndroidはKotest matcherによるユニットテスト、assembleDebug、testDebugUnitTest、lintDebug、spotlessCheck、Roborazzi、Maestroの設定/抽選/台帳フローを実施する。

## 9. 設計承認・調査範囲

本計画は2026-11-07に承認済み。確率の整数化、当選後のハズレ置換、一括抽選途中で対象がなくなった際の扱い、設定編集の競合検知を含めて実装した。確定したAPI契約は `docs/reward-inventory-api.md`、設計判断は `docs/adr/0004-server-rewards-and-inventory.md` を参照。

本番DB・稼働サーバーは調査/変更していない。既存の購入用テーブルにデータがあるか、最新mainが本番へ適用済みかは公開前に確認する。ローカルのサーバーチェックアウトは最新mainより古かったため、テーブル構造・マイグレーション方針はGitHub APIから最新mainを読み取って確認した。

参照: [サーバーのテーブル定義](https://github.com/kseito/RewardedTodoServer/blob/6bc97525984a30190caec1f018e7069db0600953/src/main/kotlin/jp/kztproject/database/Tables.kt)、[Flyway運用](https://github.com/kseito/RewardedTodoServer/blob/6bc97525984a30190caec1f018e7069db0600953/docs/database-migrations.md)、Androidの `docs/domain-model.md`、`docs/external-integration.md`、`docs/module-dependency.md`、`docs/di-setup.md`、`docs/review-policy.md`。


## 10. 実装・検証記録（2026-11-07）

### 実装

- サーバー: 報酬設定CRUD、抽選POSTと同一IDによる復旧、獲得台帳のページ取得・使用済み化、Flyway V4を実装。チケット消費は新しい抽選APIに集約し、旧消費APIを削除。
- Android: `common:rewardserver` に通信・ユーザー解決を共有化。サーバー抽選へ切り替え、保留要求をユーザー別DataStoreへ保存。獲得台帳画面と使用確認を追加し、Room実装を撤去。
- サーバー作業場所: `/Users/kazutoseito/AndroidStudioProjects/worktrees/RewardedTodoServer-inventory`、ブランチ `codex/server-rewards-and-inventory`。元のサーバーチェックアウトの変更は保持。

### 自動検証

- Android: `./gradlew testDebugUnitTest lintDebug spotlessCheck detektCustomRules assembleDebug --max-workers=2` 成功。E2E APKも `assembleE2e` 成功。
- 主要モジュールのテスト: application:reward 10件、data:reward 4件、data:ticket 6件、feature:reward 15件。通信応答消失→Repository再生成→同じPOST復旧、別ユーザーでの誤再送防止、入力精度・version、台帳の取得失敗と再試行を確認。
- Roborazzi: 意図した画面差分を目視し、`recordRoborazziDebug` → `verifyRoborazziDebug` 成功。appテスト40件。画像は既存方針どおりgit管理対象外。
- Maestro: `reward-inventory-flow`、`add-reward-flow`、`edit-reward-flow`、`delete-reward-flow`、`batch-lottery-flow`、`batch-lottery-insufficient-tickets-flow`、`keep-repeat-reward-flow` の7本成功。台帳フロー内で単発・繰り返し不可の当選、使用確認のキャンセル/確定、再起動後の使用済み保持も確認。
- 更新インストール: エミュレーターに旧 `reward` SQLiteファイルを配置して `adb install -r` 後に起動。旧DB削除、クリーンアップ済みフラグ、DataStoreを含む既存files配下のハッシュ一致を確認。
- サーバー: H2テスト成功。使い捨てPostgreSQL 16で `REWARD_TEST_DATABASE_URL` と `REWARD_MIGRATION_DATABASE_URL` を設定した `./gradlew test` が30件成功（スキップなし）。同一IDの並行送信、Webhook加算との競合、確率合計の同時更新、Flyway新規適用・再適用、RLS有効化を確認。
- Flyway適用後の実DBで台帳INSERTをトリガーにより失敗させ、チケット消費・抽選履歴・報酬無効化がロールバックされ、同じrequest_idで再試行できることを確認。

### 残る公開前の作業

- AndroidのE2EはWireMock経由、サーバーは実PostgreSQL経由でそれぞれ検証。稼働サーバーと新APKを接続するステージングの通し確認、実TodoistのWebhookを含む確認は公開前に行う。
- 本番のDB状態・バックアップ・稼働版の確認、デプロイは未実施。サーバーの `docs/reward-inventory-release.md` に手順を記載。
- 既存Lint警告10件（依存更新通知8件、共通UIの命名1件、Todo manifestの余分な文字1件）とGradle非推奨警告が残る。今回追加した箇所のLint警告は解消済み。AGENTS.mdの全警告解消条件により、現時点ではコミットしていない。
- push・Draft PR作成は、featureスキルの承認ゲート2で変更内容の承認後に実施する。
