package ch.epfl.sweng.tutosaurus.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;
import ch.epfl.sweng.tutosaurus.adapter.MeetingAdapter;
import ch.epfl.sweng.tutosaurus.adapter.MeetingConfirmationAdapter;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.helper.LocalDatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.MeetingRequest;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * Fragment where the main profile of the user is displayed
 */
public class ProfileFragment extends Fragment {

    private View myView;

    private String currentUser;
    DatabaseHelper dbh = DatabaseHelper.getInstance();
    private MeetingAdapter adapter;

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.profile_layout, container, false);
        final Activity activity = getActivity();
        ((HomeScreenActivity)activity).setActionBarTitle("Profile");
        loadImageFromStorage();

        DatabaseHelper dbh = DatabaseHelper.getInstance();

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentFirebaseUser != null) {
            currentUser = currentFirebaseUser.getUid();
        }

        final String userId = currentUser;

        DatabaseReference ref = dbh.getReference();
        ref.child("user/" + userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User thisUser = dataSnapshot.getValue(User.class);

                // Set profile name
                TextView profileName = (TextView) myView.findViewById(R.id.profileName);
                String name = thisUser.getFullName();
                if(name.length() > 30) {
                    name = name.substring(0,30) + "…";
                }
                profileName.setText(name);

                // Set rating
                RatingBar ratingBar = (RatingBar) myView.findViewById(R.id.ratingBar);
                ratingBar.setRating(thisUser.getGlobalRating());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                // if offline, retrieve user from local database
                User user = getUserLocalDB(activity);
                if (user == null) {
                    return;
                }
                TextView profileName = (TextView) myView.findViewById(R.id.profileName);
                profileName.setText(user.getFullName());
                RatingBar ratingBar = (RatingBar) myView.findViewById(R.id.ratingBar);
                ratingBar.setRating(user.getGlobalRating());
            }
        });

        Query refRequestedMeeting = dbh.getMeetingRequestsRef().child(currentUser);
        ListView meetingRequested = (ListView) myView.findViewById(R.id.meetingRequests);
        MeetingConfirmationAdapter requestedMeetingAdapter = new MeetingConfirmationAdapter(getActivity(),
                                                                                            MeetingRequest.class,
                                                                                            R.layout.meeting_confirmation_row,
                                                                                            refRequestedMeeting);
        meetingRequested.setAdapter(requestedMeetingAdapter);

        return myView;
    }

    /**
     * Load the image stored in internal storage and set it as the profile picture
     */
    private void loadImageFromStorage() {
        FileInputStream in;
        try {
            in = getActivity().getApplicationContext().openFileInput("user_profile_pic.bmp");
            Bitmap b = BitmapFactory.decodeStream(in);
            ImageView img = (ImageView) myView.findViewById(R.id.picture_view);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e) {
            ImageView img = (ImageView) myView.findViewById(R.id.picture_view);
            img.setImageResource(R.drawable.dino_logo);
            e.printStackTrace();
        }
    }

    /**
     * Return the user saved or Null if nothing in local database
     * @param context
     * @return null
     */
    @Nullable
    private User getUserLocalDB(Context context) {
        dbHelper = new LocalDatabaseHelper(context);
        Activity activity = getActivity();
        if(dbHelper != null) {
            database = dbHelper.getReadableDatabase();
            return LocalDatabaseHelper.getUser(database);
        }
        return null;
    }
}
