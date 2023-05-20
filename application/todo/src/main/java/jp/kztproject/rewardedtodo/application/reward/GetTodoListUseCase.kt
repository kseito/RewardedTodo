package jp.kztproject.rewardedtodo.application.reward

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.todo.domain.Todo


interface GetTodoListUseCase {
    fun execute(): Flow<List<Todo>>
}
