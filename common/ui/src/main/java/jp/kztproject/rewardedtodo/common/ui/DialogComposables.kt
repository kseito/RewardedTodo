package jp.kztproject.rewardedtodo.common.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CommonAlertDialog(
    message: String,
    onOkClicked: () -> Unit,
) {
    AlertDialog(
        text = {
            // TODO Display a message per exception
            Text(text = message)
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
    CommonAlertDialog(
        message = "Error occurred",
        onOkClicked = {}
    )
}
