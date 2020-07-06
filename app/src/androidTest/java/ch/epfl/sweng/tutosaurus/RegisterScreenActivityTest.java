package ch.epfl.sweng.tutosaurus;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import ch.epfl.sweng.tutosaurus.Tequila.AuthClient;
import ch.epfl.sweng.tutosaurus.Tequila.AuthServer;
import ch.epfl.sweng.tutosaurus.Tequila.OAuth2Config;
import ch.epfl.sweng.tutosaurus.Tequila.Profile;
import ch.epfl.sweng.tutosaurus.activity.MainActivity;
import ch.epfl.sweng.tutosaurus.activity.RegisterScreenActivity;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegisterScreenActivityTest {
    private Solo solo;

    private static final String CLIENT_ID = "12345@epfl.ch";
    private static final String CLIENT_KEY = "12345abc";
    private static final String REDIRECT_URI = "tutosaurus://login";

    @Rule
    public ActivityTestRule<RegisterScreenActivity> mActivityRule = new ActivityTestRule<>(
            RegisterScreenActivity.class);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                mActivityRule.getActivity());
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @Test
    public void ClickOnRegisterPopupTequilaLoginWebpage(){
        solo.assertCurrentActivity("correct activity", RegisterScreenActivity.class);
        solo.typeText(0, "albert");
        solo.typeText(1, "notThePassword");
        solo.clickOnView(solo.getView(R.id.registerLogin));
        //boolean tequilaWebPage = solo.searchText("tequila.epfl.ch");
        //assertTrue(tequilaWebPage);
        boolean teqWebPage = solo.searchText("Sign in");
        assertTrue(teqWebPage);
    }

    @Test
    public void testRegisterHomeUpGoesToMain() {
        solo.assertCurrentActivity("correct activity", RegisterScreenActivity.class);
        Intents.init();
        solo.clickOnActionBarHomeButton();
        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
    }

    @Test(expected = IOException.class)
    public void testFetchBadTokensThrowsException()throws IOException{
        OAuth2Config config = new OAuth2Config(new String[]{"Tequila.profile"}, CLIENT_ID, CLIENT_KEY, REDIRECT_URI);
        String code = "nonsense";
        AuthServer authServer = new AuthServer();
        Map<String, String> tokens = AuthServer.fetchTokens(config, code);
    }

    @Test(expected = IOException.class)
    public void testFetchProfileWithBadAcessToken() throws IOException{
        String invalid_token = "Bearer jd8ff5edaa22386b785ced275b13d625e4e14c07";
        AuthServer authServer = new AuthServer();
        Profile profile = AuthServer.fetchProfile(invalid_token);
    }

    @Test
    public void testAuthClientCodeExtract() {
        String uri = "url&code=1234";
        AuthClient authClient = new AuthClient();
        String codeExtract = AuthClient.extractCode(uri);
        assertEquals(codeExtract, "1234");
    }

}
