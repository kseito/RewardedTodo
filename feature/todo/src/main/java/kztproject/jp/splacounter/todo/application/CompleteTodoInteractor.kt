package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.data.ticket.ITicketRepository
import kztproject.jp.splacounter.todo.domain.Todo
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class CompleteTodoInteractor @Inject constructor(
        private val todoRepository: ITodoRepository,
        private val ticketRepository: ITicketRepository
) : CompleteTodoUseCase {

    override suspend fun execute(todo: Todo) {
        if (!todo.isRepeat) {
            todoRepository.delete(todo)
        }

        ticketRepository.addTicket(todo.numberOfTicketsObtained)
    }
}