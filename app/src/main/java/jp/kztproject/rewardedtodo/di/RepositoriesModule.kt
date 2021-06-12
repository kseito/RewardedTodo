package jp.kztproject.rewardedtodo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import jp.kztproject.rewardedtodo.data.auth.TodoistAccessTokenRepository
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.data.ticket.TicketRepository

@InstallIn(SingletonComponent::class)
@Module
interface RepositoriesModule {

    @Binds
    fun bindTicketRepositoryModule(repository: TicketRepository): ITicketRepository

    @Binds
    fun bindTodoistAccessTokenRepository(repository: TodoistAccessTokenRepository): ITodoistAccessTokenRepository
}