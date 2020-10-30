package kztproject.jp.splacounter.todo.application

import kotlinx.coroutines.flow.Flow
import kztproject.jp.splacounter.todo.domain.Todo


interface GetTodoListUseCase {
    fun execute(): Flow<List<Todo>>
}