package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun RewardDetailBottomSheet(
    bottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            RewardDetailBottomSheetContent()
        },
        content = content
    )
}

@Preview
@ExperimentalMaterialApi
@Composable
private fun RewardDetailBottomSheetContent() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var chanceOfWinning by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        TextField(
            value = title,
            onValueChange = { title = it },
            modifier = modifier
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = modifier
        )
        TextField(
            value = chanceOfWinning,
            onValueChange = { chanceOfWinning = it },
            modifier = modifier
        )
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = repeat,
                onCheckedChange = { repeat = it }
            )
            Text(
                text = "Repeat",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
        Row {
            Button(
                modifier = Modifier
                    .padding(end = 8.dp),
                onClick = { /*TODO*/ }
            ) {
                Text("Save")
            }
            Button(
                onClick = { /*TODO*/ }
            ) {
                Text("Delete")
            }
        }
    }
}