package jp.kztproject.rewardedtodo.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import jp.kztproject.rewardedtodo.di.auth.TodoistApiModule
import jp.kztproject.rewardedtodo.di.scope.ActivityScope
import jp.kztproject.rewardedtodo.presentation.auth.todoist.TodoistAuthActivity
import jp.kztproject.rewardedtodo.presentation.auth.todoist.TodoistAuthViewModel
import project.seito.screen_transition.di.ViewModelKey

@Module(includes = [TodoistApiModule::class])
interface TodoistAuthActivityModule {

    @ActivityScope
    @ContributesAndroidInjector
    fun contributeTodoistAuthActivity(): TodoistAuthActivity

    @Binds
    @IntoMap
    @ViewModelKey(TodoistAuthViewModel::class)
    fun bindTodoistAuthViewModel(viewModel: TodoistAuthViewModel): ViewModel
}