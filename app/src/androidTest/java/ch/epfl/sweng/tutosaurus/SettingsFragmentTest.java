package ch.epfl.sweng.tutosaurus;

import android.app.Instrumentation;
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

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class SettingsFragmentTest {

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
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings_layout));
    }

    @Test
    public void testSettingTabNotificationCheckbox() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_notification", true)) {
            onData(PreferenceMatchers.withKey("checkbox_preference_notification")).perform(click());
            Thread.sleep(1000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_notification", true), equalTo(false));
        } else {
            onData(PreferenceMatchers.withKey("checkbox_preference_notification")).perform(click());
            Thread.sleep(1000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_notification", true), equalTo(true));
        }
    }

    @Test
    public void testSettingTabChangePasswordActivityIntent() throws InterruptedException {
        Thread.sleep(500);
        Matcher<Intent> expectedIntent = allOf(hasAction("example.action.ChangePasswordActivity"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onData(PreferenceMatchers.withKey("intent_preference_password")).perform(click());
        intended(expectedIntent);
    }

    @Test
    public void testSettingTabEPFLWebSiteBrowser() throws InterruptedException {
        Thread.sleep(500);
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData("https://www.epfl.ch/"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onData(PreferenceMatchers.withKey("intent_preference_epfl")).perform(click());
        intended(expectedIntent);
    }

    @Test
    public void testSettingTabSecondNotificationChange() throws InterruptedException {
        if (sharedPreferences.getBoolean("checkbox_preference_notification", true)) {
            onData(PreferenceMatchers.withKey("checkbox_preference_notification")).perform(click());
            Thread.sleep(1000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_notification", true), equalTo(false));
        } else {
            onData(PreferenceMatchers.withKey("checkbox_preference_notification")).perform(click());
            Thread.sleep(1000);
            assertThat(sharedPreferences.getBoolean("checkbox_preference_notification", true), equalTo(true));
        }
    }

    @After
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
