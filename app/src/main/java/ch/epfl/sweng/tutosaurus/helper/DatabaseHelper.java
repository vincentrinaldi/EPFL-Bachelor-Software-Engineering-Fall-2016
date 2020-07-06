package ch.epfl.sweng.tutosaurus.helper;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import ch.epfl.sweng.tutosaurus.model.Chat;
import ch.epfl.sweng.tutosaurus.model.Meeting;
import ch.epfl.sweng.tutosaurus.model.MeetingRequest;
import ch.epfl.sweng.tutosaurus.model.Message;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * Utility class with various methods to access and write to the Firebase database. Singleton.
 */
public class DatabaseHelper {

    private final static String TAG = "DatabaseHelper";

    private static final String MEETING_PATH = "meeting/";
    public static final String USER_PATH = "user/";
    private static final String COURSE_PATH = "course/";
    public static final String MEETING_REQUEST_PATH = "meetingRequests";
    private static final String MEETING_PER_USER_PATH = "meetingsPerUser/";

    private DatabaseReference dbf;

    private static DatabaseHelper instance = null;

    /**
     * Private constructor for DatabaseHelper
     */
    private DatabaseHelper(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.setPersistenceEnabled(true);
        dbf = db.getReference();
    }

    /**
     * Getter for DatabaseHelper
     * @return an instance of DatabaseHelper
     */
    public static DatabaseHelper getInstance() {
        if(instance == null){
            instance = new DatabaseHelper();
        }
        return instance;
    }

    /**
     * Getter for the reference of the database
     * @return DatabaseReference to the database
     */
    public DatabaseReference getReference(){
        return dbf;
    }

    /**
     * Register a user in the database
     * @param  user the user to be registered
     */
    public void signUp(User user) {
        DatabaseReference ref = dbf.child(USER_PATH + user.getUid());
        ref.setValue(user);
    }

    /**
     * Adds a teaching language to a user
     * @param  userId id of the user
     * @param languageId id of the language
     */
    public void addLanguageToUser(String userId, String languageId) {
        DatabaseReference userSpeakLanguageRef = dbf.child(USER_PATH + userId + "/speaking/" + languageId);
        userSpeakLanguageRef.setValue(true);
    }

    /**
     * Removes a teaching language from a user
     * @param  userId id of the user
     * @param languageId id of the language
     */
    public void removeLanguageFromUser(String userId, String languageId) {
        DatabaseReference userSpeakLanguageRef = dbf.child(USER_PATH + userId + "/speaking/" + languageId);
        userSpeakLanguageRef.setValue(false);
    }

    /**
     * Sets the global rating of a user
     * @param  userId id of the user
     * @param globalRating the new rating
     */
    public void setRating(String userId, float globalRating) {
        DatabaseReference userRatingRef = dbf.child(USER_PATH + userId + "/globalRating/");
        userRatingRef.setValue(globalRating);
    }

    /**
     * Setter for the total number of ratings
     * @param  userId id of the user
     * @param numRatings total number of ratings
     */
    public void setNumRatings(String userId, int numRatings) {
        DatabaseReference userNumRatingRef = dbf.child(USER_PATH + userId + "/numRatings/");
        userNumRatingRef.setValue(numRatings);
    }

    /**
     * Add a taught course to a teacher and a teacher to the list of course's teachers
     * @param  userId id of the user
     * @param courseId id of the course
     */
    public void addTeacherToCourse(String userId, String courseId) {
        DatabaseReference courseRef = dbf.child(COURSE_PATH + courseId + "/teaching/" + userId);
        DatabaseReference userTeachCourseRef = dbf.child(USER_PATH + userId + "/teaching/" + courseId);
        userTeachCourseRef.setValue(true);
        courseRef.setValue(true);
    }

    /**
     * Removes a taught course from a teacher and a teacher from the list of course's teachers
     * @param  userId id of the user
     * @param courseId id of the course
     */
    public void removeTeacherFromCourse(String userId, String courseId) {
        DatabaseReference courseRef = dbf.child(COURSE_PATH + courseId + "/teaching/" + userId);
        DatabaseReference userTeachCourseRef = dbf.child(USER_PATH + userId + "/teaching/" + courseId);
        userTeachCourseRef.setValue(false);
        courseRef.setValue(false);
    }

