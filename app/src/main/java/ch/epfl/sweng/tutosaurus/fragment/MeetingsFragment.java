package ch.epfl.sweng.tutosaurus.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;
import ch.epfl.sweng.tutosaurus.adapter.MeetingAdapter;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.Meeting;

/**
 * Fragment where the list of all the meetings of the user is shown
 */
public class MeetingsFragment extends Fragment {

    private static final long DIFFERENCE_TIME_JAVA = 59958144000000L; //discrepancy with Firebase date
    public static final int MONTH_IN_MILLISECONDS = 86400 * 7 * 1000;
    private MeetingAdapter adapter;
    private String currentUserUid;
    private DatabaseHelper dbh = DatabaseHelper.getInstance();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.meetings_layout, container, false);
        ((HomeScreenActivity) getActivity()).setActionBarTitle("Meetings");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            currentUserUid = currentUser.getUid();
        }

        ListView meetingList = (ListView) myView.findViewById(R.id.meetingList);
        Query ref = dbh.getMeetingsRefForUser(currentUserUid);
        long lastWeekInMillis = System.currentTimeMillis() + DIFFERENCE_TIME_JAVA - MONTH_IN_MILLISECONDS;
        ref = ref.orderByChild("date/time").startAt(lastWeekInMillis);
        adapter = new MeetingAdapter(getActivity(), Meeting.class, R.layout.listview_meetings_row, ref);
        meetingList.setAdapter(adapter);

        return myView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}