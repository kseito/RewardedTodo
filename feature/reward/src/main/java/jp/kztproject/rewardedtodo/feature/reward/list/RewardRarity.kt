package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

internal enum class RewardRarity(val darkColor: Color, val lightColor: Color) {
    LEGEND(Color(0xFFFFC23D), Color(0xFF805600)),
    EPIC(Color(0xFFC46BFF), Color(0xFF8A3CD8)),
    RARE(Color(0xFF4AA8FF), Color(0xFF246BB8)),
    NORMAL(Color(0xFF5CCB86), Color(0xFF187A4B)),
    ;

    companion object {
        fun from(probability: Float): RewardRarity = when {
            probability <= 0.2f -> LEGEND
            probability <= 0.5f -> EPIC
            probability <= 1f -> RARE
            else -> NORMAL
        }
    }
}

@Composable
internal fun RewardRarity.color(): Color =
    if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) darkColor else lightColor

@Composable
internal fun RarityBadge(rarity: RewardRarity) {
    val color = rarity.color()
    Surface(shape = RoundedCornerShape(7.dp), color = color.copy(alpha = 0.16f)) {
        Text(
            text = rarity.name,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}
