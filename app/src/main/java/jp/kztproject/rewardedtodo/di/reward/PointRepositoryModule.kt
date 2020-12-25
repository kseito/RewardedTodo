package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.reward.application.repository.IPointRepository
import jp.kztproject.rewardedtodo.reward.repository.PointRepository

@Module
interface PointRepositoryModule {

    @Binds
    fun bindPointRepository(repository: PointRepository): IPointRepository
}