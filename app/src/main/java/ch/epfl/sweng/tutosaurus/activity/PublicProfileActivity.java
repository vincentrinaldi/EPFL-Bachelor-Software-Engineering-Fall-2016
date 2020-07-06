package ch.epfl.sweng.tutosaurus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.Course;
import ch.epfl.sweng.tutosaurus.model.FullCourseList;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * Activity that displays the profile of the current user.
 */
public class PublicProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbh = DatabaseHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String userId = intent.getStringExtra("USER_ID");

        final Button createMeeting = (Button) findViewById(R.id.createMeetingButton);
        createMeeting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent createMeetingIntent = new Intent(getBaseContext(), CreateMeetingActivity.class);
                createMeetingIntent.putExtra("TEACHER", userId);
                startActivity(createMeetingIntent);
            }
        });


        DatabaseReference ref = dbh.getReference();
        ref.child("user/" + userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User matchingTutor = dataSnapshot.getValue(User.class);

                // Set profile name
                TextView profileName= (TextView) findViewById(R.id.profileName);
                profileName.setText(matchingTutor.getFullName());

                // Set profile picture
                final ImageView profilePicture=(ImageView) findViewById(R.id.publicProfilePicture);
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://tutosaurus-16fce.appspot.com");
                StorageReference picRef = storageRef.child("profilePictures").child(matchingTutor.getUid()+".png");
                Glide.with(getBaseContext())
                        .using(new FirebaseImageLoader())
                        .load(picRef)
                        /* Glide uses the hash of the path to determine cache invalidation. There is no easy way to determine
                        * if a file with the same path has changed. A workaround is to define a signature that is always
                        * different so that Glide fetches the data each time.
                        * .signature(new StringSignature(String.valueOf(System.currentTimeMillis()))) */
                        .into(profilePicture);


                // Set email
                TextView email = (TextView) findViewById(R.id.emailView);
                email.setText(matchingTutor.getEmail());

                // Set the ratings
                RatingBar professorRate=(RatingBar) findViewById(R.id.ratingBarProfessor);
                professorRate.setRating(matchingTutor.getGlobalRating());
                professorRate.setVisibility(View.VISIBLE);
                TextView professorView = (TextView) findViewById(R.id.professorView);
                professorView.setVisibility(View.VISIBLE);

                // Set "expert in" listview
                LinearLayout courseList = (LinearLayout) findViewById(R.id.courseListLayout);
                courseList.removeAllViews();
                setSubjectButtons(matchingTutor);

                // Set the floating button to send an email
                FloatingActionButton sendEmailButton = (FloatingActionButton) findViewById(R.id.fab);
                sendEmailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchingTutor.getEmail()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Hi! I'm looking for a tutor");
                        startActivity(Intent.createChooser(intent, ""));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    private void setSubjectButtons(final User matchingTutor){

        // Find the taught courses
        ArrayList<Course> taughtCourses = new ArrayList<>();
        Map<String, Boolean> teaching = matchingTutor.getTeaching();
        ArrayList<Course> allCourses = FullCourseList.getInstance().getListOfCourses();
        for(String taughtCourseId : teaching.keySet()){
            if(teaching.get(taughtCourseId)){
                for(Course course : allCourses){
                    if(course.getId().equals(taughtCourseId)){
                        taughtCourses.add(course);
                    }
                }
            }
        }

        // Fills the linear layout
        LinearLayout courseLayout = (LinearLayout) findViewById(R.id.courseListLayout);
        for(Course course : taughtCourses ) {
            View courseRow = getLayoutInflater().inflate(R.layout.listview_descripted_course_row, null);
            TextView courseName = (TextView) courseRow.findViewById(R.id.courseName);
            courseName.setText(course.getName());
            ImageView coursePicture = (ImageView) courseRow.findViewById(R.id.coursePicture);
            coursePicture.setImageResource(course.getPictureId());
            TextView courseDescriptionView = (TextView) courseRow.findViewById(R.id.courseDescription);
            String courseDescriprion = matchingTutor.getCourseDescription(course.getId());

            if(courseDescriprion!=null){
                courseDescriptionView.setText(matchingTutor.getCourseDescription(course.getId()));
                setOpenDescriptionListener(courseRow);
            }
            courseLayout.addView(courseRow);
        }
    }

    static private void openDescription(final View row){
        row.findViewById(R.id.courseDescription).setVisibility(View.VISIBLE);
        setCloseDescriptionListener(row);
    }

    static private void closeDescription(final View row){
        row.findViewById(R.id.courseDescription).setVisibility(View.GONE);
        setOpenDescriptionListener(row);
    }

    static private void setCloseDescriptionListener(final View row) {
        final View.OnClickListener closeDescriptionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDescription(row);
            }
        };
        row.findViewById(R.id.courseName).setOnClickListener(closeDescriptionListener);
        row.findViewById(R.id.coursePicture).setOnClickListener(closeDescriptionListener);
    }

    static private void setOpenDescriptionListener(final View row) {
        final View.OnClickListener openDescriptionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDescription(row);
            }
        };
        row.findViewById(R.id.courseName).setOnClickListener(openDescriptionListener);
        row.findViewById(R.id.coursePicture).setOnClickListener(openDescriptionListener);
    }

}
