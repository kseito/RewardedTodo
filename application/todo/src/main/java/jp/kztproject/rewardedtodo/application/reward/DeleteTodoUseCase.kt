package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.todo.domain.Todo

interface DeleteTodoUseCase {
    suspend fun execute(todo: Todo)
}
