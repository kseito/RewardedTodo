package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.presentation.reward.R

@ExperimentalMaterialApi
@Composable
fun RewardDetailBottomSheet(
    bottomSheetState: ModalBottomSheetState,
    reward: Reward?,
    onRewardSaveSelected: (Int?, String, String, String, Boolean) -> Unit,
    onRewardDeleteSelected: (Reward) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            RewardDetailBottomSheetContent(
                reward = reward,
                onRewardSaveSelected = onRewardSaveSelected,
                onRewardDeleteSelected = onRewardDeleteSelected,
            )
        },
        content = content
    )
}

@ExperimentalMaterialApi
@Composable
private fun RewardDetailBottomSheetContent(
    reward: Reward?,
    onRewardSaveSelected: (Int?, String, String, String, Boolean) -> Unit,
    onRewardDeleteSelected: (Reward) -> Unit,
) {
    var id: Int? by remember { mutableStateOf(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var chanceOfWinning by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf(false) }

    LaunchedEffect(reward) {
        reward?.let {
            id = it.rewardId.value
            title = it.name.value
            description = it.description.value ?: ""
            chanceOfWinning = it.probability.value.toString()
            repeat = it.needRepeat
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = {
                Text(text = stringResource(id = R.string.hint_title))
            },
            modifier = modifier,
            singleLine = true
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text(text = stringResource(id = R.string.hint_description))
            },
            modifier = modifier
        )
        OutlinedTextField(
            value = chanceOfWinning,
            onValueChange = { chanceOfWinning = it },
            label = {
                Text(text = stringResource(id = R.string.hint_point))
            },
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
                onClick = {
                    onRewardSaveSelected(id, title, description, chanceOfWinning, repeat)
                }
            ) {
                Text("Save")
            }
            Button(
                onClick = {
                    reward?.let {
                        onRewardDeleteSelected(it)
                    }
                }
            ) {
                Text("Delete")
            }
        }
    }
}

@Preview
@ExperimentalMaterialApi
@Composable
fun RewardDetailBottomSheetContentPreview() {
    RewardDetailBottomSheetContent(
        reward = null,
        onRewardSaveSelected = { _, _, _, _, _ -> },
        onRewardDeleteSelected = {}
    )
}
