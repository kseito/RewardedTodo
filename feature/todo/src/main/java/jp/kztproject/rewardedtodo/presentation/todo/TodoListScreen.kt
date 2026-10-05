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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import jp.kztproject.rewardedtodo.domain.todo.exception.TodoistUnauthorizedException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.kztproject.rewardedtodo.common.ui.CommonAlertDialog
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.feature.todo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreenWithBottomSheet(onOpenSetting: () -> Unit, viewModel: TodoListViewModel = hiltViewModel()) {
    val sheetState = rememberModalBottomSheetState()
    var selectedTodo: Todo? by remember { mutableStateOf(null) }

    val onTodoItemClicked: (Todo) -> Unit = {
        selectedTodo = it
    }
    val onTodoUpdateSucceed: () -> Unit = {
        selectedTodo = null
    }
    val onTodoSaveSelected: (EditingTodo) -> Unit = {
        viewModel.updateTodo(it)
    }
    val onTodoDeleteSelected: (EditingTodo) -> Unit = {
        viewModel.deleteTodo(it)
    }

    Box {
        TodoListScreen(
            viewModel = viewModel,
            onTodoItemClicked = onTodoItemClicked,
            onTodoUpdateSucceed = onTodoUpdateSucceed,
            onOpenSetting = onOpenSetting,
        )

        selectedTodo?.let { todo ->
            TodoDetailBottomSheet(
                onDismissRequest = { selectedTodo = null },
                sheetState = sheetState,
                todo = todo,
                onTodoSaveSelected = onTodoSaveSelected,
                onTodoDeleteSelected = onTodoDeleteSelected,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoListScreen(
    viewModel: TodoListViewModel,
    onTodoItemClicked: (Todo) -> Unit,
    onTodoUpdateSucceed: () -> Unit,
    onOpenSetting: () -> Unit,
) {
    val todoList by viewModel.todoList.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isInitialLoading by viewModel.isInitialLoading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        TodoListContent(
            todoList = todoList,
            isInitialLoading = isInitialLoading,
            isRefreshing = refreshing,
            onRefresh = { viewModel.refreshTodoList() },
            onTodoItemClicked = onTodoItemClicked,
            onTodoDone = { viewModel.completeTodo(it) },
        )

        result?.let { outcome ->
            outcome.fold(
                onSuccess = {
                    onTodoUpdateSucceed()
                    viewModel.clearResult()
                },
                onFailure = { cause ->
                    // 失効は設定画面へ誘導する必要があるため、他のエラーと扱いを分ける
                    if (cause !is TodoistUnauthorizedException) {
                        CommonAlertDialog(
                            message = stringResource(id = R.string.error_message),
                            onOkClicked = {
                                viewModel.clearResult()
                            },
                        )
                    }
                },
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    val unauthorizedMessage = stringResource(id = R.string.error_todoist_unauthorized)
    val openSettingLabel = stringResource(id = R.string.open_setting)
    LaunchedEffect(result) {
        val cause = result?.exceptionOrNull()
        if (cause is TodoistUnauthorizedException) {
            val action = snackbarHostState.showSnackbar(
                message = unauthorizedMessage,
                actionLabel = openSettingLabel,
                withDismissAction = true,
            )
            viewModel.clearResult()
            if (action == SnackbarResult.ActionPerformed) onOpenSetting()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoListContent(
    todoList: List<Todo>,
    isInitialLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onTodoItemClicked: (Todo) -> Unit,
    onTodoDone: (Todo) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when {
            isInitialLoading && todoList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.semantics {
                            contentDescription = "initial_loading_indicator"
                        },
                    )
                }
            }

            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        if (todoList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.todo_empty_message),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                        }
                        itemsIndexed(
                            items = todoList,
                            key = { _, todo -> todo.id },
                        ) { index, todo ->
                            TodoListItem(
                                todo = todo,
                                onItemClicked = {
                                    onTodoItemClicked(todo)
                                },
                                onTodoDone = {
                                    onTodoDone(todo)
                                },
                                modifier = Modifier.animateItem(),
                            )
                            if (index < todoList.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TodoListContentLoadingPreview() {
    TodoListContent(
        todoList = emptyList(),
        isInitialLoading = true,
        isRefreshing = false,
        onRefresh = {},
        onTodoItemClicked = {},
        onTodoDone = {},
    )
}

@Preview
@Composable
fun TodoListContentEmptyPreview() {
    TodoListContent(
        todoList = emptyList(),
        isInitialLoading = false,
        isRefreshing = false,
        onRefresh = {},
        onTodoItemClicked = {},
        onTodoDone = {},
    )
}

@Preview
@Composable
fun TodoListContentWithDataPreview() {
    TodoListContent(
        todoList = listOf(
            Todo(1, "1001", "英語学習", 2, true),
            Todo(2, "1002", "ランニング", 1, false),
        ),
        isInitialLoading = false,
        isRefreshing = false,
        onRefresh = {},
        onTodoItemClicked = {},
        onTodoDone = {},
    )
}

@Preview
@Composable
fun TodoListContentErrorPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        TodoListContent(
            todoList = listOf(
                Todo(1, "1001", "英語学習", 2, true),
            ),
            isInitialLoading = false,
            isRefreshing = false,
            onRefresh = {},
            onTodoItemClicked = {},
            onTodoDone = {},
        )
        CommonAlertDialog(
            message = stringResource(id = R.string.error_message),
            onOkClicked = {},
        )
    }
}

@Composable
private fun TodoListItem(
    todo: Todo,
    onItemClicked: () -> Unit,
    onTodoDone: (Todo) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDone by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onItemClicked,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isDone,
            onCheckedChange = {
                onTodoDone.invoke(todo)
                isDone = it
            },
            modifier = Modifier
                .padding(end = 16.dp)
                .semantics { contentDescription = "todo_checkbox" },
        )
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = todo.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                    color = MaterialTheme.colorScheme.onBackground,
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
        val todo = Todo(1, "1", "Buy ingredients for dinner", 1, false)
        TodoListItem(
            todo = todo,
            onItemClicked = {},
            onTodoDone = {},
        )
    }
}
