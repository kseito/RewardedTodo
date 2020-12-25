package jp.kztproject.rewardedtodo.reward.application

import androidx.annotation.StringRes

class CommonException(@StringRes val messageId: Int) : Exception()