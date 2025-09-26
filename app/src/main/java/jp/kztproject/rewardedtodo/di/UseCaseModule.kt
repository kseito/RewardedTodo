package jp.kztproject.rewardedtodo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.application.todo.DeleteApiTokenInteractor
import jp.kztproject.rewardedtodo.application.todo.DeleteApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenInteractor
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.SaveApiTokenInteractor
import jp.kztproject.rewardedtodo.application.todo.SaveApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.ValidateApiTokenInteractor
import jp.kztproject.rewardedtodo.application.todo.ValidateApiTokenUseCase

@InstallIn(SingletonComponent::class)
@Module
interface UseCaseModule {

    @Binds
    fun bindSaveApiTokenUseCase(interactor: SaveApiTokenInteractor): SaveApiTokenUseCase

    @Binds
    fun bindValidateApiTokenUseCase(interactor: ValidateApiTokenInteractor): ValidateApiTokenUseCase

    @Binds
    fun bindGetApiTokenUseCase(interactor: GetApiTokenInteractor): GetApiTokenUseCase

    @Binds
    fun bindDeleteApiTokenUseCase(interactor: DeleteApiTokenInteractor): DeleteApiTokenUseCase
}
