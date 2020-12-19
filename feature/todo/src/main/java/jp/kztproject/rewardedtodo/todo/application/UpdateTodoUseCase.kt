package jp.kztproject.rewardedtodo.todo.application

import jp.kztproject.rewardedtodo.todo.domain.Todo

interface UpdateTodoUseCase {
    suspend fun execute(todo: Todo)
}