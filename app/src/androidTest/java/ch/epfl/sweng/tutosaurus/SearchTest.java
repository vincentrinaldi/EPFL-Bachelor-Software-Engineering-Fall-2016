package ch.epfl.sweng.tutosaurus;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sweng.tutosaurus.actions.NestedScrollViewScrollToAction;
import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;
import ch.epfl.sweng.tutosaurus.model.Course;
import ch.epfl.sweng.tutosaurus.model.FullCourseList;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Created by albertochiappa on 03/12/16.
 */

public class SearchTest extends ActivityInstrumentationTestCase2<HomeScreenActivity> {
    public SearchTest() {
        super(HomeScreenActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testCanSearchBySubject() throws InterruptedException{
        getActivity();
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_findTutors_layout));
        Thread.sleep(1000);
        onView(withId(R.id.bySubject)).perform(click());
        onView(withId(R.id.courseList)).check(matches(isDisplayed()));
        Thread.sleep(500);
        ArrayList<Course> courseArrayList = FullCourseList.getInstance().getListOfCourses();
        for(int i=0; i<courseArrayList.size(); i++){
            onData(anything()).inAdapterView(withId(R.id.courseList)).atPosition(i)
                    .check(matches(hasDescendant(allOf(withId(R.id.courseName), withText(containsString(courseArrayList.get(i).getName()))))));
            onData(anything()).inAdapterView(withId(R.id.courseList)).atPosition(i).perform(click());
            onData(anything()).inAdapterView(withId(R.id.tutorList)).atPosition(0).perform(click());
            onView(withText(courseArrayList.get(i).getName()))
                    .perform(NestedScrollViewScrollToAction.scrollTo(), click());
            onView(withText(courseArrayList.get(i).getName()))
                    .perform(NestedScrollViewScrollToAction.scrollTo(), click());
            Espresso.pressBack();
            Espresso.pressBack();
            Thread.sleep(500);

        }
        onView(withId(R.id.bySubject)).perform(click());
        onView(withId(R.id.courseList)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testCanSearchByName() throws InterruptedException {
        getActivity();
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_findTutors_layout));
        Thread.sleep(1000);
        onView(withId(R.id.byName)).perform(click());
        onView(withId(R.id.nameToSearch)).perform(typeText("Albert Einstein"));
        onView(withId(R.id.searchByName)).perform(click());
        Thread.sleep(1000);
        onData(anything()).inAdapterView(withId(R.id.tutorList)).atPosition(0).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.profileName)).check(matches(withText("Albert Einstein")));
        Espresso.pressBack();
        Espresso.pressBack();
        Thread.sleep(1000);
        onView(withId(R.id.byName)).perform(click());
        onView(withId(R.id.courseList)).check(matches(not(isDisplayed())));
        onView(withId(R.id.nameLayout)).check(matches(not(isDisplayed())));
        onView(withId(R.id.byName)).perform(click());
        onView(withId(R.id.nameLayout)).check(matches(isDisplayed()));

    }

    @Test
    public void testCanShowFullList() throws InterruptedException {
        getActivity();
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_findTutors_layout));
        Thread.sleep(1000);
        onView(withId(R.id.showFullList)).perform(click());
        Thread.sleep(1000);
        Espresso.pressBack();
        Thread.sleep(500);
        onView(withId(R.id.byName)).perform(click());
        onView(withId(R.id.showFullList)).check(matches(not(isDisplayed())));
        onView(withId(R.id.byName)).perform(click());
        onView(withId(R.id.showFullList)).check(matches(isDisplayed()));
    }

}
