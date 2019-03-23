package kztproject.jp.splacounter.reward.database

import android.arch.persistence.room.*
import kztproject.jp.splacounter.reward.database.model.Reward

@Dao
interface RewardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReward(reward: Reward)

    @Query("SELECT * FROM Reward")
    fun findAll(): Array<Reward>

    @Query("SELECT * FROM Reward WHERE id = :id")
    fun findBy(id: Int): Reward?

    @Update
    fun updateReward(address: Reward)

    @Delete
    fun deleteReward(reward: Reward)
}