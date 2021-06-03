package jp.kztproject.rewardedtodo.di.auth

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import jp.kztproject.rewardedtodo.presentation.auth.todoist.TodoistAssociationViewModel
import project.seito.screen_transition.di.ViewModelKey

@Module
interface TodoistAssociationFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(TodoistAssociationViewModel::class)
    fun bindVideoTrimMuteViewModel(viewModel: TodoistAssociationViewModel): ViewModel
}