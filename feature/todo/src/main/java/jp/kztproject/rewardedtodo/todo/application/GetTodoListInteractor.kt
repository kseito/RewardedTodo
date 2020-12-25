package jp.kztproject.rewardedtodo.todo.application

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.todo.domain.Todo
import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class GetTodoListInteractor @Inject constructor(private val todoRepository: ITodoRepository) : GetTodoListUseCase {
    override fun execute(): Flow<List<Todo>> {
        return todoRepository.findAll()
    }
}