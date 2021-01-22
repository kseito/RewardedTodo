package jp.kztproject.rewardedtodo.presentation.reward.detail

import jp.kztproject.rewardedtodo.application.reward.model.Error
import jp.kztproject.rewardedtodo.presentation.reward.R

class ErrorMessageClassifier(error: Error) {
    val messageId = when(error) {
        Error.EmptyTitle -> R.string.error_empty_title
        Error.EmptyPoint -> R.string.error_empty_point
        Error.EmptyProbability -> R.string.error_empty_probability
    }
}