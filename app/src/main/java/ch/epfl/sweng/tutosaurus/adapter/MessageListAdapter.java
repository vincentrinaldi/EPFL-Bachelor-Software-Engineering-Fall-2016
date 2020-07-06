package ch.epfl.sweng.tutosaurus.adapter;


import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.model.Message;

/**
 * An adapter to display the messages between two users inside a ChatActivity.
 */
public class MessageListAdapter extends FirebaseListAdapter<Message>{

    private final static String TAG = "MessageListAdapter";
    private final static int MESSAGE_INNER_PADDING = 100;
    private final static int MESSAGE_OUTER_PADDING = 20;

    private String currentUser;
    private String otherUser;

    public MessageListAdapter(Activity activity, Class<Message> modelClass, int modelLayout, Query query, String currentUser, String otherUser) {
        super(activity, modelClass, modelLayout, query);
        this.currentUser = currentUser;
        this.otherUser = otherUser;
    }

    @Override
    protected void populateView(View mainView, Message message, int position) {
        LinearLayout content = (LinearLayout) mainView.findViewById(R.id.chat_message_row_content);
        TextView text = (TextView) mainView.findViewById(R.id.chat_message_row_content_text);
        String from = message.getFrom();
        if (from.equals(currentUser)) {
            content.setGravity(Gravity.END);
            content.setPadding(MESSAGE_INNER_PADDING, 0, 0, 0);
        } else {
            content.setGravity(Gravity.START);
            content.setPadding(0, 0, MESSAGE_INNER_PADDING, 0);
        }
        text.setText(message.getContent());
    }
}
