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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.common.ui.CommonAlertDialog
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.feature.todo.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreenWithBottomSheet(viewModel: TodoListViewModel = hiltViewModel()) {
    val sheetState = rememberModalBottomSheetState()
    var selectedTodo: Todo? by remember { mutableStateOf(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val onTodoItemClicked: (Todo) -> Unit = {
        selectedTodo = it
        showBottomSheet = true
    }
    val onTodoUpdateSucceed: () -> Unit = {
        showBottomSheet = false
    }
    val onTodoAddClicked: () -> Unit = {
        selectedTodo = null
        showBottomSheet = true
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
            onTodoAddClicked = onTodoAddClicked,
            onTodoUpdateSucceed = onTodoUpdateSucceed,
        )

        TodoDetailBottomSheet(
            showBottomSheet = showBottomSheet,
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            todo = selectedTodo,
            onTodoSaveSelected = onTodoSaveSelected,
            onTodoDeleteSelected = onTodoDeleteSelected,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoListScreen(
    viewModel: TodoListViewModel,
    onTodoAddClicked: () -> Unit,
    onTodoItemClicked: (Todo) -> Unit,
    onTodoUpdateSucceed: () -> Unit,
) {
    val todoList by viewModel.todoList.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = { viewModel.refreshTodoList() },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
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
                            viewModel.completeTodo(todo)
                        },
                        modifier = Modifier.animateItem(),
                    )
                    if (index < todoList.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onTodoAddClicked,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomEnd),
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add")
        }

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
                            viewModel.clearResult()
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
        object : GetApiTokenUseCase {
            override suspend fun execute(): ApiToken? = null
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
        val todo = Todo(1, 1, "Buy ingredients for dinner", 1, false)
        TodoListItem(
            todo = todo,
            onItemClicked = {},
            onTodoDone = {},
        )
    }
}
