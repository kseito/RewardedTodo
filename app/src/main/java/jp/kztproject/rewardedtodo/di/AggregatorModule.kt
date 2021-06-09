package jp.kztproject.rewardedtodo.di

import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.di.auth.TodoistAuthModule
import project.seito.screen_transition.di.ViewModelModule

@InstallIn(SingletonComponent::class)
@Module(includes = [
    AndroidInjectionModule::class,
    AppModule::class,
    HomeActivityModule::class,
    TodoistAuthActivityModule::class,
    ViewModelModule::class,
    RepositoriesModule::class,
    TodoistAuthModule::class])
interface AggregatorModule