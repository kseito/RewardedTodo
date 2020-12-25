package jp.kztproject.rewardedtodo.auth.repository

interface IAuthRepository {

    suspend fun signUp(todoistToken: String)

    suspend fun login(inputString: String)
}