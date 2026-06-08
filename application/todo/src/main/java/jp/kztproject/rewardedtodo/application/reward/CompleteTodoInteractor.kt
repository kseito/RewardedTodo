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
        // Todoist未連携時はローカルへ加算、連携時はNetworkTicketRepositoryがno-op（サーバWebhook加算）。
        // 振り分けは ITicketRepository の実装である TicketRepository が担う。
        ticketRepository.addTicket(todo.numberOfTicketsObtained)
    }
}
