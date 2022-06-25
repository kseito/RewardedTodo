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
import jp.kztproject.rewardedtodo.presentation.todo.model.EditingTodo
import jp.kztproject.rewardedtodo.todo.domain.Todo

@ExperimentalMaterialApi
@Composable
fun TodoDetailBottomSheet(
    bottomSheetState: ModalBottomSheetState,
    onTodoSaveSelected: (EditingTodo) -> Unit,
    onTodoDeleteSelected: (Todo) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        content = content,
        sheetContent = {
            TodoDetailBottomSheetContent(
                todo = null,
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
    onTodoDeleteSelected: (Todo) -> Unit
) {
    var title: String by remember { mutableStateOf("") }
    var numberOfTicket: Float by remember { mutableStateOf(0f) }

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

        TextField(
            value = title,
            onValueChange = { title = it },
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
        NumberPicker(
            state = remember { mutableStateOf(1) },
            onStateChanged = { numberOfTicket = it.toFloat() },
            range = 1..100,
            modifier = Modifier
                .constrainAs(ticketInput) {
                    top.linkTo(titleTextField.bottom)
                    end.linkTo(parent.end)
                }
        )
        Button(
            onClick = {
                val editingTodo = EditingTodo(
                    name = title,
                    numberOfTicketsObtained = numberOfTicket
                )
                onTodoSaveSelected(editingTodo)
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
                onClick = { onTodoDeleteSelected(todo) },
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
