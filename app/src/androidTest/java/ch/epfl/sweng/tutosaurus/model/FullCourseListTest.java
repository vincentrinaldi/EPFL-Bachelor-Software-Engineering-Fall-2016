package ch.epfl.sweng.tutosaurus.model;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.tutosaurus.model.FullCourseList;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class FullCourseListTest {

    @Test
    public void isSingular() {
        FullCourseList firstList = FullCourseList.getInstance();
        FullCourseList secondList = FullCourseList.getInstance();
        assertEquals(firstList, secondList );
    }

    @Test
    public void returnsCorrectCourse(){
        FullCourseList courseList = FullCourseList.getInstance();
        Course returnedCourse = courseList.getCourse("mathematics");
        assertEquals("Mathematics", returnedCourse.getName());
    }

}
