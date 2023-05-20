package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.todo.domain.Todo
import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class DeleteTodoInteractor @Inject constructor(
        private val todoRepository: ITodoRepository
) : DeleteTodoUseCase {
    override suspend fun execute(todo: Todo) {
        todoRepository.delete(todo)
    }
}
