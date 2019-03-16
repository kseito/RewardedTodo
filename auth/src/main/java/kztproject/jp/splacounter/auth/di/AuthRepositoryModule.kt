package kztproject.jp.splacounter.auth.di

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.auth.repository.AuthRepository
import kztproject.jp.splacounter.auth.repository.IAuthRepository

@Module
interface AuthRepositoryModule {

    @Binds
    fun bindAuthRepository(repository: AuthRepository): IAuthRepository
}