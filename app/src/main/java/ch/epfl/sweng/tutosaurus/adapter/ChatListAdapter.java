package ch.epfl.sweng.tutosaurus.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.Chat;

/**
 * An adapter for a list of chats on Firebase.
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat>{

    private static final String TAG = "ChatListAdapter";
    private Activity activity;

    /**
     * A public constructor for the ChatListAdapter
     * @param activity the activity this adapter will be bound to
     * @param modelClass the type of object this adapter will receive from Firebase
     * @param modelLayout the layout that will be populated with the data
     * @param ref the location in Firebase from which this adapter will get its data
     */
    public ChatListAdapter(Activity activity, java.lang.Class<Chat> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
    }

    @Override
    protected void populateView(final View mainView, Chat chat, int position) {
        TextView chatName = (TextView) mainView.findViewById(R.id.message_chat_row_name);
        chatName.setText(chat.getFullName());

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://tutosaurus-16fce.appspot.com");
        StorageReference picRef = storageRef.child("profilePictures").child(chat.getUid()+".png");
        ImageView profilePicture = (ImageView) mainView.findViewById(R.id.profilePicture);

        Glide.with(activity)
                .using(new FirebaseImageLoader())
                .load(picRef)
                /* Glide uses the hash of the path to determine cache invalidation. There is no easy way to determine
                * if a file with the same path has changed. A workaround is to define a signature that is always
                * different so that Glide fetches the data each time.
                * .signature(new StringSignature(String.valueOf(System.currentTimeMillis()))) */
                .into(profilePicture);
    }
}
