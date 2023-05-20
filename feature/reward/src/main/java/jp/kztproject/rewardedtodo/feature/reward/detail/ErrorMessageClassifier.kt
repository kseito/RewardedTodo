package jp.kztproject.rewardedtodo.feature.reward.detail

import jp.kztproject.rewardedtodo.application.reward.model.error.RewardProbabilityEmptyException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardTitleEmptyException
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.presentation.reward.R

class ErrorMessageClassifier(error: Throwable) {
    val messageId = when (error) {
        is RewardTitleEmptyException -> R.string.error_empty_title
        is RewardProbabilityEmptyException -> R.string.error_empty_probability
        is LackOfTicketsException -> R.string.error_empty_tickets
        else -> R.string.error_unexpected
    }
}
