package kztproject.jp.splacounter;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import kztproject.jp.splacounter.component.PrefsComponent;
import kztproject.jp.splacounter.module.MockPreferencesModule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Inject
    SharedPreferences mPrefs;

    @Singleton
    @Component(modules = MockPreferencesModule.class)
    public interface TestComponent extends PrefsComponent {
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
        application.setmPrefsComponent(component);
        component.inject(this);
//        initPreferences();

    }

    @Test
    public void checkDefaultText() {
//        initPreferences();

        mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.text_counter)).check(matches(withText("0")));
    }

    @Test
    public void clickCountUp() {

//        initPreferences();
        mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.count_up_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("1")));
    }

    @Test
    public void clickCountDown() {

        mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.count_up_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("1")));

        onView(withId(R.id.count_down_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("0")));

        onView(withId(R.id.count_down_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("0")));

//        initPreferences();
    }

    @Test
    public void checkSaveCount() {

        mActivityRule.launchActivity(new Intent());

//        mActivityRule.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                MainActivity activity = mActivityRule.getActivity();
//
//                Button countUpButton = (Button) activity.findViewById(R.id.count_up_button);
//                countUpButton.performClick();
//                countUpButton.performClick();
//                countUpButton.performClick();
//
//                int count = mPrefs.getInt(MainActivity.COUNT, 0);
//                assertEquals(3, count);
//
//            }
//        });

        onView(withId(R.id.count_up_button)).perform(click());
        onView(withId(R.id.count_up_button)).perform(click());
        onView(withId(R.id.count_up_button)).perform(click());

        int count = mPrefs.getInt(MainActivity.COUNT, 0);
        Assert.assertEquals(3, count);


    }
}
