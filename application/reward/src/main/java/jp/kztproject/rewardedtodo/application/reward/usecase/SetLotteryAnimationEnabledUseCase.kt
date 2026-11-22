package jp.kztproject.rewardedtodo.application.reward.usecase

interface SetLotteryAnimationEnabledUseCase {
    suspend fun execute(enabled: Boolean)
}
