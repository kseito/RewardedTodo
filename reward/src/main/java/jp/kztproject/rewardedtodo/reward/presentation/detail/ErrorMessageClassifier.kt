package jp.kztproject.rewardedtodo.reward.presentation.detail

import jp.kztproject.rewardedtodo.reward.application.model.Error
import project.seito.reward.R

class ErrorMessageClassifier(error: Error) {
    val messageId = when(error) {
        Error.EmptyTitle -> R.string.error_empty_title
        Error.EmptyPoint -> R.string.error_empty_point
        Error.EmptyProbability -> R.string.error_empty_probability
    }
}