package jp.kztproject.rewardedtodo.presentation.reward

import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardListScreenWithBottomSheet

const val REWARD_SCREEN = "reward_screen"

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.rewardListScreen() {
    composable(route = REWARD_SCREEN) {
        RewardListScreenWithBottomSheet()
    }
}
