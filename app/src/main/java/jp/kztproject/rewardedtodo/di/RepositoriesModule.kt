package jp.kztproject.rewardedtodo.di

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import jp.kztproject.rewardedtodo.data.auth.TodoistAccessTokenRepository
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.data.ticket.TicketRepository

@Module
interface RepositoriesModule {

    @Binds
    fun bindTicketRepositoryModule(repository: TicketRepository): ITicketRepository

    @Binds
    fun bindTodoistAccessTokenRepository(repository: TodoistAccessTokenRepository): ITodoistAccessTokenRepository
}