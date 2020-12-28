package jp.kztproject.rewardedtodo.data.reward.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.data.reward.database.model.RewardEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class RewardDaoTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var database: AppDatabase
    private val testRewards = arrayListOf(
            RewardEntity(1, "nintendo switch", 100, 10F, "this is really I want", false),
            RewardEntity(2, "new keyboard", 50, 10F, null, false),
            RewardEntity(3, "joel robuchon", 125, 10F, null, true)
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
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

    @Test
    fun findAllAsFlow() = runBlocking {
        val dao = database.rewardDao()
        testRewards.forEach { dao.insertReward(it) }

        val rewards = dao.findAllAsFlow().take(1).first()
        assertThat(rewards.size).isEqualTo(3)
        assertThat(rewards[0].name).isEqualTo("nintendo switch")
        Unit
    }
}