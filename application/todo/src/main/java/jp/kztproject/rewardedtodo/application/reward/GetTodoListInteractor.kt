package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodoListInteractor @Inject constructor(private val todoRepository: ITodoRepository) : GetTodoListUseCase {
    override fun execute(): Flow<List<Todo>> {
        return todoRepository.findAll()
    }
}
