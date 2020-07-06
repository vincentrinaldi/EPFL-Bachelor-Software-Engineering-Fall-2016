package ch.epfl.sweng.tutosaurus.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a meeting between two or more users.
 */
public class Meeting {

    private String id;
    private Date date;
    private String nameLocation;
    private double latitudeLocation;
    private double longitudeLocation;
    private String description;
    private List<String> participants = new ArrayList<>();
    private Course course;
    private boolean rated = false;
    private float rating; //We need the getter for firebase deserialization


    /**
     * Empty constructor for Meeting (required for firebase deserialization)
     */
    public Meeting () {

    }


    /**
     *
     * All the methods of this class are getters and setters used to communicate with firebase
     */

    public void setDate(Date date) {
        this.date = date;
    }


    public void setId(String id){
        this.id = id;
    }


    public void setCourse(Course course) {
        this.course = course;
    }


    public Course getCourse() {
        return this.course;
    }


    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }


    public void addParticipant(String key) {
        this.participants.add(key);
    }


    public void addDescription(String description) { this.description = description; }


    public List<String> getParticipants(){
        return this.participants;
    }


    public String getId() {
        return this.id;
    }


    public Date getDate() {
        return this.date;
    }


    public String getNameLocation() {
        return this.nameLocation;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public double getLatitudeLocation() {
        return latitudeLocation;
    }


    public void setLatitudeLocation(double latitudeLocation) {
        this.latitudeLocation = latitudeLocation;
    }


    public double getLongitudeLocation() {
        return longitudeLocation;
    }


    public void setLongitudeLocation(double longitudeLocation) {
        this.longitudeLocation = longitudeLocation;
    }


    public boolean isRated() {
        return rated;
    }


    public void setRated(boolean rated) {
        this.rated = rated;
    }


    public float getRating() {
        return rating;
    }
}
