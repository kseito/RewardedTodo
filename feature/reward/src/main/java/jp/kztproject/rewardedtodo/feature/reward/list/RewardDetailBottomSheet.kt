package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.presentation.reward.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardDetailBottomSheet(
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    reward: Reward?,
    onRewardSaveSelected: (Int?, String, String, String, Boolean) -> Unit,
    onRewardDeleteSelected: (Reward) -> Unit,
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            RewardDetailBottomSheetContent(
                reward = reward,
                onRewardSaveSelected = onRewardSaveSelected,
                onRewardDeleteSelected = onRewardDeleteSelected,
            )
        }
    }
}

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
        } ?: run {
            id = null
            title = ""
            description = ""
            chanceOfWinning = ""
            repeat = false
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp),
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
            singleLine = true,
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text(text = stringResource(id = R.string.hint_description))
            },
            modifier = modifier,
        )
        OutlinedTextField(
            value = chanceOfWinning,
            onValueChange = { chanceOfWinning = it },
            label = {
                Text(text = stringResource(id = R.string.hint_probability))
            },
            modifier = modifier,
        )
        Row {
            Button(
                modifier = Modifier
                    .padding(end = 8.dp),
                onClick = {
                    onRewardSaveSelected(id, title, description, chanceOfWinning, repeat)
                },
            ) {
                Text("Save")
            }
            Button(
                onClick = {
                    reward?.let {
                        onRewardDeleteSelected(it)
                    }
                },
            ) {
                Text("Delete")
            }
            Checkbox(
                checked = repeat,
                onCheckedChange = { repeat = it },
            )
            Text(
                text = "Repeat",
                modifier = Modifier
                    .align(Alignment.CenterVertically),
            )
        }
    }
}

@Preview
@Composable
fun RewardDetailBottomSheetContentPreview() {
    RewardDetailBottomSheetContent(
        reward = null,
        onRewardSaveSelected = { _, _, _, _, _ -> },
        onRewardDeleteSelected = {},
    )
}
