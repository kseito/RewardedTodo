package kztproject.jp.splacounter.di.todo

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.data.todo.repository.TodoRepository
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository

@Module
interface TodoRepositoryModule {

    @Binds
    fun bindTodoRepository(repository: TodoRepository): ITodoRepository
}