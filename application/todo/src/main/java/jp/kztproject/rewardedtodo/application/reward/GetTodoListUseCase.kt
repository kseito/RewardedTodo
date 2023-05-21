package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.todo.Todo
import kotlinx.coroutines.flow.Flow


interface GetTodoListUseCase {
    fun execute(): Flow<List<Todo>>
}
