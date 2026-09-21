package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.reward.repository.ITicketRepository
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoRepository
import javax.inject.Inject

class CompleteTodoInteractor @Inject constructor(
    private val todoRepository: ITodoRepository,
    private val ticketRepository: ITicketRepository,
) : CompleteTodoUseCase {

    override suspend fun execute(todo: Todo) {
        todoRepository.complete(todo)
        // 加算はサーバー側のTodoist Webhookが行うため、アプリからの呼び出しは何もしない。
        ticketRepository.addTicket(todo.numberOfTicketsObtained)
    }
}
