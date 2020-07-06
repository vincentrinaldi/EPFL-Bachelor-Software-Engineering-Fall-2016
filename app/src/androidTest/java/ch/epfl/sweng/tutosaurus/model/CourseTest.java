package ch.epfl.sweng.tutosaurus.model;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.model.Course;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CourseTest {
    @Test
    public void testConstructors() {
        Course firstCourse = new Course("mathematics");
        firstCourse.setName("Mathematics");
        firstCourse.setDescription("I'm good at maths");
        assertEquals(firstCourse.getId(), "mathematics");
        assertEquals(firstCourse.getName(), "Mathematics");
        assertEquals(firstCourse.getDescription(), "I'm good at maths");

        Course secondCourse = new Course("physics", "Physics");
        secondCourse.setDescription("I'm good at physics");
        assertEquals(secondCourse.getId(), "physics");
        assertEquals(secondCourse.getName(), "Physics");
        assertEquals(secondCourse.getDescription(), "I'm good at physics");

        Course thirdCourse = new Course("chemistry", "Chemistry", R.drawable.flask);
        thirdCourse.setDescription("I'm good at chemistry");
        assertEquals(thirdCourse.getId(), "chemistry");
        assertEquals(thirdCourse.getName(), "Chemistry");
        assertEquals(thirdCourse.getDescription(), "I'm good at chemistry");
        assertEquals(thirdCourse.getPictureId(), R.drawable.flask);
    }
}