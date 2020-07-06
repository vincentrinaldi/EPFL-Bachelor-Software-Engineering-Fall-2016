package ch.epfl.sweng.tutosaurus;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Rule;

import ch.epfl.sweng.tutosaurus.activity.ConfirmationActivity;
import ch.epfl.sweng.tutosaurus.activity.RegisterScreenActivity;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

/**
 * Created by Stephane on 12/8/2016.
 */

public class ConfirmationActivityTest extends ActivityInstrumentationTestCase2<ConfirmationActivity> {

    private final static String EXTRA_MESSAGE_FIRST_NAME = "com.example.myfirstapp.FIRSTNAME";
    private final static String EXTRA_MESSAGE_LAST_NAME = "com.example.myfirstapp.LASTNAME";
    private final static String EXTRA_MESSAGE_EMAIL_ADDRESS = "com.example.myfirstapp.EMAILADDRESS";
    private final static String EXTRA_MESSAGE_SCIPER = "com.example.myfirstapp.SCIPER";
    private final static String EXTRA_MESSAGE_GASPAR = "com.example.myfirstapp.GASPAR";

    private final static String validFirst = "albert";
    private final static String validLast = "einstein";
    private final static String validEmail = "albert.einstein@epfl.ch";
    private final static String validSciper = "000000";
    private final static String validGaspar = "albert";

    private String newFirst = "Marie";
    private String newLast = "Curie";
    private String newEmail = "Marie.Curie@epfl.ch";
    private String newSciper = "111111";
    private String newGaspar = "marie";

    private Intent mockIntent1;
    private Intent mockIntent2;

    private Solo solo;

    public ConfirmationActivityTest() {
        super(ConfirmationActivity.class);
    }

    @Rule
    public ActivityTestRule<ConfirmationActivity> mActivityRule = new ActivityTestRule<>(
            ConfirmationActivity.class);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                mActivityRule.getActivity());

        mockIntent1 = new Intent();
        mockIntent1.putExtra(EXTRA_MESSAGE_FIRST_NAME, validFirst);
        mockIntent1.putExtra(EXTRA_MESSAGE_LAST_NAME, validLast);
        mockIntent1.putExtra(EXTRA_MESSAGE_EMAIL_ADDRESS, validEmail);
        mockIntent1.putExtra(EXTRA_MESSAGE_SCIPER, validSciper);
        mockIntent1.putExtra(EXTRA_MESSAGE_GASPAR, validGaspar);

        mockIntent2 = new Intent();
        mockIntent2.putExtra(EXTRA_MESSAGE_FIRST_NAME, newFirst);
        mockIntent2.putExtra(EXTRA_MESSAGE_LAST_NAME, newLast);
        mockIntent2.putExtra(EXTRA_MESSAGE_EMAIL_ADDRESS, newEmail);
        mockIntent2.putExtra(EXTRA_MESSAGE_SCIPER, newSciper);
        mockIntent2.putExtra(EXTRA_MESSAGE_GASPAR, newGaspar);

        setActivityIntent(mockIntent1);
        getActivity();
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testConfirmedProfileInfo() {
        solo.assertCurrentActivity("correct activity", ConfirmationActivity.class);
        boolean firstNameDisplayed = solo.searchText(validFirst);
        boolean lastNameDisplayed = solo.searchText(validLast);
        boolean emailDiplayed = solo.searchText(validEmail);
        boolean sciperDisplayed = solo.searchText(validSciper);
        assertTrue(firstNameDisplayed && lastNameDisplayed && emailDiplayed && sciperDisplayed);
    }

    public void testMismatchedPassword(){
        solo.assertCurrentActivity("correct activity", ConfirmationActivity.class);
        solo.typeText(1, "bla bla");
        solo.typeText(0, "bli bli");
        solo.clickOnView(solo.getView(R.id.backToLoginButton));
        boolean mismatchWarningDisplayed = solo.searchText("Passwords must match");
        assertTrue(mismatchWarningDisplayed);
    }

    public void testEmptyPassword(){
        solo.assertCurrentActivity("correct activity", ConfirmationActivity.class);
        solo.typeText(1, "bli bli");
        solo.clickOnView(solo.getView(R.id.backToLoginButton));
        boolean missingWarningDisplayed = solo.searchText("Password missing");
        assertTrue(missingWarningDisplayed);
    }

    public void testRegisterWithExistingAccountFails() {
        solo.assertCurrentActivity("correct activity", ConfirmationActivity.class);
        solo.typeText(1, "bla bla");
        solo.typeText(0, "bla bla");
        solo.clickOnView(solo.getView(R.id.backToLoginButton));
        boolean failMessageDisplayed = solo.searchText("Auth failed");
        assertTrue(failMessageDisplayed);
    }

    public void testConfirmationHomeUpGoesToRegister() {
        solo.assertCurrentActivity("correct activity", ConfirmationActivity.class);
        Intents.init();
        solo.clickOnActionBarHomeButton();
        intended(hasComponent(RegisterScreenActivity.class.getName()));
        Intents.release();
    }


}
