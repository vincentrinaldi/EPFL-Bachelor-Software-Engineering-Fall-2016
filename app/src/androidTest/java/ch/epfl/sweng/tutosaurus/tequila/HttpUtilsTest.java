package ch.epfl.sweng.tutosaurus.tequila;

import org.junit.Test;

import java.io.IOException;

import ch.epfl.sweng.tutosaurus.Tequila.HttpUtils;
import ch.epfl.sweng.tutosaurus.Tequila.Profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpUtilsTest {
    private final String urlSample = "https://www.epfl.ch/";
    private final String urlEncoded = "https%3A%2F%2Fwww.epfl.ch%2F";
    @Test
    public void encodingUrlUtf_8Works(){
        assertThat(HttpUtils.urlEncode(urlSample), is(urlEncoded));
    }

    @Test(expected = AssertionError.class)
    public void testMalformedUrlFetch() throws IOException {
        String url = "invalidUrl";
        HttpUtils httpUtils = new HttpUtils();
        HttpUtils.fetch(url, Profile.class);
    }
}
