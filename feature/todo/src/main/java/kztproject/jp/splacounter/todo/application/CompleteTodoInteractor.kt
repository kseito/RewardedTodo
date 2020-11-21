package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class CompleteTodoInteractor @Inject constructor(
        private val todoRepository: ITodoRepository
): CompleteTodoUseCase {

    override suspend fun execute(todo: Todo) {
        if (!todo.isRepeat) {
            todoRepository.delete(todo)
        }
    }
}