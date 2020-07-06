package ch.epfl.sweng.tutosaurus;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    private Solo solo;

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
    }

    @Test
    public void testCamera() throws InterruptedException {
        Thread.sleep(500);
        Matcher<Intent> expectedIntent = allOf(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.picture_view)).perform(click());
        onView(withText("Take picture with camera")).perform(click());
        intended(expectedIntent);
    }

    @Test
    public void testGallery() throws InterruptedException {
        Thread.sleep(500);
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.picture_view)).perform(click());
        onView(withText("Load picture from gallery")).perform(click());
        intended(expectedIntent);
    }

    @Test
    public void testImageCameraPicker() throws InterruptedException {
        Thread.sleep(500);
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.drawable.dino_logo);

        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.android.camera")).respondWith(result);

        onView(withId(R.id.picture_view)).perform(click());
        onView(withText("Take picture with camera")).perform(click());

        intended(toPackage("com.android.camera"));
    }

    @Test
    public void testImageGalleryPickerSuccess() throws InterruptedException {
        Thread.sleep(500);
        Intent resultData = new Intent();
        Uri uri1 = Uri.parse("android.resource://ch.epfl.sweng.tutosaurus/" + R.drawable.dino_logo);
        resultData.setDataAndType(uri1, "image/*");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.android.gallery")).respondWith(result);

        onView(withId(R.id.picture_view)).perform(click());
        onView(withText("Load picture from gallery")).perform(click());

        intended(toPackage("com.android.gallery"));
    }

    @Test
    public void testImageGalleryPickerFail() throws InterruptedException {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                activityRule.getActivity());
        Thread.sleep(500);
        Intent resultData = new Intent();
        Uri uri1 = Uri.parse("");
        resultData.setDataAndType(uri1, "image/*");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.android.gallery")).respondWith(result);

        onView(withId(R.id.picture_view)).perform(click());
        onView(withText("Load picture from gallery")).perform(click());

        intended(toPackage("com.android.gallery"));

        boolean toastMessageDisplayedIsCorrect = waitForToastWithText("Unable to load the image");
        assertTrue(toastMessageDisplayedIsCorrect);
        solo.finishOpenedActivities();
    }

    @Test
    public void testBackButtonOfHomeScreen() throws InterruptedException {
        Thread.sleep(500);
        onView(withId(R.id.drawer_layout)).perform(open());
        Espresso.pressBack();
    }

    @After
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private boolean waitForToastWithText(String toastText) throws InterruptedException {
        boolean toastFound = solo.searchText(toastText);
        int numEfforts = 0;
        while(toastFound == false && numEfforts < 5000){
            Thread.sleep(2);
            toastFound = solo.searchText(toastText);
            numEfforts++;
        }
        return toastFound;
    }
}
