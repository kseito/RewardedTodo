package jp.kztproject.rewardedtodo

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import jp.kztproject.rewardedtodo.presentation.reward.R as RewardResources

enum class TopLevelDestination(
    @param:DrawableRes val iconImageId: Int,
    @param:StringRes val iconTextId: Int,
    @param:StringRes val titleId: Int = iconTextId,
) {
    TODO(
        iconImageId = R.drawable.reward_done,
        iconTextId = R.string.menu_todo,
    ),
    REWARD(
        iconImageId = R.drawable.lottery_button,
        iconTextId = R.string.menu_reward,
    ),
    ACQUIRED(
        iconImageId = R.drawable.gift_box,
        iconTextId = RewardResources.string.menu_acquired,
        titleId = RewardResources.string.title_acquired_rewards,
    ),
}
