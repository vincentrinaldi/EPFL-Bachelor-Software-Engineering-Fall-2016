package ch.epfl.sweng.tutosaurus.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.tutosaurus.activity.LocationActivity;
import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.Course;
import ch.epfl.sweng.tutosaurus.model.FullCourseList;
import ch.epfl.sweng.tutosaurus.model.Meeting;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * Firebase adapter used to populate the list of meetings of the user in MeetingsFragment
 */

public class MeetingAdapter extends FirebaseListAdapter<Meeting>{

    private static final long DIFFERENCE_TIME_JAVA = 59958144000000L; //discrepancy with Firebase date
    private static final long DIFFERENCE_TIME_CALENDAR = 171398140000L; //discrepancy with the calendar
    private String currentUserUid;
    private DatabaseHelper dbh = DatabaseHelper.getInstance();
    private float meetingRating;
    private User user;


    public MeetingAdapter(Activity activity, java.lang.Class<Meeting> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            currentUserUid = currentUser.getUid();
        }
    }


    /**
     * Method that fill the list of meeting
     *
     * @param mainView view of the fragment where the adapter is used
     * @param meeting
     * @param position position in the list
     */
    @Override
    protected void populateView(View mainView, final Meeting meeting, int position) {

        TextView subject = (TextView) mainView.findViewById(R.id.courseName);
        populateCourse(mainView, meeting, subject);

        TextView otherParticipantView = (TextView) mainView.findViewById(R.id.otherParticipantMeeting);
        populateParticipants(meeting, otherParticipantView);

        TextView date = (TextView) mainView.findViewById(R.id.dateMeeting);
        populateDateMeeting(meeting, date);

        TextView descriptionMeeting = (TextView) mainView.findViewById(R.id.descriptionMeeting);
        populateDescriptionMeeting(meeting, descriptionMeeting);

        TextView locationMeeting = (TextView) mainView.findViewById(R.id.locationMeeting);
        locationMeeting.setText(meeting.getNameLocation());

        double latitudeMeeting = meeting.getLatitudeLocation();
        double longitudeMeeting = meeting.getLongitudeLocation();
        Button showLocationMeeting = (Button) mainView.findViewById(R.id.showLocationMeeting);
        showLocationMeeting(mainView, meeting, latitudeMeeting, longitudeMeeting, showLocationMeeting);

        Button detailsMeeting = (Button) mainView.findViewById(R.id.showDetailsMeeting);
        populateDetailsMeeting(mainView, meeting, detailsMeeting);

        Button syncCalendar = (Button) mainView.findViewById(R.id.syncCalendar);
        populateSyncCalendar(mainView, meeting, syncCalendar);
    }


    private void populateSyncCalendar(final View mainView, final Meeting meeting, Button syncCalendar) {
        syncCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(meeting.getDate());
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, "Switzerland/Lausanne");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis() + DIFFERENCE_TIME_CALENDAR);
                intent.putExtra(CalendarContract.Events.TITLE, meeting.getCourse().getName());
                if (user != null) {
                    intent.putExtra(Intent.EXTRA_EMAIL, user.getEmail());
                }

                if (meeting.getDescription() != null) {
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, meeting.getDescription());
                }

                if (meeting.getNameLocation() != null) {
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, meeting.getNameLocation());
                }

                mainView.getContext().startActivity(intent);
            }
        });
    }


    private void populateDetailsMeeting(final View mainView, final Meeting meeting, Button detailsMeeting) {
        final RatingBar ratingBar = (RatingBar) mainView.findViewById(R.id.ratingBar);
        if(meeting.getDate().getTime() > new Date().getTime() + DIFFERENCE_TIME_JAVA) {
            showDetailsFutureMeetings(detailsMeeting, mainView, ratingBar);
        }
        else if(meeting.isRated()) {
            showRatingRatedMeeting(meeting, detailsMeeting, ratingBar);
        }
        else {
            showToBeRatedMeeting(meeting, detailsMeeting, mainView, ratingBar);
        }
    }


    private void showToBeRatedMeeting(final Meeting meeting, Button detailsMeeting, final View currentRow, RatingBar ratingBar) {
        ratingBar.setVisibility(View.GONE);
        detailsMeeting.setVisibility(View.VISIBLE);
        detailsMeeting.setText(R.string.rate);
        detailsMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder ratingDialog = new AlertDialog.Builder(currentRow.getContext());
                final RatingBar rating = new RatingBar(currentRow.getContext());
                rating.setNumStars(5);
                rating.setStepSize(1.0f);
                rating.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT));
                LinearLayout parent = new LinearLayout(currentRow.getContext());
                parent.setGravity(Gravity.CENTER);
                parent.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT));
                parent.addView(rating);

                ratingDialog.setTitle("Rate this meeting");
                ratingDialog.setView(parent);

                ratingDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                meetingRating = rating.getRating();
                                meeting.setRated(true);
                                if (user != null) {
                                    dbh.setMeetingRated(currentUserUid, meeting.getId(), meetingRating);
                                    int numRatings = user.getNumRatings();
                                    float globalRating = user.getGlobalRating();
                                    globalRating = (globalRating * numRatings + meetingRating) / (numRatings + 1);
                                    dbh.setRating(user.getUid(), globalRating);
                                    dbh.setNumRatings(user.getUid(), numRatings + 1);
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                ratingDialog.create();
                ratingDialog.show();
            }
        });
    }


    private void showRatingRatedMeeting(Meeting meeting, Button detailsMeeting, RatingBar ratingBar) {
        detailsMeeting.setVisibility(View.GONE);
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(meeting.getRating());
    }


    private void showDetailsFutureMeetings(Button detailsMeeting, final View currentRow, RatingBar ratingBar) {
        ratingBar.setVisibility(View.GONE);
        detailsMeeting.setVisibility(View.VISIBLE);
        detailsMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout detailsLayout = (LinearLayout) currentRow.findViewById(R.id.detailsMeeting);
                if (detailsLayout.getVisibility() == View.VISIBLE) {
                    detailsLayout.setVisibility(View.GONE);
                } else {
                    detailsLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void showLocationMeeting(final View mainView, final Meeting meeting,
                                     final double latitudeMeeting, final double longitudeMeeting,
                                     Button showLocationMeeting) {

        showLocationMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (meeting.getNameLocation() == null) {
                    Toast.makeText(mainView.getContext(), "Place not selected", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(mainView.getContext(), LocationActivity.class);
                intent.putExtra("latitudeMeeting", latitudeMeeting);
                intent.putExtra("longitudeMeeting", longitudeMeeting);
                view.getContext().startActivity(intent);
            }
        });

    }


    private void populateDescriptionMeeting(final Meeting meeting, TextView descriptionMeeting) {
        if (meeting.getDescription() != null) {
            descriptionMeeting.setText(meeting.getDescription());
        }
    }


    private void populateDateMeeting(final Meeting meeting, TextView date) {
        if (meeting.getDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, HH:mm", Locale.ENGLISH);
            String dateNewFormat = dateFormat.format(meeting.getDate());
            date.setText(dateNewFormat);
        }
    }


    private void populateParticipants(final Meeting meeting, final TextView otherParticipantView) {
        Query ref = dbh.getUserRef();
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> participants = meeting.getParticipants();
                String displayParticipant = "";
                for (String participant: participants) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if (!userSnapshot.getKey().equals(currentUserUid) && userSnapshot.getKey().equals(participant)) {
                            user = userSnapshot.getValue(User.class);
                            if (displayParticipant.equals("")) {
                                displayParticipant = user.getFullName();
                            } else {
                                displayParticipant = displayParticipant + "\n" + user.getFullName();
                            }
                            otherParticipantView.setText(displayParticipant);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void populateCourse(final View mainView, final Meeting meeting, TextView subject) {
        if (meeting.getCourse() != null) {
            FullCourseList allCourses = FullCourseList.getInstance();
            Course courseMeeting = allCourses.getCourse(meeting.getCourse().getId());
            subject.setText(meeting.getCourse().getName());
            ImageView coursePicture = (ImageView) mainView.findViewById(R.id.coursePicture);
            coursePicture.setImageResource(courseMeeting.getPictureId());
        }
    }

}