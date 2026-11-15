package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.presentation.reward.R

@Composable
fun BatchLotteryResultDialog(result: BatchLotteryResult, onDismiss: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.batch_lottery_title, (result.wonRewards.size + result.missCount)))
        },
        text = {
            Column {
                if (result.hasWon) {
                    Text(
                        text = stringResource(id = R.string.batch_lottery_won, result.wonRewards.size),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    result.wonRewards
                        .groupBy { it.name }
                        .forEach { (name, rewards) ->
                            Text(
                                text = "・${name.value} x${rewards.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                }
                Text(
                    text = stringResource(id = R.string.batch_lottery_missed, result.missCount),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        },
        onDismissRequest = { },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        },
    )
}

@Preview
@Composable
fun BatchLotteryResultDialogPreview() {
    val reward1 = AcquiredReward(
        AcquiredReward.Id("1"),
        RewardId(1),
        RewardName("PS5"),
        RewardDescription(""),
        "2026-11-07T10:00:00Z",
        null,
    )
    val reward2 = AcquiredReward(
        AcquiredReward.Id("2"),
        RewardId(2),
        RewardName("Switch"),
        RewardDescription(""),
        "2026-11-07T10:00:00Z",
        null,
    )
    val result = BatchLotteryResult(
        wonRewards = listOf(reward1, reward2, reward2),
        missCount = 7,
    )
    BatchLotteryResultDialog(
        result = result,
        onDismiss = {},
    )
}
