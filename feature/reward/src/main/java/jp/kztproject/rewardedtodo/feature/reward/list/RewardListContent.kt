package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kztproject.rewardedtodo.common.ui.theme.RewardedTodoScheme
import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.presentation.reward.R

@Composable
internal fun RewardListContent(
    rewards: List<Reward>,
    ticket: Int,
    isSingleLottering: Boolean,
    isBatchLottering: Boolean,
    onSingleLottery: () -> Unit,
    onBatchLottery: () -> Unit,
    onRewardItemClick: (Reward) -> Unit,
    onAddNewRewardClick: () -> Unit,
) {
    val isLottering = isSingleLottering || isBatchLottering
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .semantics { testTagsAsResourceId = true },
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.reward_owned_tickets),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                    ) {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(34.dp),
                        )
                        Text(
                            text = pluralStringResource(R.plurals.reward_ticket_balance, ticket, ticket),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.testTag("ticket_count"),
                        )
                    }
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Button(
                            onClick = onSingleLottery,
                            enabled = !isLottering,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.weight(1f).fillMaxHeight().semantics {
                                contentDescription =
                                    "single_lottery_button"
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        ) {
                            LotteryButtonLabel(isSingleLottering, R.string.reward_draw, 1)
                        }
                        OutlinedButton(
                            onClick = onBatchLottery,
                            enabled = !isLottering,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary,
                            ),
                            modifier = Modifier.weight(1f).fillMaxHeight().semantics {
                                contentDescription =
                                    "batch_lottery_button"
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        ) {
                            LotteryButtonLabel(
                                isBatchLottering,
                                R.string.reward_draw_batch,
                                BatchLotteryResult.DEFAULT_COUNT,
                            )
                        }
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.reward_lineup),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onAddNewRewardClick, enabled = !isLottering) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(stringResource(R.string.reward_add))
                }
            }
        }
        if (rewards.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.reward_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
        items(rewards, key = { it.rewardId.value }) { reward ->
            RewardItem(reward, onRewardItemClick)
        }
    }
}

@Composable
private fun LotteryButtonLabel(isLoading: Boolean, title: Int, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = LocalContentColor.current,
                strokeWidth = 2.dp,
            )
            Text(stringResource(R.string.reward_drawing), style = MaterialTheme.typography.labelSmall)
        } else {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(
                pluralStringResource(R.plurals.reward_ticket_cost, count, count),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview
@Composable
fun RewardListPreview() {
    RewardListThemedPreview(false)
}

@Preview
@Composable
fun RewardListDarkPreview() {
    RewardListThemedPreview(true)
}

@Composable
private fun RewardListThemedPreview(isDark: Boolean) {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDark)) {
        RewardListContent(
            rewards = listOf(0.2f, 0.5f, 0.8f, 1.8f).mapIndexed { index, probability ->
                Reward(
                    RewardId(index),
                    RewardName(listOf("好きなゲーム", "マッサージ", "コーヒー豆", "マンガ")[index]),
                    Probability(probability),
                    RewardDescription(""),
                    index != 3,
                )
            },
            ticket = 256,
            isSingleLottering = false,
            isBatchLottering = false,
            onSingleLottery = {},
            onBatchLottery = {},
            onRewardItemClick = {},
            onAddNewRewardClick = {},
        )
    }
}
