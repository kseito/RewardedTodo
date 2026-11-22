package jp.kztproject.rewardedtodo.application.reward.usecase

import kotlinx.coroutines.flow.Flow

interface ObserveLotteryAnimationEnabledUseCase {
    fun execute(): Flow<Boolean>
}
