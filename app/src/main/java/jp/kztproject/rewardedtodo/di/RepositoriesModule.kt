package jp.kztproject.rewardedtodo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import jp.kztproject.rewardedtodo.data.auth.TodoistAccessTokenRepository
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.data.ticket.TicketRepository
import jp.kztproject.rewardedtodo.data.todo.ApiTokenRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository

@InstallIn(SingletonComponent::class)
@Module
interface RepositoriesModule {

    @Binds
    fun bindTicketRepositoryModule(repository: TicketRepository): ITicketRepository

    @Binds
    fun bindTodoistAccessTokenRepository(repository: TodoistAccessTokenRepository): ITodoistAccessTokenRepository

    @Binds
    fun bindApiTokenRepository(repository: ApiTokenRepository): IApiTokenRepository
}
