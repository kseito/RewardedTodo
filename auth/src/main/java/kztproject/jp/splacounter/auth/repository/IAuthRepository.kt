package kztproject.jp.splacounter.auth.repository

import io.reactivex.Completable

interface IAuthRepository {

    fun signUp(todoistToken: String): Completable

    fun login(inputString: String): Completable
}