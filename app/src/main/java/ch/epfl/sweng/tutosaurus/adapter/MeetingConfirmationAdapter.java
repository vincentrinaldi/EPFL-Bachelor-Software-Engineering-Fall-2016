package ch.epfl.sweng.tutosaurus.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.MeetingRequest;

/**
 * Firebase adapter used to populate the list of meetings to be confirmed by the user in ProfileFragment
 */

public class MeetingConfirmationAdapter extends FirebaseListAdapter<MeetingRequest> {

    public static final String TAG = "MeetingConfAdapter";
    private String currentUserUid;
    private DatabaseHelper dbh;


    public MeetingConfirmationAdapter(Activity activity, Class<MeetingRequest> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
        }
        dbh = DatabaseHelper.getInstance();
    }


    /**
     * Method that fill the list of meeting
     *
     * @param mainView view of the fragment where the adapter is used
     * @param request meeting request
     * @param position position in the list
     */
    @Override
    protected void populateView(View mainView, final MeetingRequest request, int position) {
        TextView description = (TextView) mainView.findViewById(R.id.meeting_confirmation_row_description);
        final TextView name = (TextView) mainView.findViewById(R.id.meeting_confirmation_row_name);
        TextView location = (TextView) mainView.findViewById(R.id.meeting_confirmation_row_location);
        TextView date = (TextView) mainView.findViewById(R.id.meeting_confirmation_row_date);

        populateStudentName(request, name);

        if (request.getMeeting().getNameLocation() != null) {
            location.setText(request.getMeeting().getNameLocation());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, HH:mm", Locale.ENGLISH);
        date.setText(dateFormat.format(request.getMeeting().getDate()));

        if (request.getMeeting().getDescription() != null) {
            description.setText(request.getMeeting().getDescription());
        }

        Button confirmMeetingButton = (Button) mainView.findViewById(R.id.meeting_confirmation_row_confirm);
        confirmMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbh.confirmMeeting(currentUserUid, request);
            }
        });
    }


    private void populateStudentName(MeetingRequest request, final TextView name) {
        String fromUid = request.getFrom();
        DatabaseReference fullNameRef = DatabaseHelper.getInstance().getUserRef().child(fromUid).child("fullName");
        fullNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText((String)dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        name.setText(request.getFrom());
    }
}