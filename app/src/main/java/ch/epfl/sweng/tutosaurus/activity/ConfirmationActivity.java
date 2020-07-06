package ch.epfl.sweng.tutosaurus.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.helper.LocalDatabaseHelper;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * The activity in which the user confirms that the registration data provided is correct.
 */
public class ConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmationActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String email;
    private String fullName;
    private String sciper;
    private String gaspar;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        setConfirmationAuthListener();

        Intent intent = getIntent();

        String first_name = intent.getStringExtra(RegisterScreenActivity.EXTRA_MESSAGE_FIRST_NAME);
        String last_name = intent.getStringExtra(RegisterScreenActivity.EXTRA_MESSAGE_LAST_NAME);
        email = intent.getStringExtra(RegisterScreenActivity.EXTRA_MESSAGE_EMAIL_ADDRESS);
        sciper = intent.getStringExtra(RegisterScreenActivity.EXTRA_MESSAGE_SCIPER);
        fullName = first_name + " " + last_name;
        gaspar = intent.getStringExtra(RegisterScreenActivity.EXTRA_MESSAGE_GASPAR);

        TextView first_name_text = (TextView) findViewById(R.id.firstNameProvided);
        first_name_text.setText(getString(R.string.confirmation_first_name, first_name));
        TextView last_name_text = (TextView) findViewById(R.id.lastNameProvided);
        last_name_text.setText(getString(R.string.confirmation_last_name, last_name));
        TextView email_address_text = (TextView) findViewById(R.id.emailAddressProvided);
        email_address_text.setText(getString(R.string.confirmation_email, email));
        TextView sciper_text = (TextView) findViewById(R.id.sciperProvided);
        sciper_text.setText(getString(R.string.confirmation_sciper, sciper));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), RegisterScreenActivity.class);
                startActivityForResult(myIntent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeConfirmationAuthListener();
    }

    public void confirmRegistration(View view) {
        String pw1 = ((EditText) findViewById(R.id.confirmation_password1)).getText().toString();
        String pw2 = ((EditText) findViewById(R.id.confirmation_password2)).getText().toString();
        if (pw1.isEmpty() || pw2.isEmpty()) {
            Toast.makeText(this, R.string.missing_password, Toast.LENGTH_SHORT).show();
        } else if (!pw1.equals(pw2)) {
            Toast.makeText(this, R.string.mismatch_password, Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, pw1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(ConfirmationActivity.this, R.string.authentification_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        String uid = user.getUid();
                        DatabaseHelper dbh = DatabaseHelper.getInstance();
                        User user = new User(sciper, gaspar);
                        user.setEmail(email);
                        user.setFullName(fullName);
                        user.setUid(uid);
                        dbh.signUp(user);
                        saveUserLocalDB(user);
                        startActivity(new Intent(ConfirmationActivity.this, MainActivity.class));
                    }
                }
            });
        }
    }

    private void saveUserLocalDB(User user) {
        dbHelper = new LocalDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        LocalDatabaseHelper.insertUser(user, database);
    }

    protected void setConfirmationAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    protected void removeConfirmationAuthListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
