package jp.kztproject.rewardedtodo.di.todo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.data.todo.repository.TodoRepository
import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository

@InstallIn(SingletonComponent::class)
@Module
interface TodoRepositoryModule {

    @Binds
    fun bindTodoRepository(repository: TodoRepository): ITodoRepository
}