package jp.kztproject.rewardedtodo.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import jp.kztproject.rewardedtodo.MyApplication
import jp.kztproject.rewardedtodo.di.auth.TodoistAuthModule
import project.seito.screen_transition.di.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    HomeActivityModule::class,
    TodoistAuthActivityModule::class,
    ViewModelModule::class,
    RepositoriesModule::class,
    TodoistAuthModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: MyApplication)
}
