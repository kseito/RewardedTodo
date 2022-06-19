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

@ExperimentalMaterialApi
@Composable
fun TodoDetailBottomSheet(
    bottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        content = content,
        sheetContent = {
            TodoDetailBottomSheetContent()
        },
    )
}

@Preview
@Composable
private fun TodoDetailBottomSheetContent() {
    var title: String by remember { mutableStateOf("") }
    var numberOfTicket: String by remember { mutableStateOf("") }

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
            range = 1..100,
            modifier = Modifier
                .constrainAs(ticketInput) {
                    top.linkTo(titleTextField.bottom)
                    end.linkTo(parent.end)
                }
        )
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(saveButton) {
                    top.linkTo(ticketLabelText.bottom)
                }
        ) {
            Text(text = "Save")
        }
        Button(
            onClick = { /*TODO*/ },
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
