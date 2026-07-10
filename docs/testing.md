# テスト戦略・VRT運用ガイド

本リポジトリの品質保証は、[レビュー方針](review-policy.md) の通り「機械的に検証できる観点は自動化で担保する」ことを柱としている。
このドキュメントでは、その中核であるユニットテストと VRT（Visual Regression Testing / Roborazzi スクリーンショットテスト）の書き方・運用手順をまとめる。

---

## テストの全体像

| 種類 | 対象 | 実行環境 | CI |
|------|------|---------|-----|
| ユニットテスト | domain / application / data / feature の各ロジック | JVM（一部 Robolectric） | `unit-test.yml`（PR時） |
| VRT（Roborazzi） | Compose UI の見た目 | JVM + Robolectric | `screenshot-comparison.yml`（PR時） |
| VRTカバレッジ | VRT が UI コードをどれだけ網羅しているか | JVM + JaCoCo | `vrt-coverage-check.yml`（PR時）/ `vrt-coverage.yml`（週次） |
| E2E（Maestro） | 実機/エミュレータ上の主要フロー | エミュレータ | 未CI化（`maestro-tests/` にフロー整備済み） |

---

## ユニットテストの方針

テストは各モジュールの `src/test/` に置く。実行コマンド：

```bash
# 全モジュール
./gradlew testDebugUnitTest

# 特定モジュールのみ
./gradlew :application:reward:testDebugUnitTest
```

### ライブラリの使い分け

| ライブラリ | 用途 |
|-----------|------|
| JUnit 4 | テストランナーの基本。`@Test` / `@Before` / `@After` |
| MockK | 依存（リポジトリ・UseCase）のモック。coroutine 対応の `coEvery` / `coVerify` を使う |
| Truth / AssertJ | アサーション。両方が混在しているため、**同一モジュール内の既存テストに合わせる** |
| Robolectric | Android フレームワークに依存するテスト（Room DAO、VRT）でのみ使う |
| kotlinx-coroutines-test | suspend 関数のテスト。`runTest` で包む |
| Kotest (ShouldSpec) | 一部の ViewModel テストで使用（例: `TodoListViewModelTest`） |

### レイヤーごとの書き方

**domain / application 層** — Android 依存がないので素の JVM テスト。依存は MockK でモックし、`runTest` で suspend 関数を呼ぶ。

```kotlin
class LotteryInteractorTest {
    private val mockTicketRepository: ITicketRepository = mockk()
    private val interactor = LotteryInteractor(mockTicketRepository, mockRewardRepository)

    @Test
    fun shouldGetPrize() = runTest {
        coEvery { mockTicketRepository.consumeTicket() } returns Unit
        // ...
        coVerify(exactly = 1) { mockTicketRepository.consumeTicket() }
    }
}
```

参照: `application/reward/src/test/.../LotteryInteractorTest.kt`

**data 層（Room DAO）** — Robolectric + in-memory データベースを使う。

```kotlin
@RunWith(RobolectricTestRunner::class)
class TodoDaoTest {
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
    }
}
```

参照: `data/todo/src/test/.../TodoDaoTest.kt`

**feature 層（ViewModel）** — `Dispatchers.setMain` + `StandardTestDispatcher` でメインディスパッチャを差し替え、UseCase を MockK でモックする。

参照: `feature/todo/src/test/.../TodoListViewModelTest.kt`

### テストデータ

繰り返し使うダミーデータは `test/` 配下のテスト専用モジュールに置く（例: `test/reward` の `DummyCreator`）。

### 注意点（レビュー方針より）

AIが実装とテストの両方を書くと、両者が同じ前提を共有しテストが緑でも正しさを証明しないことがある。
assert の期待値は実装（how）ではなく仕様（what）から導くこと。確率計算など「静かに壊れる」ロジックは特に注意する。

---

## VRT（Roborazzi スクリーンショットテスト）

### 仕組み

VRT は個別のテストを書く方式ではなく、**Showkase + パラメタライズドテストで全 Preview を自動撮影する**方式を採っている。

1. 各 feature/common モジュールの `@Preview` 付き Composable を Showkase（KSP）が収集する
2. `app` モジュールの `ShowkaseParameterizedTest` が、収集された全コンポーネントを Robolectric（Pixel4a / SDK 35）上で描画し、`app/screenshots/` に PNG として保存する
3. CI が期待画像（main の状態）と比較し、差分があれば PR にコメントする

つまり **`@Preview` を書けば、それがそのまま VRT の撮影対象になる**。

### スクリーンショットテストの追加方法

新しい画面・コンポーネントを VRT の対象にする手順：

1. 対象の Composable と同じファイルに、状態ごとの `@Preview` 関数を書く

   ```kotlin
   @Preview
   @Composable
   fun TodoDetailBottomSheetContentEditingPreview() {
       TodoDetailBottomSheetContent(
           todo = Todo(id = 1L, name = "英語学習", /* ... */),
           onTodoSaveSelected = {},
           // ...
       )
   }
   ```

   - **Preview 関数は public にする**（private だと Showkase に収集されない）
   - ローディング中・空・エラーなど、**状態のバリエーションごとに Preview を分ける**と VRT の網羅性が上がる

