package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.todo.domain.EditingTodo


interface UpdateTodoUseCase {
    suspend fun execute(todo: EditingTodo) : Result<Unit>
}
