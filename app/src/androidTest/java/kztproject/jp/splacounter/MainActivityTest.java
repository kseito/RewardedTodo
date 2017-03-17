package kztproject.jp.splacounter;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import kztproject.jp.splacounter.di.AppComponent;
import kztproject.jp.splacounter.module.MockPreferencesModule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Singleton
    @Component(modules = MockPreferencesModule.class)
    public interface TestComponent extends AppComponent {
        void inject(MainActivityTest mainActivityTest);
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MyApplication application = (MyApplication) instrumentation.getTargetContext().getApplicationContext();

        TestComponent component = DaggerMainActivityTest_TestComponent.builder()
                .mockPreferencesModule(new MockPreferencesModule(instrumentation.getContext()))
                .build();
        application.setmAppComponent(component);
        component.inject(this);

    }

    @Test
    public void checkDefaultText() {

        mActivityRule.launchActivity(new Intent());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.text_counter)).check(matches(withText("0")));
    }
}
