# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture Overview

This Android project implements **Clean Architecture** with a **multi-modular structure** using MVVM for the presentation layer. The architecture consists of:

- **Domain Layer** (`/domain/reward`, `/domain/todo`) - Business entities, use cases interfaces, repository interfaces
- **Application Layer** (`/application/reward`, `/application/todo`) - Use case implementations (interactors) containing business logic
- **Data Layer** (`/data/*`) - Repository implementations, Room database access, external APIs (Todoist)
- **Feature Layer** (`/feature/*`) - ViewModels, UI screens (mix of Views and Compose), navigation
- **Common Layer** (`/common/*`) - Shared utilities (database, encrypted storage, UI components)

### Key Patterns
- **Use Case/Interactor Pattern**: Each business operation has a use case interface implemented by an interactor
- **Repository Pattern**: Domain defines interfaces, data layer provides implementations
- **Value Objects**: Type-safe domain modeling with inline value classes (`RewardName`, `RewardId`)
- **Dependency Injection**: Dagger Hilt with modular DI setup

## Build Commands

```bash
# Build project
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run unit tests (all modules)
./gradlew testDebugUnitTest

# Run specific module tests
./gradlew :module:testDebugUnitTest

# Run lint checks
./gradlew lintDebug

# Format Kotlin code
./gradlew spotlessKotlinApply

# Check code style
./gradlew spotlessCheck

# Screenshot comparison tests (Roborazzi)
./gradlew compareRoborazziDebug
```

## Module Structure

The project uses 34+ modules organized by domain:
- Each domain area (reward, todo, auth) has separate modules for domain, application, data, and feature layers
- Common modules provide shared functionality
- Build-logic contains custom Gradle convention plugins

## Testing

- Unit tests are located in each module's `src/test/` directory
- Uses JUnit, Truth, MockK, and Robolectric for testing
- Screenshot testing with Roborazzi for visual regression testing
- Test utilities in `/test/` modules (e.g., `DummyCreator` for test data)

## Development Notes

- **Dependencies**: Use version catalog (`gradle/libs.versions.toml`) for dependency management
- **Architecture**: Follow strict layer separation - domain layer has no Android dependencies
- **Code Style**: Spotless enforces Kotlin code formatting
- **Branch Strategy**: Feature branches from main, staging deployments from `stg-release/*` branches
- **CI/CD**: GitHub Actions for testing and screenshot comparison

## 開発プロセス

仕様を与えて「仕様書作成 → 実装 → 動作確認 → Draft PR作成」まで一貫して進めるには、`/feature <仕様>` スキルを使う。
5フェーズ構成で、**仕様書レビュー後**と**PR作成前**の2箇所で承認のため停止する。仕様書は `docs/specs/`（雛形: `docs/specs/_template.md`）に作成される。

## レビュー方針

機械の網で防げるものは機械に任せ、人の目は機械が苦手な箇所（仕様・テストのオラクル・不可逆な変更・設計判断）だけに集中させる。詳細は `docs/review-policy.md` を参照。

## 機能実装時の参照ドキュメント

新しい機能を実装する際は、実装前に以下のドキュメントを読んでください：

- `docs/domain-model.md` - ドメインモデルとビジネスルール
- `docs/how-to-add-new-feature.md` - 機能追加の手順（レイヤー順・DI登録まで）
- `docs/module-dependency.md` - モジュール依存関係と禁止ルール
- `docs/di-setup.md` - Hilt DI構成とファイル配置
- `docs/adr/` - 設計判断の記録（Architecture Decision Records）

## Naming Conventions

### クラス命名規則

| 種別 | パターン | 例 |
|-----|---------|---|
| リポジトリインターフェース | `I` + ドメイン名 + `Repository` | `IRewardRepository` |
| リポジトリ実装 | ドメイン名 + `Repository` | `RewardRepository`, `NetworkTicketRepository` |
| ユースケースインターフェース | 動詞 + ドメイン名 + `UseCase` | `GetRewardUseCase`, `SaveRewardUseCase` |
| インタラクター | 動詞 + ドメイン名 + `Interactor` | `GetRewardInteractor` |
| ViewModel | 画面名 + `ViewModel` | `RewardListViewModel` |
| Compose Screen | 画面名 + `Screen` | `RewardListScreen` |
| Navigation | 画面名 + `Navigation` | `RewardListNavigation` |
| DAO | ドメイン名 + `Dao` | `RewardDao` |
| Room Entity | ドメイン名 + `Entity` | `RewardEntity` |
| API インターフェース | サービス名 + `Api` | `TodoistApi`, `RewardServerApi` |
| 例外 | 説明 + `Exception` | `LackOfTicketsException` |
| Value Object | ドメイン名 + 属性名（suffix なし） | `RewardId`, `RewardName`, `Probability` |
| ファクトリ | ドメイン名 + `Factory` | `LotteryBoxFactory` |
| Bottom Sheet | 名前 + `BottomSheet` | `RewardDetailBottomSheet` |

### Value Objectの使い方

- `@JvmInline value class` で定義し、プリミティブ型をラップして型安全性を確保する
- 取り違えると問題になるフィールド（ID、名前、確率など）に使う
- domain層にのみ定義し、data層ではEntityへの変換時にアンラップする

```kotlin
// 良い例: 型で区別できる
fun findBy(id: RewardId): Reward?

// 悪い例: IntをRewardIdと間違えやすい
fun findBy(id: Int): Reward?
```

### パッケージ構造

ルートパッケージ: `jp.kztproject.rewardedtodo`

```
{root}.domain.{domain}            # ドメインモデル・リポジトリIF
{root}.domain.{domain}.repository # リポジトリインターフェース
{root}.domain.{domain}.exception  # ドメイン例外
{root}.application.{domain}       # ユースケースIF
{root}.application.{domain}.usecase    # UseCase + Interactor
{root}.application.{domain}.model      # アプリ層のモデル
{root}.application.{domain}.model.error # アプリ層の例外
{root}.data.{domain}              # リポジトリ実装
{root}.data.{domain}.repository   # リポジトリ実装クラス
{root}.data.{domain}.database     # Room関連
{root}.data.{domain}.network      # APIクライアント
{root}.data.{domain}.network.model # リクエスト/レスポンスDTO
{root}.feature.{domain}           # ViewModel
{root}.feature.{domain}.list      # 一覧画面
{root}.feature.{domain}.detail    # 詳細画面
```

## Commit Guideline

- Only commit when:
    - ALL tests are passing
    - ALL compiler/linter warnings have been resolved
- コミットメッセージは **英語の動詞プレフィックス ＋ 日本語の本文** で記述する。粒度は原則「1ファイル1コミット」とし、機能実装・DI設定・テスト・spotless適用は必ず別コミットにする。

| プレフィックス | 用途 | 例 |
|-------------|------|-----|
| `Add` | 新規ファイル・機能の追加 | `Add 仕様書テンプレート` |
| `Update` | 既存機能の変更・改修 | `Update RewardListViewModelに抽選通信中フラグを追加` |
| `Remove` | コード・ファイルの削除 | `Remove 古い動画アップロード導線` |
| `Fix` | バグ修正 | `Fix 単体テストがこける問題を修正` |
| `Clean` | リファクタリング・整理 | `Clean spotless適用` |
| `Rename` | 名前変更 | `Rename .java to .kt` |

詳細なコミット粒度・実装順序のルールは `/implement` スキルに従う。