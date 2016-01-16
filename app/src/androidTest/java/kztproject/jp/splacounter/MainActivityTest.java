package kztproject.jp.splacounter;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void checkDefaultText() {
        onView(withId(R.id.text_counter)).check(matches(withText("0")));
    }

    @Test
    public void clickCountUp() {
        onView(withId(R.id.count_up_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("1")));
    }

    @Test
    public void clickCountDown() {
        onView(withId(R.id.count_up_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("1")));

        onView(withId(R.id.count_down_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("0")));

        onView(withId(R.id.count_down_button)).perform(click());
        onView(withId(R.id.text_counter)).check(matches(withText("0")));
    }

}
