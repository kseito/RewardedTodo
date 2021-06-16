package jp.kztproject.rewardedtodo.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(includes = [
    AppModule::class,
    RepositoriesModule::class])
interface AggregatorModule