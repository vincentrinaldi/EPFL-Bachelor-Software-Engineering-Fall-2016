package ch.epfl.sweng.tutosaurus.model;

/**
 * Represents a chat between two users.
 */
public class Chat implements Identifiable {

    private String fullName;
    private String uid;

    public Chat() {

    }

    /**
     * A public constructor for a Chat.
     * @param uid the uid of the other user
     */
    public Chat(String uid){
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
