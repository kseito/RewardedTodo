@file:Suppress("DeferredIsResult")

package jp.kztproject.rewardedtodo.data.reward.api

import jp.kztproject.rewardedtodo.data.reward.api.model.NumberOfTicket
import kotlinx.coroutines.Deferred
import jp.kztproject.rewardedtodo.domain.reward.RewardUser
import retrofit2.http.*

interface RewardPointService {

    @GET("/api/users/{user_id}")
    fun getPoint(@Path("user_id") userId: Long): Deferred<NumberOfTicket>

    //TODO change return value to RewardPoint
    @FormUrlEncoded
    @PUT("/api/users/{user_id}")
    fun updatePoint(@Path("user_id") userId: Long,
                    @Field("additional_point") additionalPoint: Int): Deferred<RewardUser>
}