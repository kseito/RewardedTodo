package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryAnimationPreferenceRepository
import javax.inject.Inject

class SetLotteryAnimationEnabledInteractor @Inject constructor(
    private val preferenceRepository: ILotteryAnimationPreferenceRepository,
) : SetLotteryAnimationEnabledUseCase {
    override suspend fun execute(enabled: Boolean) {
        preferenceRepository.setEnabled(enabled)
    }
}
