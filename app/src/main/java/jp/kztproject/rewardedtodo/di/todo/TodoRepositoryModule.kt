package jp.kztproject.rewardedtodo.di.todo

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.data.todo.repository.TodoRepository
import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository

@Module
interface TodoRepositoryModule {

    @Binds
    fun bindTodoRepository(repository: TodoRepository): ITodoRepository
}