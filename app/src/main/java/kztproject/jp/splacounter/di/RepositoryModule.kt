package kztproject.jp.splacounter.di

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.auth.repository.AuthRepository
import kztproject.jp.splacounter.auth.repository.IAuthRepository

@Module
interface RepositoryModule {

    @Binds
    fun bindAuthRepository(repository: AuthRepository): IAuthRepository
}