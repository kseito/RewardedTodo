package jp.kztproject.rewardedtodo.data.reward.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jp.kztproject.rewardedtodo.data.reward.database.model.RewardEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
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
        RewardEntity(1, "nintendo switch", 10F, "this is really I want", false),
        RewardEntity(2, "new keyboard", 10F, null, false),
        RewardEntity(3, "joel robuchon", 10F, null, true),
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
        rewards shouldHaveSize 1
    }

    @Test
    fun deleteReward() {
        val dao = database.rewardDao()
        dao.insertReward(testRewards[0])
        dao.deleteReward(testRewards[0])

        val rewards = dao.findAll()
        rewards.shouldBeEmpty()
    }

    @Test
    fun findAll() {
        val dao = database.rewardDao()
        dao.findAll().shouldBeEmpty()

        testRewards.forEach { dao.insertReward(it) }

        val rewards = dao.findAll()
        rewards shouldHaveSize 3
        rewards[0].name shouldBe "nintendo switch"
    }

    @Test
    fun findAllAsFlow() = runTest {
        val dao = database.rewardDao()
        testRewards.forEach { dao.insertReward(it) }

        val rewards = dao.findAllAsFlow().take(1).first()
        rewards shouldHaveSize 3
        rewards[0].name shouldBe "nintendo switch"
    }
}
