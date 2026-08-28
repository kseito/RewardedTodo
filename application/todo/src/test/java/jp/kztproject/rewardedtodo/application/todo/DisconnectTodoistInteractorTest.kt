package jp.kztproject.rewardedtodo.application.todo

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthSessionRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DisconnectTodoistInteractorTest {

    private val authSessionRepository = mockk<ITodoistAuthSessionRepository>(relaxed = true)
    private val credentialRepository = mockk<ITodoistCredentialRepository>(relaxed = true)
    private val interactor = DisconnectTodoistInteractor(authSessionRepository, credentialRepository)

    @Test
    fun `execute deletes the credential and clears any pending session`() = runTest {
        interactor.execute().isSuccess shouldBe true

        coVerify { credentialRepository.deleteCredential() }
        coVerify { authSessionRepository.clearSession() }
    }

    @Test
    fun `execute reports a failure when the deletion throws`() = runTest {
        coEvery { credentialRepository.deleteCredential() } throws IllegalStateException("disk error")

        interactor.execute().isFailure shouldBe true
    }
}