    /**
     * Adds a meeting in the database
     * @param meeting the meeting to be added
     */
    private String addMeeting(Meeting meeting) {
        String key = dbf.child(MEETING_PATH).push().getKey();
        meeting.setId(key);
        DatabaseReference meetingRef = dbf.child(MEETING_PATH).child(key);
        DatabaseReference userRef = dbf.child(USER_PATH);
        DatabaseReference meetingsPerUserRef = dbf.child(MEETING_PER_USER_PATH);
        for (String userKey: meeting.getParticipants()) {
            DatabaseReference userMeetingsRef = userRef.child(userKey + "/meetings/" + meeting.getId());
            DatabaseReference meetingsPerUserUserRef = meetingsPerUserRef.child(userKey + "/" + meeting.getId());
            userMeetingsRef.setValue(true);
            meetingsPerUserUserRef.setValue(meeting);
        }
        meetingRef.setValue(meeting);
        if (meeting.getCourse() != null) {
            DatabaseReference courseMeetingRef = dbf.child(COURSE_PATH + meeting.getCourse().getId() + "/meeting/" + meeting.getId());
            courseMeetingRef.setValue(true);
        }
        return key;
    }

    /**
     * Adds a meeting request in the database
     * @param request request of meeting to be added
     * @param teacher id of the teacher to ask a meeting to
     */
    public String requestMeeting(MeetingRequest request, String teacher) {
        String key = dbf.child(MEETING_REQUEST_PATH).child(request.getFrom()).push().getKey();
        request.setUid(key);
        DatabaseReference requestRef = dbf.child(MEETING_REQUEST_PATH).child(teacher).child(key);
        requestRef.setValue(request);
        return key; // return the key of this request
    }

    /**
     * Sends a message from a user to another one
     * @param fromUid id of the sender
     * @param fromFullName full name of the sender
     * @param toUid id of the receiver
     * @param toFullName full name of the receiver
     * @param content content of the message
     */
    public void sendMessage(String fromUid, String fromFullName, String toUid, String toFullName, String content){
        DatabaseReference chatIdFromRef = dbf.child("chats/" + fromUid);
        DatabaseReference chatIdToRef = dbf.child("chats/" + toUid);

        Chat fromChat = new Chat(toUid);
        fromChat.setFullName(toFullName);
        Chat toChat = new Chat(fromUid);
        toChat.setFullName(fromFullName);
        chatIdFromRef.child(toUid).setValue(fromChat);
        chatIdToRef.child(fromUid).setValue(toChat);

        DatabaseReference messageFromRef = dbf.child("messages/" + fromUid + "/" + toUid);
        DatabaseReference messageToRef = dbf.child("messages/" + toUid + "/" + fromUid);

        String key = messageFromRef.push().getKey();

        long timestamp = (new Date()).getTime();
        Message message = new Message(fromUid, content, timestamp);
        messageFromRef.child(key).setValue(message);
        messageToRef.child(key).setValue(message);
    }

    /**
     * Adds the description to a user's subject
     * @param description presentation of the subject
     * @param userId id of the user to add a description to
     * @param courseId id of the course to add a description
     */
    public void addSubjectDescription(String description, String userId, String courseId){
        DatabaseReference userLearnCourseRef = dbf.child(USER_PATH + userId + "/coursePresentation/" + courseId);
        userLearnCourseRef.setValue(description);
    }

    /**
     * Returns a reference to the user's meeting in the database
     * @param key the id of the user
     */
    public DatabaseReference getMeetingsRefForUser(String key) {
        return dbf.child(MEETING_PER_USER_PATH + key);
    }

    /**
     * @return  a reference to all the users in the database
     */
    public DatabaseReference getUserRef() {
        return dbf.child(USER_PATH);
    }

    /**
     * @return  a reference to all the umeeting requests
     */
    public DatabaseReference getMeetingRequestsRef() {
        return dbf.child(MEETING_REQUEST_PATH);
    }

    /**
     * Confirms a requested meeting
     * @param currentUserUid id of the user accepting the meeting
     * @return  the id of the confirmed meeting
     */
    public String confirmMeeting(String currentUserUid, MeetingRequest request) {
        String meetingId;
        DatabaseReference meetingRequestRef = dbf.child(MEETING_REQUEST_PATH).child(currentUserUid).child(request.getUid());
        Meeting meeting = request.getMeeting();
        meetingId = addMeeting(meeting);
        Log.d(TAG, "meeting added: " + meeting.getId());
        meetingRequestRef.removeValue();
        return meetingId;
    }

    /**
     * Rates a past meeting
     * @param userId id of the rated user
     * @param meetingId id of the meeting being rated
     * @param rating rating for the meeting
     */
    public void setMeetingRated(String userId, String meetingId, float rating) {
        DatabaseReference meetingsPerUserCurrentUserRef = dbf.child(MEETING_PER_USER_PATH + userId +"/" + meetingId + "/rated/");
        meetingsPerUserCurrentUserRef.setValue(true);

        DatabaseReference meetingsPerUserCurrentUserRefRating = dbf.child(MEETING_PER_USER_PATH + userId +"/" + meetingId + "/rating/");
        meetingsPerUserCurrentUserRefRating.setValue(rating);

    }
}