package kztproject.jp.splacounter.reward.api

import io.reactivex.Single
import kztproject.jp.splacounter.auth.api.model.RewardUser
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RewardListClient(url: HttpUrl) {

    private val service: RewardListService

    init {

        val client = OkHttpClient.Builder().build()

        service = Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RewardListService::class.java)
    }

    fun getPoint(userId: Long): Single<Int> = service.getPoint(userId).map { it.point }

    fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser> =
            service.updatePoint(userId, -additionalPoint)

}