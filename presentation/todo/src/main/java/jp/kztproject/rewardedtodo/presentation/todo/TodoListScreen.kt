package jp.kztproject.rewardedtodo.presentation.todo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import jp.kztproject.rewardedtodo.presentation.common.CommonAlertDialog
import jp.kztproject.rewardedtodo.presentation.reward.list.DarkColorScheme
import jp.kztproject.rewardedtodo.todo.application.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.todo.application.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.todo.application.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.todo.application.GetTodoListUseCase
import jp.kztproject.rewardedtodo.todo.application.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.todo.domain.EditingTodo
import jp.kztproject.rewardedtodo.todo.domain.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun TodoListScreenWithBottomSheet(
    viewModel: TodoListViewModel = hiltViewModel(),
) {
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
        onTodoDeleteSelected = onTodoDeleteSelected
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
    val error by viewModel.error.observeAsState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colors.background)
    ) {
        Column {
            todoList?.forEachIndexed { index, todo ->
                TodoListItem(
                    todo = todo,
                    onItemClicked = {
                        onTodoItemClicked(todo)
                    },
                    onTodoDone = {
                        viewModel.completeTodo(it)
                    })
                if (index < todoList!!.lastIndex) {
                    Divider()
                }
            }
        }

        val (createTodoButton) = createRefs()

        FloatingActionButton(
            onClick = onTodoAddClicked,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier
                .constrainAs(createTodoButton) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .padding(24.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add")
        }

        error?.let {
            it.fold(
                onSuccess = {
                    onTodoUpdateSucceed()
                },
                onFailure = { error ->
                    error.printStackTrace()
                    CommonAlertDialog(
                        message = stringResource(id = R.string.error_message),
                        onOkClicked = {
                            viewModel.error.value = null
                        }
                    )
                }
            )
        }
    }
}

@Preview
@ExperimentalMaterialApi
@Composable
fun TodoListScreenPreview() {
    val viewModel = TodoListViewModel(
        object : GetTodoListUseCase {
            override fun execute(): Flow<List<Todo>> {
                // TODO cannot display
                return flowOf(
                    listOf(
                        Todo(1, 1001, "英語学習", 2, true),
                    )
                )
            }
        },
        object : FetchTodoListUseCase {
            override suspend fun execute() {}
        },
        object : UpdateTodoUseCase {
            override suspend fun execute(todo: EditingTodo): Result<Unit> {
                return Result.success(Unit)
            }
        },
        object : DeleteTodoUseCase {
            override suspend fun execute(todo: Todo) {}
        },
        object : CompleteTodoUseCase {
            override suspend fun execute(todo: Todo) {}
        }
    )
    TodoListScreen(
        viewModel = viewModel,
        onTodoItemClicked = {},
        onTodoAddClicked = {},
        onTodoUpdateSucceed = {}
    )
}

@Composable
private fun TodoListItem(todo: Todo, onItemClicked: () -> Unit, onTodoDone: (Todo) -> Unit) {
    val isDone by remember { mutableStateOf(false) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClicked)
            .padding(16.dp)
    ) {
        val (checkbox, title, ticketImage, ticketCount) = createRefs()

        Checkbox(
            checked = isDone,
            onCheckedChange = { onTodoDone.invoke(todo) },
            modifier = Modifier
                .constrainAs(checkbox) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .padding(0.dp, 0.dp, 16.dp, 0.dp)
        )
        Text(
            text = todo.name,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(checkbox.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_ticket),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(36.dp)
                .constrainAs(ticketImage) {
                    top.linkTo(title.bottom)
                    start.linkTo(title.start)
                }
        )
        Text(
            text = "${todo.numberOfTicketsObtained}",
            fontSize = 20.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .constrainAs(ticketCount) {
                    top.linkTo(ticketImage.top)
                    start.linkTo(ticketImage.end, 16.dp)
                    bottom.linkTo(ticketImage.bottom)
                }
        )
    }
}

@Preview
@Composable
private fun TodoListItemPreview() {
    Surface {
        val todo = Todo(1, 1, "Buy ingredients for dinner", 1, false)
        TodoListItem(todo, {}) {}
    }
}

@Preview
@Composable
private fun DarkModeTodoListItemPreview() {
    MaterialTheme(
        colors = DarkColorScheme
    ) {
        val todo = Todo(1, 1, "Buy ingredients for dinner", 1, false)
        TodoListItem(todo, {}) {}
    }
}
