package jp.kztproject.rewardedtodo.presentation.todo

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ErrorAlertDialog(
    onOkClicked: () -> Unit
) {
    AlertDialog(
        text = {
            // TODO Display a message per exception
            Text(text = "Error occurred.")
        },
        onDismissRequest = { },
        confirmButton = {
            Button(
                onClick = onOkClicked,
                content = {
                    Text("OK")
                }
            )
        },
    )
}

@Preview
@Composable
fun ErrorAlertDialogPreview() {
    ErrorAlertDialog {}
}
