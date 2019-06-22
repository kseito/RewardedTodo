package kztproject.jp.splacounter.auth.repository

interface IAuthRepository {

    suspend fun signUp(todoistToken: String)

    suspend fun login(inputString: String)
}