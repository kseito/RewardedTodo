package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class DeleteTodoInteractor @Inject constructor(
        private val todoRepository: ITodoRepository
) : DeleteTodoUseCase {
    override suspend fun execute(todo: Todo) {
        todoRepository.delete(todo)
    }
}