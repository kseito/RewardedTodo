package jp.kztproject.rewardedtodo.presentation.todo

import NumberPicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import jp.kztproject.rewardedtodo.todo.domain.EditingTodo
import jp.kztproject.rewardedtodo.todo.domain.Todo

@ExperimentalMaterialApi
@Composable
fun TodoDetailBottomSheet(
    todo: Todo?,
    bottomSheetState: ModalBottomSheetState,
    onTodoSaveSelected: (EditingTodo) -> Unit,
    onTodoDeleteSelected: (EditingTodo) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        content = content,
        sheetContent = {
            TodoDetailBottomSheetContent(
                todo = todo,
                onTodoSaveSelected = onTodoSaveSelected,
                onTodoDeleteSelected = onTodoDeleteSelected
            )
        },
    )
}

@Composable
private fun TodoDetailBottomSheetContent(
    todo: Todo?,
    onTodoSaveSelected: (EditingTodo) -> Unit, //TODO create new Domain
    onTodoDeleteSelected: (EditingTodo) -> Unit
) {
    var id: Long? by remember { mutableStateOf(null) }
    var title: String by remember { mutableStateOf("") }
    var numberOfTicket = remember { mutableStateOf(0) }

    LaunchedEffect(todo) {
        if (todo != null) {
            id = todo.id
            title = todo.name
            numberOfTicket.value = todo.numberOfTicketsObtained.toInt()
        } else {
            id = null
            title = ""
            numberOfTicket.value = 1
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val (
            titleTextField,
            ticketLabelText,
            ticketLabelImage,
            ticketInput,
            saveButton,
            deleteButton
        ) = createRefs()

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = {
                Text(text = "Title")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .constrainAs(titleTextField) {
                    top.linkTo(parent.top)
                }
        )
        Text(
            text = stringResource(id = R.string.todo_reward),
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(ticketLabelText) {
                    top.linkTo(titleTextField.bottom)
                }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_ticket),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .constrainAs(ticketLabelImage) {
                    top.linkTo(titleTextField.bottom)
                    start.linkTo(ticketLabelText.end)
                }
        )
        // TODO Sometimes weird actions occurred.
        NumberPicker(
            state = numberOfTicket,
            onStateChanged = { numberOfTicket.value = it },
            range = 1..100,
            modifier = Modifier
                .constrainAs(ticketInput) {
                    top.linkTo(titleTextField.bottom)
                    end.linkTo(parent.end)
                }
        )
        Button(
            onClick = {
                todo?.let {
                    val editingTodo = EditingTodo.from(it)
                        .copy(
                            id = id,
                            name = title,
                            numberOfTicketsObtained = numberOfTicket.value.toFloat()
                        )
                    onTodoSaveSelected(editingTodo)
                }
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(saveButton) {
                    top.linkTo(ticketLabelText.bottom)
                }
        ) {
            Text(text = "Save")
        }
        todo?.let {
            Button(
                onClick = {
                    val editingTodo = EditingTodo.from(todo)
                    onTodoDeleteSelected(editingTodo)
                },
                modifier = Modifier
                    .constrainAs(deleteButton) {
                        top.linkTo(ticketLabelText.bottom)
                        start.linkTo(saveButton.end)
                    }
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@Preview
@Composable
fun TodoDetailBottomSheetContentPreview() {
    TodoDetailBottomSheetContent(
        todo = null,
        onTodoSaveSelected = {},
        onTodoDeleteSelected = {}
    )
}
