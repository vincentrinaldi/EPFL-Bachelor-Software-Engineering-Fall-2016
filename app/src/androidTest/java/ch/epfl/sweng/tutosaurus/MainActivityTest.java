package ch.epfl.sweng.tutosaurus;

import android.net.wifi.WifiManager;
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

import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;
import ch.epfl.sweng.tutosaurus.activity.MainActivity;
import ch.epfl.sweng.tutosaurus.activity.RegisterScreenActivity;
import ch.epfl.sweng.tutosaurus.activity.ResetPasswordActivity;
import ch.epfl.sweng.tutosaurus.network.NetworkChangeReceiver;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private WifiManager wifi;
    private Solo solo;
    private NetworkChangeReceiver receiver;
    private String valid_email;
    private String valid_password;
    private String invalid_email;
    private String nonsense;


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                mActivityRule.getActivity());
        /**wifi = (WifiManager) mActivityRule.getActivity().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);*/
        valid_email = "albert.einstein@epfl.ch";
        valid_password = "tototo";
        invalid_email = "HollyMolly@wow.com";
        nonsense = "blabla";

    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        //wifi.setWifiEnabled(true);
    }

    @Test
    public void forgotPasswordButtonGoesToCorrectActivity() {
        solo.assertCurrentActivity("correct activity", MainActivity.class);
        Intents.init();
        solo.clickOnView(solo.getView(R.id.forgotPasswordButton));
        intended(hasComponent(ResetPasswordActivity.class.getName()));
        Intents.release();

    }

    @Test
    public void signUpButtonGoesToCorrectActivity() {
        solo.assertCurrentActivity("correct activity", MainActivity.class);
        Intents.init();
        solo.clickOnView(solo.getView(R.id.registerButton));
        intended(hasComponent(RegisterScreenActivity.class.getName()));
        Intents.release();

    }

    @Test
    public void LogInWithoutAnyInputDisplaysWarning() throws InterruptedException {
        solo.assertCurrentActivity("correct activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.connectionButton));
        boolean warningDisplayed = waitForToastWithText("Please type in your email and password");
        assertTrue(warningDisplayed);
    }

    @Test
    public void LogInWithoutPasswordDisplaysWarning() throws InterruptedException {
        solo.assertCurrentActivity("correct activity", MainActivity.class);
        solo.typeText(0, valid_email);
        solo.clickOnView(solo.getView(R.id.connectionButton));
        boolean warningDisplayed = waitForToastWithText("Please type in your email and password");
        assertTrue(warningDisplayed);
    }

    @Test
    public void LogInWithoutEmailDisplaysWarning() throws InterruptedException {
        solo.assertCurrentActivity("correct activity", MainActivity.class);
        solo.typeText(1, valid_password);
        solo.clickOnView(solo.getView(R.id.connectionButton));
        boolean warningDisplayed = waitForToastWithText("Please type in your email and password");
        assertTrue(warningDisplayed);
    }

    @Test
    public void logInWithInvalidCredentialsShouldDisplayFail() throws InterruptedException {
        solo.assertCurrentActivity("correct activity", MainActivity.class);
        solo.typeText(0, invalid_email);
        solo.typeText(1, nonsense);
        solo.clickOnView(solo.getView(R.id.connectionButton));
        boolean warningDisplayed = waitForToastWithText("Login failed");
        assertTrue(warningDisplayed);
    }

    @Test
    public void loginInWithValidGoesToHome(){
        solo.assertCurrentActivity("correct activity", MainActivity.class);
        solo.typeText(0, valid_email);
        solo.typeText(1, valid_password);
        Intents.init();
        solo.clickOnView(solo.getView(R.id.connectionButton));
        intended(hasComponent(HomeScreenActivity.class.getName()));
        Intents.release();
    }

    private boolean waitForToastWithText(String toastText) throws InterruptedException {
        boolean toastFound = solo.searchText(toastText);
        int numEfforts = 0;
        while(toastFound == false && numEfforts < 5000){
            Thread.sleep(1);
            toastFound = solo.searchText(toastText);
            numEfforts++;
        }
        return toastFound;
    }

}
