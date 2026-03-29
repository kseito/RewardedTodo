package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoRepository
import javax.inject.Inject

class CompleteTodoInteractor @Inject constructor(private val todoRepository: ITodoRepository) : CompleteTodoUseCase {

    override suspend fun execute(todo: Todo) {
        todoRepository.complete(todo)
    }
}
