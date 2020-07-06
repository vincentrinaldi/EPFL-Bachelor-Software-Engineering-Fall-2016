package ch.epfl.sweng.tutosaurus.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a User of the application.
 */
public class User implements Identifiable {

    private String sciper;
    private String username;
    private String fullName;
    private String email;
    private String uid;
    private int profilePicture;

    private Map<String, Boolean> languages = new HashMap<>();

    private Map<String, Boolean> teaching = new HashMap<>();
    private Map<String, Boolean> studying = new HashMap<>();
    private Map<String, Boolean> speaking = new HashMap<>();
    private Map<String, Boolean> meetings = new HashMap<>();
    private Map<String, String> coursePresentation = new HashMap<>();

    private Map<String, Double> ratings = new HashMap<>(); /* (course id -> globalRating) */
    private Map<String, Integer> totalHoursTaught = new HashMap<>(); /* (course id -> hours taught */

    private float globalRating;
    private int numRatings;

    /**
     * Default constructor (for Firebase database)
     */
    public User(){

    }

    /**
     * Constructor for the User class.
     * @param sciper the sciper number of this user, used as an unique identifier
     */
    public User(String sciper) {
        this.sciper = sciper;
    }

    /**
     * Constructor for the User class.
     * @param sciper the sciper number of this user, used as an unique identifier
     * @param username the username of this user (normally a GASPAR username)
     */
    public User(String sciper, String username) {
        this.sciper = sciper;
        this.username = username;
    }

    /**
     * Set this user's username (for now, this will be their GASPAR username).
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set this user's full name.
     * @param fullName the full name of the user
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Sets this user's email address
     * @param email the email address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets this user's email address
     * @param profilePicture the id of the profile picture
     */
    public void setPicture(int profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    /**
     * Sets this user's teacher globalRating, as a number between 0 and 1 included.
     * @param globalRating the user's globalRating between 0 and 1
     * @throws IllegalArgumentException if the globalRating is not comprised between 0 and 1
     */
    public void setGlobalRating(float globalRating){
        if(globalRating > 5.0 || globalRating < 0) {
            throw new IllegalArgumentException("The globalRating should be between 0 and 5");
        } else {
            this.globalRating = globalRating;
        }
    }

    /**
     * Returns this user's sciper number.
     * @return the sciper number of the user
     */
    public String getSciper() {
        return this.sciper;
    }

    /**
     * Returns this user's username (normally the GASPAR username).
     * @return the username of the user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns this user's full fullName.
     * @return the full fullName of the user
     */
    public String getFullName() {
        return this.fullName;
    }

    /**
     * Returns this user's email address.
     * @return the email address of the user
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns this user's teacher globalRating.
     * @return the globalRating of the user
     */
    public float getGlobalRating() {
        return this.globalRating;
    }

     /**
     * Add a course to the list of courses that this user is prepared to teach.
     * @param courseId the course to add
     */
    public void addTeaching(String courseId) {
        teaching.put(courseId, true);
    }

    /**
     * Add a course to the list of courses that this user wants assistance with.
     * @param courseId the course to add.
     */
    public void addStudying(String courseId) {
        studying.put(courseId, true);
    }

    /**
     * Add a language to the list of this user's spoken languages.
     * @param language the language to add
     */
    public void addLanguage(String language) {
        this.languages.put(language, true);
    }

    /**
     * Returns a map containing this user's spoken languages, mapped to the boolean value true.
     * @return a map of this user's spoken languages
     */
    public Map<String, Boolean> getLanguages() {
        Map<String, Boolean> map = new HashMap<>();
        for(String l : languages.keySet()) {
            map.put(l, true);
        }
        return map;
    }

    /**
     * Returns a map containing the courses that this user has agreed to teach
     * @return a map of courses mapped to the true boolean value
     */
    public Map<String, Boolean> getTeaching() {
        return this.teaching;
    }

    /**
     * Returns a map containing the courses that this user wants help with.
     * @return a map of courses mapped to the true boolean value
     */
    public Map<String, Boolean> getStudying() {
        return this.studying;
    }


    /**
     * Returns a map containing the descriptions of the courses.
     * @return a map of descriptions mapped to the courseId
     */
    public String getCourseDescription(String courseId){
        return coursePresentation.get(courseId);
    }


    public Map<String, String> getCoursePresentation(){
        return coursePresentation;
    }

    /**
     * Increase the number of hours taught in a particular course.
     * @param courseId the unique id of the course
     * @param hours the number of hours by which to increase the number of hours taught
     */
    public void addHoursTaught(String courseId, int hours) {
        if(!totalHoursTaught.containsKey(courseId)) {
            totalHoursTaught.put(courseId, 0);
        }
        totalHoursTaught.put(courseId, totalHoursTaught.get(courseId) + hours);
    }

    /**
     * Set the globalRating for a particular course.
     * @param courseId the unique id of the §course
     * @param rating the globalRating for this course
     */
    public void setCourseRating(String courseId, double rating) {
        if(rating > 1.0 || rating < 0) {
            throw new IllegalArgumentException("The globalRating should be between 0 and 1");
        } else {
            ratings.put(courseId, rating);
        }
    }

    /**
     * Get the number of hours taught in a particular course
     * @param courseId the unique id of the course
     * @return the number of hours taught
     */
    public int getHoursTaught(String courseId) {
        if(totalHoursTaught.containsKey(courseId)) {
            return totalHoursTaught.get(courseId);
        } else {
            return  0;
        }
    }

    /**
     * Returns this user's email address.
     * @return the id of the profile picture
     */
    public int getPicture() {
        return this.profilePicture;
    }

    /**
     * Get the globalRating for a particular course
     * @param courseId the unique id of the course
     * @return the globalRating for this course
     */
    public double getCourseRating(String courseId) {
        if(ratings.containsKey(courseId)) {
            return ratings.get(courseId);
        }
        return 0.0;
    }


    /**
     * Returns the name automatically (to be used in the listview)
     * @return the full name
     */

    public String toString(){
        return this.fullName;
    }

    public boolean isTeacher(String courseId) {
        if(teaching.containsKey(courseId)){
            return teaching.get(courseId);
        } else {
            return false;
        }
    }


    public void setSpeaking(Map<String, Boolean> speaking) {
        this.speaking = speaking;
    }


    public void setMeetings(Map<String, Boolean> meetings) {
        this.meetings = meetings;
    }


    public void setCoursePresentation(Map<String, String> coursePresentation) {this.coursePresentation = coursePresentation;}


    public int getNumRatings() {
        return numRatings;
    }


    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }
}
