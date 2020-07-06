package ch.epfl.sweng.tutosaurus.model;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by samuel on 02.12.16.
 */


@RunWith(AndroidJUnit4.class)
public class MessageTest {

    @Test
    public void testDefaultConstructor() {
        Message message = new Message();
        assertNotNull(message);
    }

    @Test
    public void testConstructor() {
        Message message = new Message("Albert", "E=mc2", (long)(1.0));
        assertEquals(message.getContent(), "E=mc2");
        assertEquals(message.getFrom(), "Albert");
        assertEquals(message.getTimestamp(), (long)(1.0));
    }

    @Test
    public void testSetter() {
        Message message = new Message();
        message.setFrom("Albert");
        message.setContent("E=mc2");
        message.setTimestamp((long)(1.0));
        assertEquals(message.getContent(), "E=mc2");
        assertEquals(message.getFrom(), "Albert");
        assertEquals(message.getTimestamp(), (long)(1.0));
    }

}
