package jp.kztproject.rewardedtodo.feature.reward.list

import io.kotest.matchers.shouldBe
import org.junit.Test

class RewardRarityTest {
    @Test
    fun `rarity follows the design thresholds in percent`() {
        RewardRarity.from(0f) shouldBe RewardRarity.LEGEND
        RewardRarity.from(0.2f) shouldBe RewardRarity.LEGEND
        RewardRarity.from(0.21f) shouldBe RewardRarity.EPIC
        RewardRarity.from(0.5f) shouldBe RewardRarity.EPIC
        RewardRarity.from(0.51f) shouldBe RewardRarity.RARE
        RewardRarity.from(1f) shouldBe RewardRarity.RARE
        RewardRarity.from(1.01f) shouldBe RewardRarity.NORMAL
        RewardRarity.from(100f) shouldBe RewardRarity.NORMAL
    }
}
