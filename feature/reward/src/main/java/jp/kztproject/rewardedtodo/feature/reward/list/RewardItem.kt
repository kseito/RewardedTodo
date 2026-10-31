package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import jp.kztproject.rewardedtodo.presentation.reward.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName

@Composable
internal fun RewardItem(reward: Reward, onRewardItemClick: (Reward) -> Unit) {
    val rarity = RewardRarity.from(reward.probability.value)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .clickable(onClick = { onRewardItemClick(reward) }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.width(5.dp).fillMaxHeight().background(rarity.color()))
            Column(
                modifier = Modifier.weight(1f).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                RarityBadge(rarity)
                Text(
                    text = reward.name.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (reward.needRepeat) {
                        Icon(
                            imageVector = Icons.Filled.Repeat,
                            contentDescription = "Repeat",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = stringResource(if (reward.needRepeat) R.string.reward_repeat else R.string.reward_once),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(
                modifier = Modifier.padding(end = 14.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "${reward.probability.value} %",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.reward_probability),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Composable
fun RewardItemPreview() {
    val reward = Reward(
        RewardId(1),
        RewardName("PS5"),
        Probability(1f),
        RewardDescription("this is very rare"),
        true,
    )
    RewardItem(
        reward = reward,
        onRewardItemClick = {},
    )
}

@Preview
@Composable
fun RewardItemNonRepeatPreview() {
    val reward = Reward(
        RewardId(1),
        RewardName("PS5"),
        Probability(1f),
        RewardDescription("one-time only"),
        false,
    )
    RewardItem(
        reward = reward,
        onRewardItemClick = {},
    )
}
