package ch.epfl.sweng.tutosaurus;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.widget.AdapterView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.sweng.tutosaurus.activity.ChatActivity;
import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

public class MessagingFragmentTests {

    private static final String TAG = "MessagingFragmentTests";

    @Rule
    public ActivityTestRule<HomeScreenActivity> rule = new ActivityTestRule<>(
            HomeScreenActivity.class,
            true,
            false
    );

    @Before
    public void logIn(){
        Task<AuthResult> logintask = FirebaseAuth.getInstance().signInWithEmailAndPassword("albert.einstein@epfl.ch", "tototo");
        try {
            Log.d(TAG, "logging in...");
            Tasks.await(logintask);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOpenChat() {
        Intents.init();
        rule.launchActivity(new Intent().setAction("OPEN_TAB_MESSAGES"));
        //onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        //onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messaging_layout));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything())
                .inAdapterView(allOf(
                        isAssignableFrom(AdapterView.class),
                        isDisplayed()
                ))
                .atPosition(0)
                .perform(click());
        intended(hasComponent(hasClassName(ChatActivity.class.getName())));
        Intents.release();
    }

    @Test
    public void testTypeMessage() {
        rule.launchActivity(new Intent().setAction("OPEN_TAB_MESSAGES"));
        //onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        //onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messaging_layout));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(anything())
                .inAdapterView(allOf(
                        isAssignableFrom(AdapterView.class),
                        isDisplayed()
                ))
                .atPosition(0)
                .perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.chat_message_input)).perform(typeText("Hello World!"));
        closeSoftKeyboard();
        onView(withId(R.id.chat_message_send)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOpenChatChatList() {
        Intents.init();
        rule.launchActivity(new Intent().setAction("OPEN_TAB_MESSAGES"));
        //onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        //onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messaging_layout));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.action_switch_adapter)).perform(click());
        onView(withId(R.id.action_switch_adapter)).perform(click());

        onData(anything())
                .inAdapterView(allOf(
                        isAssignableFrom(AdapterView.class),
                        isDisplayed()
                ))
                .atPosition(0)
                .perform(click());
        intended(hasComponent(hasClassName(ChatActivity.class.getName())));
        Intents.release();
    }

    @After
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }


}
