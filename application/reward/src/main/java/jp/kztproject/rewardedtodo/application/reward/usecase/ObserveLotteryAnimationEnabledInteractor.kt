package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryAnimationPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLotteryAnimationEnabledInteractor @Inject constructor(
    private val preferenceRepository: ILotteryAnimationPreferenceRepository,
) : ObserveLotteryAnimationEnabledUseCase {
    override fun execute(): Flow<Boolean> = preferenceRepository.observeEnabled()
}
