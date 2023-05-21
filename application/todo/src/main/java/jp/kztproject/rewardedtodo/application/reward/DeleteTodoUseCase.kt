package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.todo.Todo

interface DeleteTodoUseCase {
    suspend fun execute(todo: Todo)
}
