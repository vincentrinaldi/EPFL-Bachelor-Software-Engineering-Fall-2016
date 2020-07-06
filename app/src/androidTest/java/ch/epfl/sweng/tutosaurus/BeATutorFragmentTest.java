package ch.epfl.sweng.tutosaurus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class BeATutorFragmentTest {

    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getTargetContext());

    @Rule
    public IntentsTestRule<HomeScreenActivity> activityRule = new IntentsTestRule<>(
            HomeScreenActivity.class,
            true,
            false
    );

    @Before
    public void logIn() throws InterruptedException {
        Task<AuthResult> login = FirebaseAuth.getInstance().signInWithEmailAndPassword("albert.einstein@epfl.ch", "tototo");
        try {
            Tasks.await(login);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        activityRule.launchActivity(new Intent().setAction("OPEN_TAB_PROFILE"));
        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).perform(open());
        Thread.sleep(200);
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_beATutor_layout));
    }

    @Test
    public void testBeATutorTabEnglishCheckbox() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_english", false)) {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_english")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_english", false), equalTo(false));
        } else {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_english")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_english", false), equalTo(true));
        }
    }

    @Test
    public void testBeATutorTabMathematicsCheckbox() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_mathematics", false)) {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_mathematics")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_mathematics", false), equalTo(false));
        } else {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_mathematics")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_mathematics", false), equalTo(true));
        }
    }

    @Test
    public void testBeATutorTabMathematicsEditTextCustom() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_mathematics", false)) {
            onData(PreferenceMatchers.withKey("edit_text_preference_mathematics")).check(matches(isEnabled()));
            onData(PreferenceMatchers.withKey("edit_text_preference_mathematics")).perform(click());
            Thread.sleep(1000);
            onView(withId(16908291)).perform(clearText(), typeText("I love Mathematics"));
            closeSoftKeyboard();
            onView(withId(16908313)).perform(click());
            Thread.sleep(1000);
            assertThat(sharedPreferences.getString("edit_text_preference_mathematics", "Enter your description."),
                    equalTo("I love Mathematics"));
        } else {
            onData(PreferenceMatchers.withKey("edit_text_preference_mathematics")).check(matches(not(isEnabled())));
        }
    }

    @Test
    public void testBeATutorTabMathematicsEditTextDefault() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_mathematics", false)) {
            onData(PreferenceMatchers.withKey("edit_text_preference_mathematics")).check(matches(isEnabled()));
            onData(PreferenceMatchers.withKey("edit_text_preference_mathematics")).perform(click());
            Thread.sleep(1000);
            onView(withId(16908291)).perform(clearText());
            closeSoftKeyboard();
            onView(withId(16908313)).perform(click());
            Thread.sleep(1000);
            assertThat(sharedPreferences.getString("edit_text_preference_mathematics", "Enter your description."),
                    equalTo("Enter your description."));
        } else {
            onData(PreferenceMatchers.withKey("edit_text_preference_mathematics")).check(matches(not(isEnabled())));
        }
    }

    @Test
    public void testBeATutorTabSecondDatabaseChangeForLanguage() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_english", false)) {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_english")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_english", false), equalTo(false));
        } else {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_english")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_english", false), equalTo(true));
        }
    }

    @Test
    public void testBeATutorTabSecondDatabaseChangeForSubject() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_mathematics", false)) {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_mathematics")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_mathematics", false), equalTo(false));
        } else {
            Thread.sleep(2000);
            onData(PreferenceMatchers.withKey("checkbox_preference_mathematics")).perform(click());
            Thread.sleep(2000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_mathematics", false), equalTo(true));
        }
    }

    @After
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
