# 新しいユースケースの追加手順

Clean Architectureの各レイヤーを順番に実装する。
ここでは「報酬ドメインに新しいユースケースを追加する」ケースを例として説明する。

---

## レイヤー構成の確認

```
domain/reward/          ← リポジトリインターフェース（必要なら追加）
application/reward/     ← UseCaseインターフェース + Interactor実装  ★主な作業場所
data/reward/            ← リポジトリ実装（必要なら追加）
feature/reward/         ← ViewModel でUseCaseを呼び出す
app/src/.../di/reward/  ← Hilt DIモジュール（バインド登録）
```

> **重要**: UseCaseインターフェースはdomain層ではなく **application層** に定義する。

---

## ステップ1: Application層にUseCaseインターフェースを作成

`application/reward/src/main/java/.../application/reward/usecase/` に新しいファイルを作成。

```kotlin
// GetRewardUseCase.kt の例
package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId

interface GetRewardUseCase {
    suspend fun execute(id: RewardId): Reward?
}
```

参照: `application/reward/src/main/java/.../application/reward/usecase/GetRewardUseCase.kt`

---

## ステップ2: Application層にInteractorを実装

同じディレクトリにInteractorクラスを作成。必ずUseCaseインターフェースを実装する。

```kotlin
// GetRewardInteractor.kt の例
package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import javax.inject.Inject

class GetRewardInteractor @Inject constructor(
    private val rewardRepository: IRewardRepository
) : GetRewardUseCase {
    override suspend fun execute(id: RewardId): Reward? {
        return rewardRepository.findBy(id)
    }
}
```

参照: `application/reward/src/main/java/.../application/reward/usecase/GetRewardInteractor.kt`

**ポイント**
- `@Inject constructor` を必ず付ける
- コンストラクタにはDomain層のインターフェース（`IRewardRepository`）を受け取る
- バリデーションが必要な場合は `kotlin.Result` を戻り値にして `Result.failure(XxxException())` を返す

---

## ステップ3: Hilt DIモジュールにバインドを追加

`app/src/main/java/.../di/reward/` 配下の対応するモジュールファイルに `@Binds` を追加する。

```kotlin
// RewardDetailViewModelModule.kt の例
@InstallIn(ActivityRetainedComponent::class)
@Module
interface RewardDetailViewModelModule {
    @Binds
    fun bindGetRewardUseCase(useCase: GetRewardInteractor): GetRewardUseCase

    // 既存のバインド...
}
```

参照: `app/src/main/java/.../di/reward/RewardDetailViewModelModule.kt`

**スコープの選び方**

| スコープ | アノテーション | 使う場面 |
|---------|-------------|---------|
| アプリ全体で単一 | `@InstallIn(SingletonComponent::class)` | リポジトリ、DB、APIクライアント |
| ViewModel毎 | `@InstallIn(ActivityRetainedComponent::class)` | ViewModel経由で使うUseCase（`@HiltViewModel` は `FragmentComponent` から注入不可） |

**モジュールファイルが存在しない場合**

新しいモジュールファイルを作成し、親モジュール (`RewardModule.kt`) の `includes` に追加する。

```kotlin
// RewardModule.kt
@InstallIn(SingletonComponent::class)
@Module(
    includes = [
        RewardRepositoryModule::class,
        RewardListViewModelModule::class,
        RewardDetailViewModelModule::class,
        YourNewModule::class,  // ← 追加
    ]
)
abstract class RewardModule
```

参照: `app/src/main/java/.../di/reward/RewardModule.kt`

---

## ステップ4: ViewModelからUseCaseを呼び出す

`feature/reward/` 配下の対応するViewModelにUseCaseをinjectして呼び出す。

```kotlin
@HiltViewModel
class RewardDetailViewModel @Inject constructor(
    private val getRewardUseCase: GetRewardUseCase,  // ← 追加
    private val deleteRewardUseCase: DeleteRewardUseCase,
) : ViewModel() {

    fun loadReward(id: RewardId) {
        viewModelScope.launch {
            val reward = getRewardUseCase.execute(id)
            // StateFlow等に反映
        }
    }
}
```

参照: `feature/reward/src/main/java/.../feature/reward/list/RewardListViewModel.kt`

---

## （必要な場合）Domain層にリポジトリメソッドを追加

既存のリポジトリインターフェースにメソッドが足りない場合のみ対応する。

**1. Domain層のインターフェースに追加**

```kotlin
// domain/reward/repository/IRewardRepository.kt
interface IRewardRepository {
    suspend fun findBy(id: RewardId): Reward?
    suspend fun newMethod(): SomeType  // ← 追加
    // ...
}
```

**2. Data層の実装クラスに追加**

```kotlin
// data/reward/repository/RewardRepository.kt
class RewardRepository @Inject constructor(
    private val rewardDao: RewardDao
) : IRewardRepository {
    override suspend fun newMethod(): SomeType {
        return withContext(Dispatchers.IO) {
            rewardDao.someQuery()?.convert()
        }
    }
}
```

参照:
- `domain/reward/src/main/java/.../domain/reward/repository/IRewardRepository.kt`
- `data/reward/src/main/java/.../data/reward/repository/RewardRepository.kt`

---

## チェックリスト

```
[ ] application/reward/usecase/ に XxxUseCase.kt（インターフェース）を作成
[ ] application/reward/usecase/ に XxxInteractor.kt（実装）を作成
    [ ] @Inject constructor を付けた
    [ ] コンストラクタにはDomain層のインターフェースを受け取る
[ ] app/di/reward/ の対応するモジュールに @Binds を追加
    [ ] 新しいモジュールを作った場合、RewardModule.kt の includes に追加した
[ ] feature/reward/ のViewModelにUseCaseをinjectした
[ ] （必要なら）IRewardRepository にメソッドを追加し、RewardRepository にも実装した
```

---

## エラーハンドリングが必要な場合

バリデーションがあるUseCaseは `Result<T>` を返す。エラークラスは `application/reward/model/error/` に置く。

```kotlin
class SaveRewardInteractor @Inject constructor(
    private val rewardRepository: IRewardRepository
) : SaveRewardUseCase {
    override suspend fun execute(reward: RewardInput): Result<Unit> {
        return when {
            reward.name.isNullOrEmpty() -> Result.failure(RewardTitleEmptyException())
            reward.probability == null  -> Result.failure(RewardProbabilityEmptyException())
            else -> {
                rewardRepository.createOrUpdate(reward)
                Result.success(Unit)
            }
        }
    }
}
```

参照: `application/reward/src/main/java/.../application/reward/usecase/SaveRewardInteractor.kt`
