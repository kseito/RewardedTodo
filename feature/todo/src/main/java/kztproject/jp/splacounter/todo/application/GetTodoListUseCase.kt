package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo


interface GetTodoListUseCase {
    suspend fun execute(): List<Todo>
}