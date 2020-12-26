package jp.kztproject.rewardedtodo.di

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.data.ticket.TicketRepository

@Module
interface RepositoriesModule {

    @Binds
    fun bindTicketRepositoryModule(repository: TicketRepository): ITicketRepository
}