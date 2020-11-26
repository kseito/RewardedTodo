package kztproject.jp.splacounter.di

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.data.ticket.ITicketRepository
import kztproject.jp.splacounter.data.ticket.TicketRepository

@Module
interface RepositoriesModule {

    @Binds
    fun bindTicketRepositoryModule(repository: TicketRepository): ITicketRepository
}