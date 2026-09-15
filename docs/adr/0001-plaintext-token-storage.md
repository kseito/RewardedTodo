---
status: "accepted"
date: 2026-05-30
decision-makers: kseito
---

# Todoist APIトークンを平文DataStoreで保存する

## Context and Problem Statement

Todoist連携のためAPIトークンを端末に永続化する必要がある。保存先として、平文の `DataStore<Preferences>` と、アプリ層で暗号化する `EncryptedSharedPreferences`（Keystore鍵）のどちらを使うか判断が必要だった。

本アプリは Google Play 等に公開しておらず、作者本人のみが、本人所有の端末で使う個人開発アプリである（ソースコードは GitHub で公開しているが、アプリ本体は配布していない）。

関連コード: `data/todo` の `ApiTokenRepository` / `common/kvs` の `UserPreferencesKeys`

## Considered Options

* 平文の `DataStore<Preferences>` に保存する
* アプリ層で暗号化する（`EncryptedSharedPreferences` / Keystore鍵）

## Decision Outcome

Chosen option: "平文の `DataStore<Preferences>` に保存する"（`UserPreferencesKeys.TODOIST_API_TOKEN`）。アプリ層での追加暗号化は行わない。理由は以下のとおり。

- Android 10 以上はデフォルトで **FBE（File-Based Encryption）** が有効で、ユーザー認証情報に紐づく鍵でディスク上のデータが暗号化される。端末紛失・電源オフ・物理的なディスク吸い出しといった at-rest の脅威はこれで守られる。
- アプリサンドボックスにより、他アプリから当該データは読めない。
- 自分専用・本人所有の端末でのみ動かすという脅威モデルでは、`EncryptedSharedPreferences` を足して得られる限界利益（端末がroot化され、かつアンロックされて動作中のケースのみ上乗せで守る）がほぼ無い。
- なお、ソースコードを GitHub で公開しているが、トークンはコードに含めず実行時にユーザーが入力して端末の DataStore に保存するため、リポジトリ公開と本決定は無関係（コードへのシークレット混入は別途 gitleaks 等で防ぐ）。

### Consequences

- 受け入れたリスク: root化された端末がアンロックされ動作中の場合、トークンは保護されない。現状の脅威モデルでは許容する。
- **再評価が必要になる条件**: アプリを一般公開する／本人以外が使う／より機微な認証情報（OAuthリフレッシュトークン等）を保存する、のいずれかに該当したら、この決定を見直し `EncryptedSharedPreferences` への移行を検討する。
