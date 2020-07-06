package ch.epfl.sweng.tutosaurus.model;

/**
 * Represents a meeting request.
 */
public class MeetingRequest implements Identifiable{

    private String uid;
    private Meeting meeting;
    private boolean accepted;
    private String type;
    private String from;


    /**
     * Empty constructor for Meeting (required for firebase deserialization)
     */
    public MeetingRequest() {

    }


    public MeetingRequest(String uid, Meeting meeting, boolean accepted, String type, String from) {
        this.uid = uid;
        this.meeting = meeting;
        this.accepted = accepted;
        this.type = type;
        this.from = from;
    }


    @Override
    public String getUid() {
        return this.uid;
    }


    @Override
    public String getFullName() {
        return meeting.getDescription();
    }


    public void setUid(String uid) {
        this.uid = uid;
    }


    public Meeting getMeeting() {
        return meeting;
    }


    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }


    public boolean isAccepted() {
        return accepted;
    }


    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
