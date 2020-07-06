package ch.epfl.sweng.tutosaurus.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.network.NetworkChangeReceiver;

import static ch.epfl.sweng.tutosaurus.network.NetworkChangeReceiver.LOG_TAG;

/**
 * The welcoming application of the app, in which the user logs in.
 */
public class MainActivity extends AppCompatActivity {

    private NetworkChangeReceiver receiver;
    private TextView networkStatus;

    private final static String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        receiver.setActivity(MainActivity.this);
        receiver.setBroadcastToastEnabled();

        networkStatus = (TextView) findViewById(R.id.networkStatus);
        receiver.setNetStatusTextView(networkStatus);

        Button resetPasswordButton = (Button) findViewById(R.id.forgotPasswordButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);
        Button login = (Button) findViewById(R.id.connectionButton);

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(resetPasswordButton);
        buttons.add(registerButton);
        buttons.add(login);

        receiver.setButtonsToManage(buttons);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ResetPasswordActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder loginAlertB = new AlertDialog.Builder(MainActivity.this);
                loginAlertB.setTitle(R.string.login).setPositiveButton("Ok", null).setIcon(R.drawable.dino_logo);
                String email = ((EditText) findViewById(R.id.main_email)).getText().toString();
                String password = ((EditText) findViewById(R.id.main_password)).getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    loginAlertB.setMessage(R.string.request_email_and_password);
                    loginAlertB.create().show();
                } else {
                    LoginAsyncTask loginTask = new LoginAsyncTask();
                    loginTask.execute(email, password);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

        unregisterReceiver(receiver);

    }


    public void sendMessageForReg(View view) {
        Intent intent = new Intent(this, RegisterScreenActivity.class);
        startActivity(intent);
    }


    private void dispatchHomeScreenIntent() {
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        intent.setAction("OPEN_TAB_PROFILE");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private class LoginAsyncTask extends AsyncTask<String, String, Task> {

        @Override
        protected Task doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
            task.addOnCompleteListener(new LoginOnCompleteListener());
            try {
                Tasks.await(task);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return task;
        }
    }

    private class LoginOnCompleteListener implements OnCompleteListener<AuthResult> {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                dispatchHomeScreenIntent();
            } else {
                Toast.makeText(MainActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
