package ch.epfl.sweng.tutosaurus;

import android.os.Build;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.tutosaurus.actions.NestedScrollViewScrollToAction;
import ch.epfl.sweng.tutosaurus.activity.MainActivity;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


/**
 * Created by santo on 26/11/16.
 *
 * Integration Test
 * Create a meeting, display it in the list of meetings, delete the meeting
 * .
 */


@RunWith(AndroidJUnit4.class)
public class MeetingTest{


    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(
            MainActivity.class
    );


    @Before
    public void setUp() throws InterruptedException {

        onView(withId(R.id.main_email)).perform(typeText("albert.einstein@epfl.ch"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.main_password)).perform(typeText("tototo"));
        Espresso.closeSoftKeyboard();
        onView(withText("Log in")).perform(click());
        Thread.sleep(4000);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseHelper.getInstance().getMeetingsRefForUser(uid).removeValue();
        DatabaseHelper.getInstance().getMeetingRequestsRef().child(uid).removeValue();
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_findTutors_layout));
        Thread.sleep(1000);
        onView(withId(R.id.byName)).perform(click());
        onView(withId(R.id.nameToSearch)).perform(typeText("Albert Einstein"));
        onView(withId(R.id.searchByName)).perform(click());
        Thread.sleep(1000);
        onData(anything()).inAdapterView(withId(R.id.tutorList)).atPosition(0).perform(click());
        onView(withId(R.id.createMeetingButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());
    }


    @Test
    public void testRequestAndConfirmMeeting() throws InterruptedException, UiObjectNotFoundException {


        int year = 2020;
        int month = 11;
        int day = 15;

        onView(withId(R.id.pickDateTime)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());

        int minutes = 50;
        int hour = 10;
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(hour, minutes));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.dateView)).check(matches(withText("  2020, 11/15")));
        onView(withId(R.id.timeView)).check(matches(withText("   h 10:50")));

        onData(anything()).inAdapterView(withId(R.id.courseListView)).atPosition(0).perform(click());

        closeSoftKeyboard();
        onView(withId(R.id.description)).perform(typeText("Einstein, I love you"));
        closeSoftKeyboard();

        onView(withId(R.id.addMeeting)).perform(click());
        Thread.sleep(1000);
        onData(anything()).inAdapterView(withId(R.id.meetingRequests)).atPosition(0).
                onChildView(withId(R.id.meeting_confirmation_row_confirm)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_meetings_layout));
        Thread.sleep(2000);
        onData(anything()).inAdapterView(withId(R.id.meetingList)).atPosition(0).
                onChildView(withId(R.id.showDetailsMeeting)).perform(click());
        Thread.sleep(500);
        onData(anything()).inAdapterView(withId(R.id.meetingList)).atPosition(0).
                onChildView(withId(R.id.showLocationMeeting)).perform(click());
        Thread.sleep(1000);
        allowPermissionsIfNeeded();
        Espresso.pressBack();
    }

    
    @Test
    public void testPlacePicker() throws InterruptedException {
        onView(withId(R.id.pickLocation)).perform(click());
    }


    @After
    public void deleteMeeting() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseHelper.getInstance().getMeetingsRefForUser(uid).removeValue();
    }


    private static void allowPermissionsIfNeeded() throws UiObjectNotFoundException {
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().clickable(true).checkable(false).index(1));
            if (allowPermissions.exists()) {
                allowPermissions.click();
            }
        }
    }


}