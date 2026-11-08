package jp.kztproject.rewardedtodo.feature.reward.detail

import jp.kztproject.rewardedtodo.application.reward.model.error.OverMaxRewardsException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardProbabilityEmptyException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardTitleEmptyException
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.presentation.reward.R
import jp.kztproject.rewardedtodo.domain.reward.exception.RewardServerException
import jp.kztproject.rewardedtodo.domain.reward.exception.PendingLotteryException

class ErrorMessageClassifier(error: Throwable) {
    val messageId = when (error) {
        is RewardTitleEmptyException -> R.string.error_empty_title

        is RewardProbabilityEmptyException -> R.string.error_empty_probability

        is OverMaxRewardsException -> R.string.error_over_max_rewards

        is LackOfTicketsException -> R.string.error_empty_tickets

        is PendingLotteryException -> R.string.reward_pending

        is IllegalArgumentException, is ArithmeticException -> R.string.reward_error_probability

        is RewardServerException -> when (error.code) {
            "VERSION_CONFLICT" -> R.string.reward_error_conflict
            "PROBABILITY_TOTAL_EXCEEDED", "INVALID_REWARD" -> R.string.reward_error_probability
            "REWARD_LIMIT" -> R.string.error_over_max_rewards
            "NO_ACTIVE_REWARDS" -> R.string.reward_error_no_active
            else -> R.string.error_unexpected
        }

        else -> R.string.error_unexpected
    }
}
