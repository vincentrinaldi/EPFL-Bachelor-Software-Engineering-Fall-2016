package ch.epfl.sweng.tutosaurus.model;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Created by albertochiappa on 17/12/16.
 */

@RunWith(AndroidJUnit4.class)
public class MeetingModelTest {
    Meeting meeting = new Meeting();

    @Test
    public void canConstructMeetingrequest(){
        MeetingRequest meetingRequest = new MeetingRequest("uid", meeting, false, "noType", "Einstein");
        assertEquals("uid", meetingRequest.getUid());
        assertEquals(meeting, meetingRequest.getMeeting());
        assertEquals("noType", meetingRequest.getType());
        assertEquals("Einstein", meetingRequest.getFrom());
    }

    @Test
    public void canSetMeetingLocation(){
        meeting.setNameLocation("EPFL");
        assertEquals("EPFL", meeting.getNameLocation());
    }
}
