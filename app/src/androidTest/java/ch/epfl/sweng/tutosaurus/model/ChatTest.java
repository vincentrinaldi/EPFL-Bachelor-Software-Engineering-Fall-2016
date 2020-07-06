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
public class ChatTest {

    @Test
    public void constructorTest() {
        Chat chat = new Chat();
        assertNotNull(chat);
    }

    @Test
    public void getterSetterTest() {
        Chat chat = new Chat();
        chat.setFullName("chat");
        chat.setUid("2");
        assertEquals(chat.getFullName(), "chat");
        assertEquals(chat.getUid(), "2");
    }
}
