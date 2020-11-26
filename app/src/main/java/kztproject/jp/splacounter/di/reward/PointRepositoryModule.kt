package kztproject.jp.splacounter.di.reward

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.repository.PointRepository

@Module
interface PointRepositoryModule {

    @Binds
    fun bindPointRepository(repository: PointRepository): IPointRepository
}