package jp.kztproject.rewardedtodo.presentation.common

import androidx.annotation.StringRes
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CommonAlertDialog(
    @StringRes messageId: Int,
    onOkClicked: () -> Unit,
) {
    CommonAlertDialog(
        message = stringResource(id = messageId),
        onOkClicked = onOkClicked
    )
}
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
