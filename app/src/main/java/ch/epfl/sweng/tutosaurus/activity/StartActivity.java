package ch.epfl.sweng.tutosaurus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Headless activity that checks wether an user is logged in at application startup.
 */
public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
        if(curUser != null) {
            Log.d(TAG, "already logged in: " + curUser.getEmail());
            dispatchHomeScreenIntent();
        } else {
            Log.d(TAG, "not logged in");
            dispatchLogInIntent();
        }
    }

    private void dispatchHomeScreenIntent() {
        Intent intent = new Intent(StartActivity.this, HomeScreenActivity.class);
        intent.setAction("OPEN_TAB_PROFILE");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    private void dispatchLogInIntent() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }
}
