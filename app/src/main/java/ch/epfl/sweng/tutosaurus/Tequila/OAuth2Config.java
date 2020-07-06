package ch.epfl.sweng.tutosaurus.Tequila;

/**
 * Configuration for an OAuth2 client.
 *
 * @author Solal Pirelli
 */
public final class OAuth2Config {
    /**
     * These are the scopes your app needs.
     * Unless you wish to fetch data from other EPFL services,
     * `Tequila.profile` is the only scope you need.
     */
    private final String[] scopes;

    /**
     * This is a public value that simply identifies your app.
     */
    private final String clientId;

    /**
     * This value MUST be kept secret.
     * Your Android app MUST NOT handle it.
     * It MUST NOT appear as a constant in your code, even on the server.
     * It MUST NOT be in any git repository.
     * Otherwise, somebody could impersonate your application.
     */
    private final String clientSecret;

    /**
     * This is the URI that Tequila will redirect to after authenticating your users.
     */
    private final String redirectUri;

    /**
     * Creating the configuartion for the OAuth2 authenfication service for Tequila
     * @param scopes the copes for this config
     * @param clientId the client id of this config
     * @param clientSecret the client secret of this config
     * @param redirectUri the redirect uri of this config
     */
    public OAuth2Config(String[] scopes, String clientId, String clientSecret, String redirectUri) {
        this.scopes = scopes;

        if(clientId.isEmpty() || clientSecret.isEmpty()){
            throw new IllegalArgumentException("Need valid Client credentials for configuring the authentification");
        }else if(redirectUri.isEmpty()){
            throw new IllegalArgumentException("Need redirect back to application uri");
        }else {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
        }
    }

    public String[] getScopes() {
        return scopes;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}