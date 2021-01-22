package jp.kztproject.rewardedtodo.data.reward.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.data.reward.database.model.RewardEntity

@Dao
interface RewardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReward(rewardEntity: RewardEntity)

    @Query("SELECT * FROM RewardEntity")
    fun findAll(): List<RewardEntity>

    @Query("SELECT * FROM RewardEntity")
    fun findAllAsFlow(): Flow<List<RewardEntity>>

    @Query("SELECT * FROM RewardEntity WHERE id = :id")
    fun findBy(id: Int): RewardEntity?

    @Update
    fun updateReward(address: RewardEntity)

    @Delete
    fun deleteReward(rewardEntity: RewardEntity)
}