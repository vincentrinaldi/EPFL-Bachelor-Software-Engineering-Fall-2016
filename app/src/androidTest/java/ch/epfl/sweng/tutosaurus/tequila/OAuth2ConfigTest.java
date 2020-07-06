package ch.epfl.sweng.tutosaurus.tequila;

import org.junit.Test;

import ch.epfl.sweng.tutosaurus.Tequila.OAuth2Config;

public class OAuth2ConfigTest {
    private String[] scopes = new String[0];

    @Test
    public void createEmptyScopesConfigWorks(){
        OAuth2Config config = new OAuth2Config(scopes, "client_id", "client_secret", "redirect");
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingCredentials(){
        OAuth2Config config = new OAuth2Config(scopes, "", "", "redirect");
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingUri(){
        OAuth2Config config = new OAuth2Config(scopes, "client_id", "client_secret", "");
    }
}
