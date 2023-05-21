package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.todo.Todo

interface CompleteTodoUseCase {
    suspend fun execute(todo: Todo)
}
