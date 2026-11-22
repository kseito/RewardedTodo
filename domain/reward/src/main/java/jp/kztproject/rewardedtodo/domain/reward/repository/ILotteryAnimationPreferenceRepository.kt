package jp.kztproject.rewardedtodo.domain.reward.repository

import kotlinx.coroutines.flow.Flow

/** 抽選演出の表示設定を永続化する。 */
interface ILotteryAnimationPreferenceRepository {
    fun observeEnabled(): Flow<Boolean>

    suspend fun setEnabled(enabled: Boolean)
}
