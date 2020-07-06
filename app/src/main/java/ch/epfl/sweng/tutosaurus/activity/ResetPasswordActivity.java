package ch.epfl.sweng.tutosaurus.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.sweng.tutosaurus.R;

/**
 * Activity in which the user can request a password reset.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        Button resetPasswordButton = (Button) findViewById(R.id.rstPasswordButton);
        emailInput = (EditText) findViewById(R.id.resetEmailInput);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), R.string.request_registered_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new ProgressDialog(ResetPasswordActivity.this);
                progressDialog.setIcon(R.drawable.dino_logo);
                progressDialog.setMessage(getString(R.string.sending_request));
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(true);
                progressDialog.show();

                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPasswordActivity.this, R.string.reset_password_sent, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ResetPasswordActivity.this, R.string.reset_request_fail, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

}
