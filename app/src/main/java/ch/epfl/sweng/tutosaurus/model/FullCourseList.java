package ch.epfl.sweng.tutosaurus.model;

import java.util.ArrayList;

import ch.epfl.sweng.tutosaurus.R;

/**
 * A class that contain static definitions of the various available courses.
 */
public class FullCourseList {
    private static FullCourseList ourInstance = new FullCourseList();
    private ArrayList<Course> listOfCourses;

    public static FullCourseList getInstance() {
        return ourInstance;
    }

    /**
     * Constructor for the Course class. Set to private to only instantiate through the getters.
     */
    private FullCourseList() {
        ArrayList<Course> listOfCourses = new ArrayList<>(0);
        listOfCourses.add(new Course("mathematics", "Mathematics", R.drawable.school));
        listOfCourses.add(new Course("physics", "Physics", R.drawable.molecule));
        listOfCourses.add(new Course("chemistry", "Chemistry", R.drawable.flask));
        listOfCourses.add(new Course("computer_science", "Computer Science", R.drawable.computer));
        this.listOfCourses = listOfCourses;
    }

    /**
     * Returns all the courses.
      * @return an arraylist with all the courses.
     */
    public ArrayList<Course> getListOfCourses(){
        return listOfCourses;
    }

    /**
     * Returns the course corresponding to the id.
     * @return the course matching the id.
     */
    public Course getCourse(String courseId) {
        for (Course course: listOfCourses) {
            if (course.getId().equals(courseId)) {
                return  course;
            }
        }
        return null;
    }
}
