@file:Suppress("DeferredIsResult")

package kztproject.jp.splacounter.reward.infrastructure.api

import kotlinx.coroutines.Deferred
import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.database.model.RewardPoint
import retrofit2.http.*

interface RewardPointService {

    @GET("/api/users/{user_id}")
    fun getPoint(@Path("user_id") userId: Long): Deferred<RewardPoint>

    //TODO change return value to RewardPoint
    @FormUrlEncoded
    @PUT("/api/users/{user_id}")
    fun updatePoint(@Path("user_id") userId: Long,
                    @Field("additional_point") additionalPoint: Int): Deferred<RewardUser>
}