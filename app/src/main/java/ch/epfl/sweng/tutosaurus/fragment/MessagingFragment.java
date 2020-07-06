package ch.epfl.sweng.tutosaurus.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.activity.ChatActivity;
import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;
import ch.epfl.sweng.tutosaurus.adapter.ChatListAdapter;
import ch.epfl.sweng.tutosaurus.adapter.UserListAdapter;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.Chat;
import ch.epfl.sweng.tutosaurus.model.Identifiable;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * A fragment where the user can select with which other user they want to chat.
 */
public class MessagingFragment extends Fragment {

    private static final String TAG = "MessagingFragment";

    private View myView;
    private ListView listView;

    private ChatListAdapter chatListAdapter;
    private UserListAdapter userListAdapter;

    public static final String EXTRA_MESSAGE_USER_ID = "ch.epfl.sweng.tutosaurus.USER_ID";
    public static final String EXTRA_MESSAGE_FULL_NAME = "ch.epfl.seng.tutosaurus.FULL_NAME";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.messaging_fragment, container, false);
        ((HomeScreenActivity) getActivity()).setActionBarTitle("Messages");
        DatabaseHelper dbh = DatabaseHelper.getInstance();
        listView = (ListView) myView.findViewById(R.id.message_list);
        String currentUserUid = "";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            currentUserUid = currentUser.getUid();
        }
        Query chatRef = dbh.getReference().child("chats").child(currentUserUid);
        Log.d(TAG, "chatRef: " + chatRef.toString());
        Query userRef = dbh.getReference().child("user");

        chatListAdapter = new ChatListAdapter(getActivity(), Chat.class, R.layout.message_chat_row, chatRef);
        userListAdapter = new UserListAdapter(getActivity(), User.class, R.layout.message_chat_row, userRef);

        listView.setAdapter(userListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Identifiable item = (Identifiable) listView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EXTRA_MESSAGE_USER_ID, item.getUid());
                intent.putExtra(EXTRA_MESSAGE_FULL_NAME, item.getFullName());
                Log.d(TAG, "fullName: " + item.getFullName());
                ((HomeScreenActivity) getActivity()).dispatchChatIntent(intent);
            }
        });

        setHasOptionsMenu(true);

        Intent resumeIntent = new Intent(getActivity(), HomeScreenActivity.class);
        resumeIntent.setAction("OPEN_TAB_MESSAGES");
        getActivity().setIntent(resumeIntent);

        return myView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_messaging_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_adapter :
                if (listView.getAdapter().equals(chatListAdapter)) {
                    listView.setAdapter(userListAdapter);
                } else {
                    listView.setAdapter(chatListAdapter);
                }
        }
        return true;
    }
}
