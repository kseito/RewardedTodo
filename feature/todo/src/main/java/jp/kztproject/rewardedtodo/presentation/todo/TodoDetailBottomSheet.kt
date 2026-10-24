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
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    todo: Todo,
    onTodoSaveSelected: (EditingTodo) -> Unit,
    isSaving: Boolean = false,
) {
    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismissRequest() },
        sheetState = sheetState,
    ) {
        TodoDetailBottomSheetContent(
            todo = todo,
            onTodoSaveSelected = onTodoSaveSelected,
            isSaving = isSaving,
        )
    }
}

@Composable
private fun TodoDetailBottomSheetContent(
    todo: Todo,
    onTodoSaveSelected: (EditingTodo) -> Unit, // TODO create new Domain
    isSaving: Boolean = false,
) {
    var title: String by remember { mutableStateOf(todo.name) }
    val numberOfTicket = remember { mutableStateOf(todo.numberOfTicketsObtained) }

    LaunchedEffect(todo) {
        title = todo.name
        numberOfTicket.value = todo.numberOfTicketsObtained
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
        ) = createRefs()

        OutlinedTextField(
            value = title,
            onValueChange = {},
            readOnly = true,
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
            onStateChanged = { if (!isSaving) numberOfTicket.value = it },
            range = 1..100,
            enabled = !isSaving,
            modifier = Modifier
                .constrainAs(ticketInput) {
                    top.linkTo(titleTextField.bottom)
                    end.linkTo(parent.end)
                },
        )
        Button(
            enabled = !isSaving,
            onClick = {
                val editingTodo = EditingTodo.from(todo)
                    .copy(
                        name = title,
                        numberOfTicketsObtained = numberOfTicket.value,
                    )
                onTodoSaveSelected(editingTodo)
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(saveButton) {
                    top.linkTo(ticketLabelText.bottom)
                },
        ) {
            Text(text = if (isSaving) "Saving…" else "Save")
        }
    }
}

@Preview
@Composable
fun TodoDetailBottomSheetContentEditingPreview() {
    TodoDetailBottomSheetContent(
        todo = Todo(
            todoistId = "abc123",
            name = "英語学習",
            numberOfTicketsObtained = 3,
            isRepeat = false,
        ),
        onTodoSaveSelected = {},
    )
}

@Preview
@Composable
fun TodoDetailBottomSheetContentRepeatPreview() {
    TodoDetailBottomSheetContent(
        todo = Todo(
            todoistId = "repeat-task",
            name = "ランニング",
            numberOfTicketsObtained = 1,
            isRepeat = true,
        ),
        onTodoSaveSelected = {},
    )
}
