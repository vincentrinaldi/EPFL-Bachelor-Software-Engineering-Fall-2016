package ch.epfl.sweng.tutosaurus.model;

/**
 * Represents a course
 */
public class Course {

    private String name;
    private String id;
    private int pictureId;
    private String description;
    /**
     * Constructor with no arguments for Course (required for firebase deserialization)
     */
    public Course() {

    }

    /**
     * Constructor for the Course class.
     * @param id the unique id of this course
     */
    public Course(String id) {
        this.id = id;
    }

    /**
     * Constructor for the Course class.
     * @param id the unique id of this course
     * @param name the name of this course
     */
    public Course(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor for the Course class.
     * @param id the unique id of this course
     * @param name the name of this course
     * @param pictureId the id of the symbol of the course
     */
    public Course(String id, String name, int pictureId) {
        this.id = id;
        this.name = name;
        this.pictureId=pictureId;
    }
    /**
     * Sets this course's name.
     *
     * @param name the name of the course
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns this course's unique id.
     * @return the unique id for this course
     */
    public String getId(){
        return this.id;
    }

    /**
     * Returns this course's name.
     *
     * @return the name of the course
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this course's picture.
     *
     * @return the name of the course
     */
    public int getPictureId() {
        return this.pictureId;
    }

    /**
     * Returns this course's description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * Sets this course's description.
     *
     */
    public void setDescription(String description) {
        this.description=description;
    }
}
