package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo
import javax.inject.Inject

class GetTodoListInteractor @Inject constructor() : GetTodoListUseCase {
    override suspend fun execute(): List<Todo> {
        return listOf(1, 2, 3, 4, 5)
                .map {
                    Todo(it.toLong(), "Test Todo $it", 0.5f, true)
                }
    }
}