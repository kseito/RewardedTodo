package jp.kztproject.rewardedtodo.feature.reward

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListScreenWithBottomSheet
import kotlinx.serialization.Serializable

@Serializable
data object RewardListRoute : NavKey

fun EntryProviderScope<NavKey>.rewardListScreen() {
    entry<RewardListRoute> {
        RewardListScreenWithBottomSheet()
    }
}
