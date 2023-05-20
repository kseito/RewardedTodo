package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.todo.domain.Todo

interface CompleteTodoUseCase {
    suspend fun execute(todo: Todo)
}
