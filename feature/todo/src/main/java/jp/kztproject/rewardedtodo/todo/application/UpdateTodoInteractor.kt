package jp.kztproject.rewardedtodo.todo.application

import jp.kztproject.rewardedtodo.todo.domain.Todo
import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class UpdateTodoInteractor @Inject constructor(
        private val todoRepository: ITodoRepository
) : UpdateTodoUseCase {

    override suspend fun execute(todo: Todo) {
        todoRepository.update(todo)
    }
}