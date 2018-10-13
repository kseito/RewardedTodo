package kztproject.jp.splacounter.api

import io.reactivex.Single
import kztproject.jp.splacounter.model.RewardUser
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

    fun createUser(todoistId: Long) : Single<RewardUser> = service.createUser(todoistId)

    fun findUser(userId: Int) : Single<RewardUser> = service.findUser(userId)

}