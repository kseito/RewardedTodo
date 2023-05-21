package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoRepository
import javax.inject.Inject

class UpdateTodoInteractor @Inject constructor(
        private val todoRepository: ITodoRepository
) : UpdateTodoUseCase {

    override suspend fun execute(todo: EditingTodo): Result<Unit> {
        return try {
            todo.validate()
            todoRepository.update(todo.toTodo())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
