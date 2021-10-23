package jp.kztproject.rewardedtodo.todo.application

import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class FetchTodoListInteractor @Inject constructor(private val todoRepository: ITodoRepository) : FetchTodoListUseCase {
    override suspend fun execute() {
        todoRepository.sync()
    }
}