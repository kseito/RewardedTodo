package jp.kztproject.rewardedtodo

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class TopLevelDestination(
    @DrawableRes val iconImageId: Int,
    @StringRes val iconTextId: Int
) {
    TODO(
        iconImageId = R.drawable.reward_done,
        iconTextId = R.string.menu_todo
    ),
    REWARD(
        iconImageId = R.drawable.lottery_button,
        iconTextId = R.string.menu_reward
    )
}
