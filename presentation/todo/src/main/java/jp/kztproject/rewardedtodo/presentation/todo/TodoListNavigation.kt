package jp.kztproject.rewardedtodo.presentation.todo

import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val TODO_SCREEN = "todo_screen"

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.todoListScreen() {
    composable(route = TODO_SCREEN) {
        TodoListScreenWithBottomSheet()
    }
}
