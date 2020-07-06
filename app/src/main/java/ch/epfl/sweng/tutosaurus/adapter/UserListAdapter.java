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
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.model.User;

public class UserListAdapter extends FirebaseListAdapter<User> {

    private static final String TAG = "UserListAdapter";
    private Activity activity;

    public UserListAdapter(Activity activity, java.lang.Class<User> modelClass, int modelLayout, Query query) {
        super(activity, modelClass, modelLayout, query);
        this.activity = activity;
    }

    @Override
    protected void populateView(View mainView, User user, int position) {
        TextView userFullName = (TextView) mainView.findViewById(R.id.message_chat_row_name);
        userFullName.setText(user.getFullName());
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://tutosaurus-16fce.appspot.com");
        StorageReference picRef = storageRef.child("profilePictures").child(user.getUid()+".png");
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
