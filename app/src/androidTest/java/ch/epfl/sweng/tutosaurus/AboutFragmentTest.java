package ch.epfl.sweng.tutosaurus;

import android.content.Intent;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AboutFragmentTest {

    // Simple tests which doesn't cover any lines of the code just for checking
    // if some texts have been modified like the version number or the description

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
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_about_layout));
    }

    @Test
    public void checkTextTitle() {
        onView(withId(R.id.aboutTitle)).check(matches(withText("TutoSaurus ©")));
    }

    @Test
    public void checkTextVersion() {
        onView(withId(R.id.aboutVersion)).check(matches(withText("Version : 1.0.0")));
    }

    @Test
    public void checkTextDescription() {
        onView(withId(R.id.aboutDescription)).check(matches(withText("Copyright EPFL Inc. 2016. All rights reserved.")));
    }

    @Test
    public void checkTextDevelopers() {
        onView(withId(R.id.aboutDevelopers)).check(matches(withText("This app is developed by team No Clue from EPFL.")));
    }

    @Test
    public void checkTextGoal() {
        onView(withId(R.id.aboutGoal)).check(matches(withText("This Android application is intended for students " +
                "and tutors from Switzerland and provides study material needed to help every student realize their potential.")));
    }

    @After
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }
}