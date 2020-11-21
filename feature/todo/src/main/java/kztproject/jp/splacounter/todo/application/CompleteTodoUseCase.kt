package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo

interface CompleteTodoUseCase {
    suspend fun execute(todo: Todo)
}