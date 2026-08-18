package jp.kztproject.rewardedtodo.presentation.todo

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

const val TODO_SCREEN = "todo_screen"

@Serializable
data object TodoListRoute : NavKey

fun NavGraphBuilder.todoListScreen() {
    composable(route = TODO_SCREEN) {
        TodoListScreenWithBottomSheet()
    }
}

fun EntryProviderScope<NavKey>.todoListScreen() {
    entry<TodoListRoute> {
        TodoListScreenWithBottomSheet()
    }
}
