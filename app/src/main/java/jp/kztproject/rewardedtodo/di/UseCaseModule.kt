package jp.kztproject.rewardedtodo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.application.todo.CompleteTodoistAuthInteractor
import jp.kztproject.rewardedtodo.application.todo.CompleteTodoistAuthUseCase
import jp.kztproject.rewardedtodo.application.todo.DisconnectTodoistInteractor
import jp.kztproject.rewardedtodo.application.todo.DisconnectTodoistUseCase
import jp.kztproject.rewardedtodo.application.todo.GetTodoistCredentialInteractor
import jp.kztproject.rewardedtodo.application.todo.GetTodoistCredentialUseCase
import jp.kztproject.rewardedtodo.application.todo.GetValidAccessTokenInteractor
import jp.kztproject.rewardedtodo.application.todo.GetValidAccessTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.RefreshTodoistTokenInteractor
import jp.kztproject.rewardedtodo.application.todo.RefreshTodoistTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.StartTodoistAuthInteractor
import jp.kztproject.rewardedtodo.application.todo.StartTodoistAuthUseCase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface UseCaseModule {

    @Binds
    fun bindGetTodoistCredentialUseCase(interactor: GetTodoistCredentialInteractor): GetTodoistCredentialUseCase

    @Binds
    fun bindStartTodoistAuthUseCase(interactor: StartTodoistAuthInteractor): StartTodoistAuthUseCase

    @Binds
    fun bindCompleteTodoistAuthUseCase(interactor: CompleteTodoistAuthInteractor): CompleteTodoistAuthUseCase

    // リフレッシュの並走でトークンが相互に無効化されないよう、実装側でMutexを持つ単一インスタンスを共有する
    @Binds
    @Singleton
    fun bindRefreshTodoistTokenUseCase(interactor: RefreshTodoistTokenInteractor): RefreshTodoistTokenUseCase

    @Binds
    fun bindGetValidAccessTokenUseCase(interactor: GetValidAccessTokenInteractor): GetValidAccessTokenUseCase

    @Binds
    fun bindDisconnectTodoistUseCase(interactor: DisconnectTodoistInteractor): DisconnectTodoistUseCase
}
