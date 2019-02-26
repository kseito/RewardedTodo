package kztproject.jp.splacounter.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import kztproject.jp.splacounter.MyApplication
import kztproject.jp.splacounter.auth.di.AuthApiModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    BaseActivityModule::class,
    RepositoryModule::class,
    AuthApiModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: MyApplication)
}
