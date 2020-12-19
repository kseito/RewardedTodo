package jp.kztproject.rewardedtodo.todo.application

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.todo.domain.Todo


interface GetTodoListUseCase {
    fun execute(): Flow<List<Todo>>
}