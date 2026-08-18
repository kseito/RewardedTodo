package jp.kztproject.rewardedtodo.feature.reward

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListScreenWithBottomSheet
import kotlinx.serialization.Serializable

const val REWARD_SCREEN = "reward_screen"

@Serializable
data object RewardListRoute : NavKey

fun NavGraphBuilder.rewardListScreen() {
    composable(route = REWARD_SCREEN) {
        RewardListScreenWithBottomSheet()
    }
}

fun EntryProviderScope<NavKey>.rewardListScreen() {
    entry<RewardListRoute> {
        RewardListScreenWithBottomSheet()
    }
}
