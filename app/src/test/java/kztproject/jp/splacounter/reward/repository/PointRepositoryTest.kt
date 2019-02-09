package kztproject.jp.splacounter.reward.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import kztproject.jp.splacounter.reward.api.RewardListClient
import org.junit.Test

class PointRepositoryTest {

    private val mockRewardListClient: RewardListClient = mock()

    private val target: PointRepository = PointRepository(mockRewardListClient)

    @Test
    fun loadPoint() {
        target.loadPoint(1)

        verify(mockRewardListClient, times(1)).getPoint(1)
    }

    @Test
    fun consumePoint() {
        target.consumePoint(1, 2)

        verify(mockRewardListClient, times(1)).consumePoint(1, -2)
    }
}