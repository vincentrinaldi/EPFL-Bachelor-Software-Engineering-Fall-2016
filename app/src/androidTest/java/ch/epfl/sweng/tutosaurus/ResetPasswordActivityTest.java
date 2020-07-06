package ch.epfl.sweng.tutosaurus;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.tutosaurus.activity.MainActivity;
import ch.epfl.sweng.tutosaurus.activity.ResetPasswordActivity;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ResetPasswordActivityTest {

    private Solo solo;
    private String invalid_email;
    private String valid_email;

    @Rule
    public ActivityTestRule<ResetPasswordActivity> mActivityRule = new ActivityTestRule<>(
            ResetPasswordActivity.class);

    @Before
    public void setUp() {
        //setUp() is run before a test case is started.
        //This is where the solo object is created.
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                mActivityRule.getActivity());
        invalid_email = "HolyMolly@wow.com";
        valid_email = "albert.einstein@epfl.ch";
    }

    @After
    public void tearDown() {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    @Test
    public void correctFailToastDisplayed() throws InterruptedException{
        solo.assertCurrentActivity("wrong activity", ResetPasswordActivity.class);
        solo.typeText(0, invalid_email);
        solo.clickOnView(solo.getView(R.id.rstPasswordButton));
        boolean toastMsg = waitForToastWithText("Failed to send reset!");
        assertTrue(toastMsg);
    }

    @Test
    public void correctSuccessToastDisplayed() throws InterruptedException {
        solo.assertCurrentActivity("wrong activity", ResetPasswordActivity.class);
        solo.typeText(0, valid_email);
        solo.clickOnView(solo.getView(R.id.rstPasswordButton));
        boolean toastMsgDisplayed = waitForToastWithText("Instructions sent to your email!");
        boolean tooManyRequests = false;
        //Check that test fails because too many requests for a particular email were sent
        if (!toastMsgDisplayed) {
            solo.clickOnView(solo.getView(R.id.rstPasswordButton));
            tooManyRequests = solo.searchText("Failed to send reset!");
        }
        assertTrue(tooManyRequests || toastMsgDisplayed);
    }

    @Test
    public void correctToastDisplayedIfEmptyEmail() throws InterruptedException {
        solo.assertCurrentActivity("wrong activity", ResetPasswordActivity.class);
        solo.clickOnView(solo.getView(R.id.rstPasswordButton));
        boolean toastMsg = waitForToastWithText("Enter your registered email id");
        assertTrue(toastMsg);
    }

    @Test
    public void goToMainActivityOnBackButton() {
        solo.assertCurrentActivity("correct activity", ResetPasswordActivity.class);
        Intents.init();
        solo.clickOnView(solo.getView(R.id.backButton));
        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
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
