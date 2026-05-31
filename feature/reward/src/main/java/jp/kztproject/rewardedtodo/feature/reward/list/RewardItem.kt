package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName

@Composable
internal fun RewardItem(reward: Reward, onRewardItemClick: (Reward) -> Unit) {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = { onRewardItemClick(reward) },
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(8f),
            ) {
                Text(
                    text = reward.name.value,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = reward.description.value ?: "",
                    color = Color.Gray,
                )
                if (reward.needRepeat) {
                    Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = "Repeat",
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = "${reward.probability.value} %",
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.End,
            )
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
