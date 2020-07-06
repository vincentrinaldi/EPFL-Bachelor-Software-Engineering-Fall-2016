package ch.epfl.sweng.tutosaurus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sweng.tutosaurus.R;

/**
 * An activity in which the user can request a password change.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ChangePasswordActivity.this, HomeScreenActivity.class);
            intent.setAction("OPEN_TAB_SETTINGS");
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Change the password of an user account
     * @param view
     */
    public void changePassword(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText newPasswordChoosed = (EditText) findViewById(R.id.chooseNewPass);
        EditText newPasswordConfirmed = (EditText) findViewById(R.id.confirmNewPass);

        if (newPasswordChoosed.getText().toString().equals("") || newPasswordConfirmed.getText().toString().equals("")) {
            Toast.makeText(this, "Please fill both boxes above", Toast.LENGTH_SHORT).show();
        } else if (!newPasswordChoosed.getText().toString().equals(newPasswordConfirmed.getText().toString())) {
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
        } else {
            user.updatePassword(newPasswordChoosed.getText().toString());
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            newPasswordChoosed.getText().clear();
            newPasswordConfirmed.getText().clear();
        }
    }
}
