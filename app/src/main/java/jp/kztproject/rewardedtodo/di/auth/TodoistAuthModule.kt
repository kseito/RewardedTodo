package jp.kztproject.rewardedtodo.di.auth

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.kztproject.rewardedtodo.presentation.auth.todoist.TodoistAssociationFragment

@Module
interface TodoistAuthModule {

    @ContributesAndroidInjector(modules = [TodoistAssociationFragmentModule::class])
    fun contributeTodoListFragment(): TodoistAssociationFragment
}