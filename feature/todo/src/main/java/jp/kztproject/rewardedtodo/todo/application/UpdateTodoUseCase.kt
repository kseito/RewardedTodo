package jp.kztproject.rewardedtodo.todo.application

import jp.kztproject.rewardedtodo.todo.domain.EditingTodo


interface UpdateTodoUseCase {
    suspend fun execute(todo: EditingTodo) : Result<Unit>
}
