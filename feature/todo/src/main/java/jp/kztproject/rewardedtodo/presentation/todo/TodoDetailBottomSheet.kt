package jp.kztproject.rewardedtodo.presentation.todo

import NumberPicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.feature.todo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailBottomSheet(
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    todo: Todo?,
    onTodoSaveSelected: (EditingTodo) -> Unit,
    onTodoDeleteSelected: (EditingTodo) -> Unit,
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            TodoDetailBottomSheetContent(
                todo = todo,
                onTodoSaveSelected = onTodoSaveSelected,
                onTodoDeleteSelected = onTodoDeleteSelected,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

@Composable
private fun TodoDetailBottomSheetContent(
    todo: Todo?,
    onTodoSaveSelected: (EditingTodo) -> Unit, // TODO create new Domain
    onTodoDeleteSelected: (EditingTodo) -> Unit,
    onDismissRequest: () -> Unit,
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
            .padding(8.dp),
    ) {
        val (
            titleTextField,
            ticketLabelText,
            ticketLabelImage,
            ticketInput,
            saveButton,
            deleteButton,
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
                },
        )
        Text(
            text = stringResource(id = R.string.todo_reward),
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(ticketLabelText) {
                    top.linkTo(titleTextField.bottom)
                },
        )
        Image(
            painter = painterResource(id = R.drawable.ic_ticket),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .constrainAs(ticketLabelImage) {
                    top.linkTo(titleTextField.bottom)
                    start.linkTo(ticketLabelText.end)
                },
        )
        NumberPicker(
            state = numberOfTicket,
            onStateChanged = { numberOfTicket.value = it },
            range = 0..100,
            modifier = Modifier
                .constrainAs(ticketInput) {
                    top.linkTo(titleTextField.bottom)
                    end.linkTo(parent.end)
                },
        )
        Button(
            onClick = {
                todo?.let {
                    val editingTodo = EditingTodo.from(it)
                        .copy(
                            id = id,
                            name = title,
                            numberOfTicketsObtained = numberOfTicket.value,
                        )
                    onTodoSaveSelected(editingTodo)
                } ?: run {
                    val editingTodo = EditingTodo(
                        id = id,
                        name = title,
                        numberOfTicketsObtained = numberOfTicket.value,
                    )
                    onTodoSaveSelected(editingTodo)
                }
                onDismissRequest()
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(saveButton) {
                    top.linkTo(ticketLabelText.bottom)
                },
        ) {
            Text(text = "Save")
        }
        todo?.let {
            Button(
                onClick = {
                    val editingTodo = EditingTodo.from(todo)
                    onTodoDeleteSelected(editingTodo)
                    onDismissRequest()
                },
                modifier = Modifier
                    .constrainAs(deleteButton) {
                        top.linkTo(ticketLabelText.bottom)
                        start.linkTo(saveButton.end)
                    },
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
        onTodoDeleteSelected = {},
        onDismissRequest = {},
    )
}
