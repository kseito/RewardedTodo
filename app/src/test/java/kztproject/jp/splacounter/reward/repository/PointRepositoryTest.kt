package kztproject.jp.splacounter.reward.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import kztproject.jp.splacounter.reward.api.RewardListService
import org.junit.Test

class PointRepositoryTest {

    private val mockRewardListService: RewardListService = mock()

    private val target: PointRepository = PointRepository(mockRewardListService)

    @Test
    fun loadPoint() {
        target.loadPoint(1)

        verify(mockRewardListService, times(1)).getPoint(1)
    }

    @Test
    fun consumePoint() {
        target.consumePoint(1, 2)

        verify(mockRewardListService, times(1)).updatePoint(1, -2)
    }
}