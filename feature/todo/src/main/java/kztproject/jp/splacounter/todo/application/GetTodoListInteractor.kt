package kztproject.jp.splacounter.todo.application

import kztproject.jp.splacounter.todo.domain.Todo
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class GetTodoListInteractor @Inject constructor(private val todoRepository: ITodoRepository) : GetTodoListUseCase {
    override suspend fun execute(): List<Todo> {
        return todoRepository.findAll()
    }
}