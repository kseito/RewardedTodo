package jp.kztproject.rewardedtodo.todo.application

import jp.kztproject.rewardedtodo.todo.domain.Todo

interface CompleteTodoUseCase {
    suspend fun execute(todo: Todo)
}