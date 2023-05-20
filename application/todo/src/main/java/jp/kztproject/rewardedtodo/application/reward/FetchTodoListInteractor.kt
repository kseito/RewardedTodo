package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class FetchTodoListInteractor @Inject constructor(private val todoRepository: ITodoRepository) : FetchTodoListUseCase {
    override suspend fun execute() {
        todoRepository.sync()
    }
}
