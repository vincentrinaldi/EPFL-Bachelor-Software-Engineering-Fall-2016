package ch.epfl.sweng.tutosaurus.Tequila;

import android.text.TextUtils;

/**
 * Client code for Tequila authentication.
 *
 * @author Solal Pirelli
 */
public final class AuthClient {
    public static String createCodeRequestUrl(OAuth2Config config) {
        return "https://tequila.epfl.ch/cgi-bin/OAuth2IdP/auth" +
                "?response_type=code" +
                "&client_id=" + HttpUtils.urlEncode(config.getClientId()) +
                "&redirect_uri=" + HttpUtils.urlEncode(config.getRedirectUri()) +
                "&scope=" + TextUtils.join(",", config.getScopes());
    }

    public static String extractCode(String redirectUri) {
        String marker = "code=";
        return redirectUri.substring(redirectUri.indexOf(marker) + marker.length());
    }
}