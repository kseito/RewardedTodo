package kztproject.jp.splacounter.reward.application

import androidx.annotation.StringRes

class CommonException(@StringRes val messageId: Int) : Exception()