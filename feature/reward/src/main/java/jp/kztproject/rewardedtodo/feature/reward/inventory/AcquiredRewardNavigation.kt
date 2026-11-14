package jp.kztproject.rewardedtodo.feature.reward.inventory

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object AcquiredRewardRoute : NavKey

fun EntryProviderScope<NavKey>.acquiredRewardScreen() {
    entry<AcquiredRewardRoute> {
        AcquiredRewardScreen()
    }
}
