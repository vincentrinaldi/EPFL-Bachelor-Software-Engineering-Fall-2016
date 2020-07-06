package ch.epfl.sweng.tutosaurus.model;

/**
 * Represents a message sent from one user to another.
 */
public class Message {

    private String from;
    private String content;
    private long timestamp;

    public Message(){

    }

    public Message(String from, String content, long timestamp) {
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