2. モジュールに Showkase が未導入なら `build.gradle.kts` に追加する（`feature/todo` などを参照）

   ```kotlin
   debugImplementation(libs.showkase)
   implementation(libs.showkase.annotation)
   kspDebug(libs.showkase.processor)
   ```

3. ローカルで撮影して確認する

   ```bash
   # 期待画像を撮影（app/screenshots/ に出力）
   ./gradlew recordRoborazziDebug

   # 期待画像との比較（差分があると *_compare.png が生成される）
   ./gradlew compareRoborazziDebug
   ```

画像のファイル名は `{Preview関数名}_{グループ名}.png` になる（`ShowkaseParameterizedTest` 参照）。

### 期待画像の更新・アップロードの仕組み

期待画像は Git 管理ではなく **GitHub Actions のアーティファクトで受け渡している**。手動更新は不要で、main へのマージが実質的な「期待画像の更新」になる。

```
main へ push（マージ）
  └─ upload_expected_image.yml
       recordRoborazziDebug → アーティファクト expected_screenshots にアップロード

PR 作成・更新
  └─ screenshot-comparison.yml
       expected_screenshots（base ブランチの最新）をダウンロード
       → compareRoborazziDebug で比較 → 差分レポートをアップロード
  └─ screenshot-comparison-comment.yml（上記完了後に起動）
       差分画像（*_compare.png）を companion_<ブランチ名> ブランチに push し、
       PR に比較画像コメントを投稿。差分が解消されたらコメントを自動削除
```

運用上のポイント：

- **意図した見た目の変更**：PR コメントの比較画像で差分が意図通りか確認してマージすればよい。マージ後に main で期待画像が再生成される
- **意図しない差分**：リグレッションなので実装を修正する
- アーティファクトには保持期限があるため、main が長期間動いていないと期待画像のダウンロードに失敗することがある。その場合は main で `upload_expected_image.yml` を再実行する

#### GCS 運用と gcs-upload.sh の位置づけ

リポジトリ直下の `gcs-upload.sh` は、差分画像（`*_compare.png`）を GCS へアップロードする構想の名残であり、**現在は使われていない**（`gsutil` 行はコメントアウトされたまま、CI からも参照されていない）。期待画像・差分画像の受け渡しはすべて上記の GitHub Actions アーティファクト + companion ブランチで完結している。

---

## VRT カバレッジレポート

### 仕組み

`app` モジュールの `jacocoVrtReport` タスク（`AndroidJacocoConventionPlugin` が登録）が、VRT 実行時に JaCoCo でカバレッジを計測する。
撮影対象の Composable は複数モジュールに分散しているため、依存モジュールのクラスを集約して `jp.kztproject.**` のみをレポート対象にしている。

VRT で描画されないコードはノイズになるため除外済み：

- 非UI層（`data` / `application` / `domain`）と ViewModel（これらの網羅はユニットテストの領分）
- Activity / Navigation / DI / 例外クラス
- Hilt / Room / Showkase / Compose コンパイラの生成コード

つまりレポートの数値は「**UI（Composable）コードのうち、VRT のスクリーンショットで実際に描画された割合**」を表す。

### 実行とCI

```bash
# レポート生成（app/build/reports/jacoco/jacocoVrtReport/）
./gradlew :app:jacocoVrtReport

# 下限チェック（INSTRUCTION 60% を下回ると失敗）
./gradlew :app:jacocoVrtCoverageVerification
```

| ワークフロー | トリガー | 役割 |
|-------------|---------|------|
| `vrt-coverage-check.yml` | PR | 下限（INSTRUCTION 60%）を下回ったらマージをブロック。失敗時はレポートをアーティファクトに残す |
| `vrt-coverage.yml` | 毎週月曜 09:00 JST / 手動 | レポートを生成しアーティファクト `vrt-coverage-report` として90日保持 |

### レポートの見方

1. CI の実行結果からアーティファクト `vrt-coverage-report` をダウンロードする（ローカル実行なら `app/build/reports/jacoco/jacocoVrtReport/html/index.html` を開く）
2. トップページの **INSTRUCTION カバレッジ**（Missed Instructions）が下限チェックの対象値
3. パッケージ → クラスと掘っていくと、**赤い行 = VRT で描画されていない UI コード**が特定できる
4. カバレッジが低いクラスは「Preview が無い」「特定の状態（エラー・空など）の Preview が無い」ことが原因のことが多い。該当する状態の `@Preview` を追加して撮影対象に載せる

カバレッジ改善タスクの例: 「Add VRT for TodoDetailBottomSheet (missing state coverage)」のように、不足している状態の Preview を追加する PR を作る。

---

## E2E テスト（Maestro）

主要ユーザーフローの E2E テストは `maestro-tests/` に YAML フローとして整備済み（CI 化は未対応）。
作成・実行方法は `/maestro-e2e` スキルを参照。
