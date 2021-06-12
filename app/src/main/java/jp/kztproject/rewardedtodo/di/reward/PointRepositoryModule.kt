package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.domain.reward.repository.IPointRepository
import jp.kztproject.rewardedtodo.reward.repository.PointRepository

@InstallIn(SingletonComponent::class)
@Module
interface PointRepositoryModule {

    @Binds
    fun bindPointRepository(repository: PointRepository): IPointRepository
}