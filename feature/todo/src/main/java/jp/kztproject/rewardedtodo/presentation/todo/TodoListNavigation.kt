package jp.kztproject.rewardedtodo.presentation.todo

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object TodoListRoute : NavKey

fun EntryProviderScope<NavKey>.todoListScreen() {
    entry<TodoListRoute> {
        TodoListScreenWithBottomSheet()
    }
}
