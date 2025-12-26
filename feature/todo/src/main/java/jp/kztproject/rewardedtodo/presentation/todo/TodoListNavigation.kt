package jp.kztproject.rewardedtodo.presentation.todo

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val TODO_SCREEN = "todo_screen"

fun NavGraphBuilder.todoListScreen() {
    composable(route = TODO_SCREEN) {
        TodoListScreenWithBottomSheet()
    }
}
