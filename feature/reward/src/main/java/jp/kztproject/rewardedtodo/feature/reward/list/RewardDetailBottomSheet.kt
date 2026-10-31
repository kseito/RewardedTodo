package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import jp.kztproject.rewardedtodo.common.ui.theme.RewardedTodoScheme
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
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
internal fun RewardDetailBottomSheetContent(
    reward: Reward?,
    onRewardSaveSelected: (Int?, String, String, String, Boolean) -> Unit,
    onRewardDeleteSelected: (Reward) -> Unit,
) {
    var title by remember(reward) { mutableStateOf(reward?.name?.value.orEmpty()) }
    var description by remember(reward) { mutableStateOf(reward?.description?.value.orEmpty()) }
    var chanceOfWinning by remember(reward) { mutableStateOf(reward?.probability?.value?.toString().orEmpty()) }
    var repeat by remember(reward) { mutableStateOf(reward?.needRepeat ?: false) }
    val probability = chanceOfWinning.toFloatOrNull()?.takeIf { it.isFinite() && it in 0f..100f }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = stringResource(if (reward == null) R.string.reward_add_title else R.string.reward_edit_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.hint_title)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = fieldColors,
            singleLine = true,
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.hint_description)) },
            placeholder = { Text(stringResource(R.string.reward_description_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = fieldColors,
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.reward_probability),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                probability?.let { RarityBadge(RewardRarity.from(it)) }
            }
            OutlinedTextField(
                value = chanceOfWinning,
                onValueChange = { chanceOfWinning = it },
                label = { Text(stringResource(R.string.hint_probability)) },
                suffix = { Text("%") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Slider(
                value = probability ?: 0f,
                onValueChange = { chanceOfWinning = ((it * 100).roundToInt() / 100f).toString() },
                valueRange = 0f..100f,
                modifier = Modifier.semantics { contentDescription = "reward_probability_slider" },
            )
        }
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(value = repeat, role = Role.Switch, onValueChange = { repeat = it })
                    .semantics { contentDescription = "repeat_checkbox" }
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.reward_keep_in_pool),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.reward_repeat),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = repeat, onCheckedChange = null)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    onRewardSaveSelected(reward?.rewardId?.value, title, description, chanceOfWinning, repeat)
                },
            ) {
                Text(stringResource(R.string.reward_save))
            }
            if (reward != null) {
                OutlinedButton(
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    onClick = { onRewardDeleteSelected(reward) },
                ) {
                    Text(stringResource(R.string.reward_delete))
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardDetailBottomSheetContentPreview() {
    RewardDetailPreview(isDark = false, reward = null)
}

@Preview
@Composable
fun RewardDetailEditingPreview() {
    RewardDetailPreview(isDark = false, reward = previewReward)
}

@Preview
@Composable
fun RewardDetailEditingDarkPreview() {
    RewardDetailPreview(isDark = true, reward = previewReward)
}

private val previewReward = Reward(
    RewardId(1),
    RewardName("マンガを買う"),
    Probability(1.8f),
    RewardDescription(""),
    true,
)

@Composable
private fun RewardDetailPreview(isDark: Boolean, reward: Reward?) {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDark)) {
        Surface {
            RewardDetailBottomSheetContent(
                reward = reward,
                onRewardSaveSelected = { _, _, _, _, _ -> },
                onRewardDeleteSelected = {},
            )
        }
    }
}
