package kztproject.jp.splacounter.database

import android.arch.persistence.room.Room
import kztproject.jp.splacounter.BuildConfig
import kztproject.jp.splacounter.model.Reward
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class RewardDaoTest {

    private lateinit var database: AppDatabase
    private val testRewards = arrayListOf(
            Reward(1, "nintendo switch", 100, "this is really I want", null),
            Reward(2, "new keyboard", 50, null, null),
            Reward(3, "joel robuchon", 125, null, null)
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.application, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertReward() {
        val dao = database.rewardDao()
        dao.insertReward(testRewards[0])

        val rewards = dao.findAll()
        assertThat(rewards.size).isEqualTo(1)
    }

    @Test
    fun deleteReward() {
        val dao = database.rewardDao()
        dao.insertReward(testRewards[0])
        dao.deleteReward(testRewards[0])

        val rewards = dao.findAll()
        assertThat(rewards.size).isEqualTo(0)
    }

    @Test
    fun findAll() {
        val dao = database.rewardDao()
        assertThat(dao.findAll()).isEmpty()

        testRewards.forEach { dao.insertReward(it) }

        val rewards = dao.findAll()
        assertThat(rewards.size).isEqualTo(3)
        assertThat(rewards[0].name).isEqualTo("nintendo switch")
    }
}