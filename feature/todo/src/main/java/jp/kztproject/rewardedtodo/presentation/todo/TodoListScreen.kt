@file:OptIn(ExperimentalMaterialApi::class)

package jp.kztproject.rewardedtodo.presentation.todo

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.common.ui.CommonAlertDialog
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.feature.todo.R
import jp.kztproject.rewardedtodo.presentation.reward.list.DarkColorScheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun TodoListScreenWithBottomSheet(viewModel: TodoListViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var selectedTodo: Todo? by remember { mutableStateOf(null) }
    val onTodoItemClicked: (Todo) -> Unit = {
        coroutineScope.launch {
            selectedTodo = it
            bottomSheetState.show()
        }
    }
    val onTodoUpdateSucceed: () -> Unit = {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }
    val onTodoAddClicked: () -> Unit = {
        coroutineScope.launch {
            selectedTodo = null
            bottomSheetState.show()
        }
    }
    val onTodoSaveSelected: (EditingTodo) -> Unit = {
        viewModel.updateTodo(it)
    }
    val onTodoDeleteSelected: (EditingTodo) -> Unit = {
        viewModel.deleteTodo(it)
    }

    TodoDetailBottomSheet(
        todo = selectedTodo,
        bottomSheetState = bottomSheetState,
        onTodoSaveSelected = onTodoSaveSelected,
        onTodoDeleteSelected = onTodoDeleteSelected,
    ) {
        TodoListScreen(
            viewModel = viewModel,
            onTodoItemClicked = onTodoItemClicked,
            onTodoAddClicked = onTodoAddClicked,
            onTodoUpdateSucceed = onTodoUpdateSucceed,
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun TodoListScreen(
    viewModel: TodoListViewModel,
    onTodoAddClicked: () -> Unit,
    onTodoItemClicked: (Todo) -> Unit,
    onTodoUpdateSucceed: () -> Unit,
) {
    val todoList by viewModel.todoList.observeAsState()
    val result by viewModel.result.observeAsState()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.refreshTodoList() },
    )

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize(),
    ) {
        LazyColumn {
            todoList?.let {
                itemsIndexed(
                    items = it,
                    key = { _, todo -> todo.id },
                ) { index, todo ->
                    TodoListItem(
                        todo = todo,
                        onItemClicked = {
                            onTodoItemClicked(todo)
                        },
                        onTodoDone = {
                            viewModel.completeTodo(todo)
                        },
                        modifier = Modifier.animateItem(),
                    )
                    if (index < todoList!!.lastIndex) {
                        Divider()
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onTodoAddClicked,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomEnd),
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add")
        }

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = refreshing,
            state = pullRefreshState,
        )

        result?.let {
            it.fold(
                onSuccess = {
                    onTodoUpdateSucceed()
                },
                onFailure = { error ->
                    error.printStackTrace()
                    CommonAlertDialog(
                        message = stringResource(id = R.string.error_message),
                        onOkClicked = {
                            viewModel.result.value = null
                        },
                    )
                },
            )
        }
    }
}

@Preview
@Composable
@Suppress("ViewModelConstructorInComposable")
fun TodoListScreenPreview() {
    val viewModel = TodoListViewModel(
        object : GetTodoListUseCase {
            override fun execute(): Flow<List<Todo>> = flowOf(
                listOf(
                    Todo(1, 1001, "英語学習", 2, true),
                ),
            )
        },
        object : FetchTodoListUseCase {
            override suspend fun execute() {}
        },
        object : UpdateTodoUseCase {
            override suspend fun execute(todo: EditingTodo): Result<Unit> = Result.success(Unit)
        },
        object : DeleteTodoUseCase {
            override suspend fun execute(todo: Todo) {}
        },
        object : CompleteTodoUseCase {
            override suspend fun execute(todo: Todo) {}
        },
    )
    TodoListScreen(
        viewModel = viewModel,
        onTodoItemClicked = {},
        onTodoAddClicked = {},
        onTodoUpdateSucceed = {},
    )
}

@Composable
private fun TodoListItem(
    todo: Todo,
    onItemClicked: () -> Unit,
    onTodoDone: (Todo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDone by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onItemClicked,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isDone,
            onCheckedChange = { onTodoDone.invoke(todo) },
            modifier = Modifier.padding(end = 16.dp),
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = todo.name,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ticket),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(36.dp),
                )
                Text(
                    text = "${todo.numberOfTicketsObtained}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun TodoListItemPreview() {
    Surface {
        val todo = Todo(1, 1, "Buy ingredients for dinner", 1, false)
        TodoListItem(
            todo = todo,
            onItemClicked = {},
            onTodoDone = {},
        )
    }
}

@Preview
@Composable
fun DarkModeTodoListItemPreview() {
    MaterialTheme(
        colors = DarkColorScheme,
    ) {
        val todo = Todo(1, 1, "Buy ingredients for dinner", 1, false)
        TodoListItem(
            todo = todo,
            onItemClicked = {},
            onTodoDone = {},
        )
    }
}