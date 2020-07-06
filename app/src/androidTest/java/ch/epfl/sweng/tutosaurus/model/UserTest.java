package ch.epfl.sweng.tutosaurus.model;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by albertochiappa on 17/12/16.
 */

@RunWith(AndroidJUnit4.class)
public class UserTest {
    User user;
    @Test
    public void canConstructWithSciperAndName(){
        user = new User("000000", "Einstein");
        assertEquals("000000", user.getSciper());
        assertEquals("Einstein", user.getUsername());
    }

    @Test
    public void canAddLanguage(){
        user = new User();
        user.addLanguage("english");
        Map<String, Boolean> taughtLanguages = user.getLanguages();
        assertEquals(true, (boolean) taughtLanguages.get("english"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void doesntAcceptMeaninglessRatingsForCourse(){
        user = new User();
        user.addLanguage("english");
        user.setCourseRating("english", 10000f);
    }

    @Test (expected = IllegalArgumentException.class)
    public void doesntAcceptMeaninglessRatingsForGlobalRating(){
        user = new User();
        user.setGlobalRating(10000f);
    }

    @Test
    public void canConvertUserToString(){
        user = new User("000000", "Einstein");
        user.setFullName("Albert Einstein");
        String userToString = user.toString();
        assertEquals(userToString, "Albert Einstein");
    }

    @Test
    public void canGetCoursePresentationAsHashMap(){
        user = new User("000000", "Einstein");
        assertEquals(HashMap.class, user.getCoursePresentation().getClass());
    }
}
