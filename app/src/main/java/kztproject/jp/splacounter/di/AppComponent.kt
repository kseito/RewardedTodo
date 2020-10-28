package kztproject.jp.splacounter.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import kztproject.jp.splacounter.MyApplication
import kztproject.jp.splacounter.data.todo.di.TodoDatabaseModule
import kztproject.jp.splacounter.data.todo.di.TodoRepositoryModule
import project.seito.screen_transition.di.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    HomeActivityModule::class,
    ViewModelModule::class,
    TodoDatabaseModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: MyApplication)
}
