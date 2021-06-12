package jp.kztproject.rewardedtodo.di

import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(includes = [
    AndroidInjectionModule::class,
    AppModule::class,
    RepositoriesModule::class])
interface AggregatorModule