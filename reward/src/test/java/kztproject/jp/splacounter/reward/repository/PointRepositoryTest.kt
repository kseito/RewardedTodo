package kztproject.jp.splacounter.reward.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import kztproject.jp.splacounter.reward.api.RewardPointService
import org.junit.Test

class PointRepositoryTest {

    private val mockRewardPointService: RewardPointService = mock()

    private val target: PointRepository = PointRepository(mockRewardPointService)

    @Test
    fun loadPoint() {
        target.loadPoint(1)

        verify(mockRewardPointService, times(1)).getPoint(1)
    }

    @Test
    fun consumePoint() {
        target.consumePoint(1, 2)

        verify(mockRewardPointService, times(1)).updatePoint(1, -2)
    }
}