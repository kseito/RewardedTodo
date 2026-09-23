package jp.kztproject.rewardedtodo.application.todo

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.reward.repository.IAccountCacheRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ClearLocalDataInteractorTest {

    private val todoRepository = mockk<ITodoRepository>(relaxed = true)
    private val accountCacheRepository = mockk<IAccountCacheRepository>(relaxed = true)
    private val interactor = ClearLocalDataInteractor(todoRepository, accountCacheRepository)

    @Test
    fun `Todoとアカウントキャッシュをどちらも削除する`() = runTest {
        interactor.execute()

        coVerify(exactly = 1) { todoRepository.deleteAll() }
        coVerify(exactly = 1) { accountCacheRepository.clear() }
    }

    @Test
    fun `Todoの削除に失敗してもキャッシュの削除は行われ例外は投げない`() = runTest {
        coEvery { todoRepository.deleteAll() } throws IllegalStateException()

        interactor.execute()

        coVerify(exactly = 1) { accountCacheRepository.clear() }
    }

    @Test
    fun `キャッシュの削除に失敗しても例外は投げない`() = runTest {
        coEvery { accountCacheRepository.clear() } throws IllegalStateException()

        interactor.execute()

        coVerify(exactly = 1) { todoRepository.deleteAll() }
    }
}
