package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo

interface DeleteTodoUseCase {
    suspend fun execute(todo: Todo)
}