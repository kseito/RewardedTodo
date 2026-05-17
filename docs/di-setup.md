# Hilt DI 構成

全てのHiltモジュールは `app` モジュールの `di/` パッケージに集約されている。
各feature/data/applicationモジュール自体にはHiltモジュールを持たず、バインド設定はappに一元管理される。

---

## ファイル配置

```
app/src/main/java/.../di/
├── AggregatorModule.kt         ← 最上位の集約モジュール
├── AppModule.kt                ← DataStore<Preferences> の提供
├── RepositoriesModule.kt       ← TicketRepository, ApiTokenRepository のバインド
├── UseCaseModule.kt            ← APIトークン関連UseCaseのバインド
├── reward/
│   ├── RewardModule.kt                ← 報酬系の集約モジュール
│   ├── RewardRepositoryModule.kt      ← IRewardRepository のバインド
│   ├── RewardDatabaseModule.kt        ← Room DB + RewardDao の提供
│   ├── RewardListViewModelModule.kt   ← 報酬一覧で使うUseCaseのバインド
│   └── RewardDetailViewModelModule.kt ← 報酬詳細で使うUseCaseのバインド
├── todo/
│   ├── TodoRepositoryModule.kt        ← ITodoRepository のバインド
│   ├── TodoDatabaseModule.kt          ← Todo用 Room DB の提供
│   └── TodoListFragmentModule.kt      ← Todo一覧で使うUseCaseのバインド
├── ticket/
│   └── TicketNetworkModule.kt         ← RewardServerApi (Retrofit) の提供
└── auth/
    └── TodoistApiModule.kt            ← TodoistApi (Retrofit) の提供
```

---

## モジュール階層

```
AggregatorModule (@SingletonComponent)
├── AppModule
├── RepositoriesModule
└── UseCaseModule

RewardModule (@SingletonComponent) ※AggregatorModuleとは別系統
├── RewardRepositoryModule
├── RewardListViewModelModule  (@FragmentComponent)
└── RewardDetailViewModelModule (@FragmentComponent)
```

> `RewardModule` は `AggregatorModule` に含まれていないが、Hiltが自動的にアプリ全体で認識する。

---

## スコープ別モジュール一覧

### SingletonComponent（アプリ全体で単一インスタンス）

リポジトリ実装・DB・APIクライアントはすべてSingletonにする。

| モジュール | 提供/バインドするもの |
|-----------|-------------------|
| `AppModule` | `DataStore<Preferences>`（SharedPreferences代替） |
| `RepositoriesModule` | `ITicketRepository` → `NetworkTicketRepository` |
| `RepositoriesModule` | `IApiTokenRepository` → `ApiTokenRepository` |
| `UseCaseModule` | APIトークン関連4UseCase（Save/Validate/Get/Delete） |
| `RewardRepositoryModule` | `IRewardRepository` → `RewardRepository` |
| `RewardDatabaseModule` | `AppDatabase`（Room）, `RewardDao` |
| `TicketNetworkModule` | `RewardServerApi`（Retrofit、Moshi、OkHttp） |
| `TodoistApiModule` | `TodoistApi`（Retrofit + Bearerトークン自動挿入） |

### ActivityRetainedComponent（ViewModel毎のインスタンス）

`@HiltViewModel` で使うUseCaseは `ActivityRetainedComponent`（または `ViewModelComponent`）にする。
`FragmentComponent` は `ViewModelComponent` の祖先ではないため、`@HiltViewModel` から注入できない。

| モジュール | バインドするUseCase |
|-----------|-----------------|
| `RewardListViewModelModule` | `LotteryUseCase`, `BatchLotteryUseCase`, `GetRewardsUseCase`, `GetPointUseCase` |
| `RewardDetailViewModelModule` | `GetRewardUseCase`, `DeleteRewardUseCase`, `SaveRewardUseCase` |
| `TodoListFragmentModule` | Todo関連UseCase |

---

## 重要な実装パターン

### APIクライアントへのトークン自動挿入

`TodoistApi` のOkHttpInterceptorでBearerトークンを自動挿入している。
トークンはリクエスト時に `IApiTokenRepository` から都度取得する。

```kotlin
// app/di/auth/TodoistApiModule.kt
.addInterceptor { chain ->
    val token = runBlocking {
        apiTokenRepository.getToken()?.value.orEmpty()
    }
    val request = chain.request().newBuilder()
        .header("Authorization", "Bearer $token")
        .build()
    chain.proceed(request)
}
```

### @Binds と @Provides の使い分け

| アノテーション | 使う場面 | 例 |
|-------------|---------|---|
| `@Binds` | インターフェース→実装クラスの紐づけ | `IRewardRepository` → `RewardRepository` |
| `@Provides` | Retrofitなど複数の依存を組み合わせて生成するとき | `RewardServerApi`, `AppDatabase` |

---

## 新しいドメインを追加するときのDI手順

1. `app/di/{domain}/` ディレクトリを作成
2. リポジトリのバインドモジュールを作成（`@InstallIn(SingletonComponent::class)`）
3. DB/APIが必要なら提供モジュールを作成（`@Provides @Singleton`）
4. UseCase用のバインドモジュールを作成（`@InstallIn(ActivityRetainedComponent::class)`）
5. 集約モジュール（`XxxModule`）を作成し、上記をまとめて `includes` に列挙

詳細な手順は `docs/how-to-add-new-feature.md` を参照。
