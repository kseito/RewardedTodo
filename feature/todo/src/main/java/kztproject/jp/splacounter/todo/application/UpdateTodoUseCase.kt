package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo

interface UpdateTodoUseCase {
    suspend fun execute(todo: Todo)
}