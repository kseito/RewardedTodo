package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.todo.EditingTodo


interface UpdateTodoUseCase {
    suspend fun execute(todo: EditingTodo) : Result<Unit>
}
