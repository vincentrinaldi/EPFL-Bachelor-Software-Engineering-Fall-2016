package ch.epfl.sweng.tutosaurus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.adapter.CourseAdapter;
import ch.epfl.sweng.tutosaurus.fragment.DatePickerFragment;
import ch.epfl.sweng.tutosaurus.fragment.TimePickerFragment;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.Course;
import ch.epfl.sweng.tutosaurus.model.FullCourseList;
import ch.epfl.sweng.tutosaurus.model.Meeting;
import ch.epfl.sweng.tutosaurus.model.MeetingRequest;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * Activity where the student can create a meeting request
 */
public class CreateMeetingActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;

    private DatabaseHelper dbh = DatabaseHelper.getInstance();

    private String currentUserUid;
    private String teacherId;

    private TimePickerFragment timePicker = new TimePickerFragment();
    private DatePickerFragment datePicker = new DatePickerFragment();

    private Course courseMeeting;

    private Meeting meeting = new Meeting();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            currentUserUid = currentUser.getUid();
        }

        Intent intent = getIntent();
        teacherId = intent.getStringExtra("TEACHER");
        meeting.addParticipant(teacherId);
        meeting.addParticipant(currentUserUid);

        final DatabaseReference ref = dbh.getReference();
        ref.child("user/" + teacherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User matchingTutor = dataSnapshot.getValue(User.class);
                setSubjectList(matchingTutor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        final Button addMeeting = (Button) findViewById(R.id.addMeeting);
        setAddMeetingListener(addMeeting);

    }


    public void showDateTimePickerDialog(View v) throws InterruptedException {
        timePicker.show(getFragmentManager(), "timePicker");
        datePicker.show(getFragmentManager(), "datePicker");

        TextView dateTimeView = (TextView) findViewById(R.id.dateView   );
        dateTimeView.setVisibility(View.VISIBLE);
        String date = datePicker.getDate() + " h " + timePicker.getTime();
        dateTimeView.setText(date);
    }


    public void showLocationPickerDialog(View v) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }


    private void setSubjectList(final User matchingTutor) {
        ArrayList<Course> taughtCourses = new ArrayList<>();
        Map<String, Boolean> teaching = matchingTutor.getTeaching();
        ArrayList<Course> allCourses = FullCourseList.getInstance().getListOfCourses();
        for (String taughtCourseId : teaching.keySet()) {
            if (teaching.get(taughtCourseId)) {
                for (Course course : allCourses) {
                    if (course.getId().equals(taughtCourseId)) {
                        taughtCourses.add(course);
                    }
                }
            }
        }

        final CourseAdapter courseAdapter = new CourseAdapter(this, R.layout.listview_course_row, taughtCourses);
        final ListView courseListView = (ListView) findViewById(R.id.courseListView);
        courseListView.setAdapter(courseAdapter);
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                courseMeeting = courseAdapter.getItemAtPosition(position);
            }
        });
    }


    private void setAddMeetingListener(final Button addMeetingButton){
        addMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText description = (EditText) findViewById(R.id.description);
                meeting.addDescription(description.getText().toString());

                Date dateMeeting = new Date();
                dateMeeting.setMinutes(timePicker.getMeetingMinutes());
                dateMeeting.setHours(timePicker.getMeetingHour());
                dateMeeting.setYear(datePicker.getMeetingYear());
                dateMeeting.setMonth(datePicker.getMeetingMonth());
                dateMeeting.setDate(datePicker.getMeetingDay());

                meeting.setDate(dateMeeting);
                meeting.setCourse(courseMeeting);

                if (dateMeeting.getYear() == -1) {
                    Toast.makeText(getBaseContext(), "Date not selected", Toast.LENGTH_LONG).show();
                } else {
                    if (courseMeeting == null) {
                        Toast.makeText(getBaseContext(), "Course not selected", Toast.LENGTH_LONG).show();
                    } else if (dateMeeting.getYear() == -1) {  //TODO: <= getCurrentDate
                        Toast.makeText(getBaseContext(), "Date not selected", Toast.LENGTH_LONG).show();
                    } else {
                        MeetingRequest request = new MeetingRequest();
                        request.setFrom(currentUserUid);
                        request.setAccepted(false);
                        request.setMeeting(meeting);
                        request.setType("received");
                        dbh.requestMeeting(request, teacherId);

                        Toast.makeText(getBaseContext(), "Meeting requested, wait for confirmation", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), StartActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String placeName = place.getName().toString();

                TextView placeNameView = (TextView) findViewById(R.id.placeName);
                placeNameView.setText(placeName);
                placeNameView.setVisibility(View.VISIBLE);

                meeting.setNameLocation(placeName);
                meeting.setLatitudeLocation(place.getLatLng().latitude);
                meeting.setLongitudeLocation(place.getLatLng().longitude);
            }
        }
    }
}
