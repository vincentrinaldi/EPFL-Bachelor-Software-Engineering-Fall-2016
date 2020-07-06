package ch.epfl.sweng.tutosaurus;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
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

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class HelpFragmentTest {

    // Simple tests which doesn't cover any lines of the code have been added
    // to check if some texts have been modified

    @Rule
    public IntentsTestRule<HomeScreenActivity> activityRule = new IntentsTestRule<>(
            HomeScreenActivity.class,
            true,
            false
    );

    @Before
    public void logIn() {
        Task<AuthResult> login = FirebaseAuth.getInstance().signInWithEmailAndPassword("albert.einstein@epfl.ch", "tototo");
        try {
            Tasks.await(login);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        activityRule.launchActivity(new Intent().setAction("OPEN_TAB_PROFILE"));
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_help_layout));
    }

    @Before
    public void grantPhonePermission() {
        // Wake sure the phone permission is granted before running these tests.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.CALL_PHONE");
        }
    }

    @Test
    public void testCall() throws InterruptedException {
        Thread.sleep(1000);
        Matcher<Intent> expectedIntent = hasAction(Intent.ACTION_CALL);
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.phoneLogo)).perform(click());
        intended(expectedIntent);
    }

    @Test
    public void testMessage() throws InterruptedException {
        Thread.sleep(1000);
        Matcher<Intent> expectedIntent = hasAction(Intent.ACTION_SEND);
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.messageLogo)).perform(click());
        intended(expectedIntent);
    }

    @Test
    public void checkTextPhoneNumber() {
        onView(withId(R.id.phoneNumberHelp)).check(matches(withText("+41 79 138 0861")));
    }

    @Test
    public void checkTextEmail() {
        onView(withId(R.id.emailAddressHelp)).check(matches(withText("vincent.rinaldi@epfl.ch")));
    }

    @Test
    public void checkTextAvailabilityPhone() {
        onView(withId(R.id.clockMessagePhone)).check(matches(withText("Monday to Friday : 06:00 to 18:00")));
    }

    @Test
    public void checkTextAvailabilityAddress() {
        onView(withId(R.id.clockMessageEmail)).check(matches(withText("Service available 24/7")));
    }

    @Test
    public void checkTextHelpMessagePhone() {
        onView(withId(R.id.helpMessagePhone)).check(matches(withText("Need some help urgently ? You can call our support line. " +
                "You will be put in interaction with one of our support agents.")));
    }

    @Test
    public void checkTextHelpMessageEmail() {
        onView(withId(R.id.helpMessageEmail)).check(matches(withText("You can also send us an email. " +
                "A member of the assistance service will answer to your request soon.")));
    }

    @After
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
