package kztproject.jp.splacounter.database

import android.arch.persistence.room.*
import kztproject.jp.splacounter.database.model.Reward

@Dao
interface RewardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReward(reward: Reward)

    @Query("SELECT * FROM Reward")
    fun findAll(): Array<Reward>

    @Update
    fun updateReward(address: Reward)

    @Delete
    fun deleteReward(reward: Reward)
}