package jp.kztproject.rewardedtodo.auth.di

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.auth.repository.AuthRepository
import jp.kztproject.rewardedtodo.auth.repository.IAuthRepository

@Module
interface AuthRepositoryModule {

    @Binds
    fun bindAuthRepository(repository: AuthRepository): IAuthRepository
}