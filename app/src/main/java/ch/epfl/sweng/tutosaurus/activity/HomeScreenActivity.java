package ch.epfl.sweng.tutosaurus.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.fragment.AboutFragment;
import ch.epfl.sweng.tutosaurus.fragment.BeATutorFragment;
import ch.epfl.sweng.tutosaurus.fragment.FindTutorsFragment;
import ch.epfl.sweng.tutosaurus.fragment.HelpFragment;
import ch.epfl.sweng.tutosaurus.fragment.MeetingsFragment;
import ch.epfl.sweng.tutosaurus.fragment.MessagingFragment;
import ch.epfl.sweng.tutosaurus.fragment.ProfileFragment;
import ch.epfl.sweng.tutosaurus.fragment.SettingsFragment;
import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;
import ch.epfl.sweng.tutosaurus.helper.LocalDatabaseHelper;
import ch.epfl.sweng.tutosaurus.helper.PictureHelper;
import ch.epfl.sweng.tutosaurus.model.User;
import ch.epfl.sweng.tutosaurus.service.MeetingService;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The main activity of the app that contains the various fragments.
 */
public class HomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeScreenActivity";

    private static final int GALLERY_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private final int PROFILE_PICTURE_HEIGHT = 600;
    private final int PROFILE_PICTURE_WIDTH = 600;

    private ImageView pictureView;
    private CircleImageView circleView;

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        Intent serviceIntent = new Intent(this, MeetingService.class);
        getApplicationContext().startService(serviceIntent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        pictureView = (ImageView) findViewById(R.id.picture_view);

        circleView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.circleView);
        linkProfilePictureToNavView(circleView);

        //Handle specific intents
        if (intent.getAction() != null) {
            if (intent.getAction().equals("OPEN_TAB_PROFILE")) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
            }
            if (intent.getAction().equals("OPEN_TAB_MEETINGS")) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new MeetingsFragment()).commit();
            }
            if (intent.getAction().equals("OPEN_TAB_SETTINGS")) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
            }
            if(intent.getAction().equals("OPEN_TAB_MESSAGES")) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new MessagingFragment()).commit();
            }
        }

        String currentUser = null;
        DatabaseHelper dbh = DatabaseHelper.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentFirebaseUser != null) {
            currentUser = currentFirebaseUser.getUid();
        }
        String userId = currentUser;
        DatabaseReference ref = dbh.getReference();
        ref.child("user/" + userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User thisUser = dataSnapshot.getValue(User.class);
                dbHelper = new LocalDatabaseHelper(getBaseContext());
                if(dbHelper != null) {
                    database = dbHelper.getWritableDatabase();
                    LocalDatabaseHelper.insertUser(thisUser, database);
                }
                //Update profile picture and user information in burger menu header
                setBurgerMenuUser(thisUser.getFullName(), thisUser.getEmail(), thisUser.getSciper());
                getImage(thisUser.getSciper());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeScreenActivity.this, "Error Loading User", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "Restarted!");
        pictureView = (ImageView) findViewById(R.id.picture_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed!");
        pictureView = (ImageView) findViewById(R.id.picture_view);
    }

    /**
     * Send an intent to let the user call a phone number
     * @param view
     */
    public void sendMessageForCall(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: (+41)791380861"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    10);
        } else {
            startActivity(intent);
        }
    }

    /**
     * Send an intent to let the user send an email
     * @param view
     */
    public void sendMessageForEmail(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:")).setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "vincent.rinaldi@epfl.ch" });
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logOutButton) {
            mAuth.signOut();

            // Delete all data
            LocalDatabaseHelper.clear(dbHelper.getWritableDatabase());
            File file = new File(this.getFilesDir().getAbsolutePath() +
                                    File.separator + "user_profile_pic.bmp");
            file.delete();

            Intent logInIntent = new Intent(this, MainActivity.class);
            logInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logInIntent);
            Intent serviceIntent = new Intent(this, MeetingService.class);
            stopService(serviceIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_profile_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
        } else if (id == R.id.nav_findTutors_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FindTutorsFragment()).commit();
        } else if (id == R.id.nav_beATutor_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new BeATutorFragment()).commit();
        } else if (id == R.id.nav_messaging_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MessagingFragment(), "MESSAGING_FRAGMENT").commit();
        } else if (id == R.id.nav_meetings_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MeetingsFragment()).commit();
        } else if (id == R.id.nav_settings_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        } else if (id == R.id.nav_help_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HelpFragment()).commit();
        } else if (id == R.id.nav_about_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AboutFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Set the title of the tab as the text of the action bar
     * @param title
     */
    public void setActionBarTitle(String title) {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null) {
            mActionBar.setTitle(title);
        }
    }

    /**
     * Send an intent to open the photo gallery and select an image
     * @param view
     */
    private void loadImageFromGallery(View view) {
        Intent imageGalleryIntent = new Intent(Intent.ACTION_PICK);
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picturesDirectoryPath = picturesDirectory.getPath();
        Uri uriRepresentationPicturesDir = Uri.parse(picturesDirectoryPath);
        imageGalleryIntent.setDataAndType(uriRepresentationPicturesDir, "image/*");

        startActivityForResult(imageGalleryIntent, GALLERY_REQUEST);
    }

    /**
     * Send an intent to open the camera to take a photo
     * @param v
     */
    private void dispatchTakePictureIntent(View v) {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Toast.makeText(HomeScreenActivity.this, "No camera", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(HomeScreenActivity.this, "Camera is busy", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Start a chat after selecting one in the chat list
     * @param chatIntent
     */
    public void dispatchChatIntent(Intent chatIntent) {
        if(chatIntent.getComponent().getClassName().equals(ChatActivity.class.getName())) {
            startActivity(chatIntent);
        } else {
            Log.d(TAG, "not a chat intent");
        }
    }

    /**
     * Save the current picture to internal storage and update the circle picture in the menu
     * @param bitmapImage
     */
    private void saveToInternalStorage(Bitmap bitmapImage) {
        FileOutputStream fos = null;
        try {
            fos = getApplicationContext().openFileOutput("user_profile_pic.bmp", Context.MODE_PRIVATE);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        circleView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.circleView);
        linkProfilePictureToNavView(circleView);
    }

    /**
     * Update the circle picture in the navigator header of the burger menu
     * @param item
     */
    private void linkProfilePictureToNavView(CircleImageView item) {
        FileInputStream in;
        try {
            in = getApplicationContext().openFileInput("user_profile_pic.bmp");
            Bitmap b = BitmapFactory.decodeStream(in);
            if(b != null) {
                item.setImageBitmap(b);
            }
        }
        catch (FileNotFoundException e) {
            item.setImageResource(R.drawable.dino_logo);
            e.printStackTrace();
        }
    }

    /**
     * Handle camera and gallery intents
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST) {

            if (resultCode == RESULT_OK) {

                Uri imageSelectedUri = data.getData();
                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageSelectedUri);
                    Bitmap imageSelected = BitmapFactory.decodeStream(inputStream);
                    imageSelected = resizeBitmap(imageSelected);
                    pictureView = (ImageView) findViewById(R.id.picture_view);
                    pictureView.setImageBitmap(imageSelected);
                    pictureView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    saveToInternalStorage(imageSelected);
                    assert inputStream != null;
                    inputStream.close();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser != null) {
                        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        PictureHelper.storePicOnline(imageSelectedUri.getPath(), currentUserUid);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to load the image", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = resizeBitmap(imageBitmap);
            pictureView = (ImageView) findViewById(R.id.picture_view);
            pictureView.setImageBitmap(imageBitmap);
            pictureView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            saveToInternalStorage(imageBitmap);
        }
        // Store Profile Pic online
        User user = getUserLocalDB(this);
        if (user != null) {
            String filePath = this.getFilesDir().getAbsolutePath() + File.separator
                    + "user_profile_pic.bmp";
            PictureHelper.storePicOnline(filePath, user.getSciper());
            PictureHelper.storePicOnline(filePath, user.getUid());
        }
    }

    /**
     * Resize image to specific dimensions
     * @param img
     */
    private Bitmap resizeBitmap(Bitmap img) {
        return ThumbnailUtils.extractThumbnail(img, PROFILE_PICTURE_WIDTH, PROFILE_PICTURE_HEIGHT);
    }

    /**
     * Build the Dialog View for taking/selecting a new profile picture
     * @param view
     */
    public void showChangePictureDialog(final View view){
        AlertDialog.Builder changePictureDialog = new AlertDialog.Builder(this);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item);

        arrayAdapter.add("Take picture with camera");
        arrayAdapter.add("Load picture from gallery");

        changePictureDialog.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        changePictureDialog.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        if (strName != null) {
                            if(strName.equals("Take picture with camera")){
                                dispatchTakePictureIntent(view);
                            }
                            else if(strName.equals("Load picture from gallery")){
                                loadImageFromGallery(view);
                            }
                        }
                    }
                });
        changePictureDialog.show();
    }

    /**
     * Return user saved
     * @param context
     * @return null
     */
    @Nullable
    private User getUserLocalDB(Context context) {
        dbHelper = new LocalDatabaseHelper(context);
        if(dbHelper != null) {
            database = dbHelper.getReadableDatabase();
            return LocalDatabaseHelper.getUser(database);
        }
        return null;
    }

    /**
     * Retrieve profile picture from Firebase
     * @param key
     */
    private void getImage(String key) {
        StorageReference storageRef = FirebaseStorage.getInstance().
                getReferenceFromUrl("gs://tutosaurus-16fce.appspot.com");
        final long MAX_SIZE = 4096 * 4096;
        storageRef.child("profilePictures/" + key + ".png").getBytes(
                Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ImageView img = (ImageView) findViewById(R.id.picture_view);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if(img != null) {
                    img.setImageBitmap(bmp);
                    saveToInternalStorage(bmp);
                    linkProfilePictureToNavView(circleView);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(HomeScreenActivity.this, "Problem Retrieving Image", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Set the TextView of the burger menu
     * @param fullName
     * @param email
     * @param sciper
     */
    private void setBurgerMenuUser(String fullName, String email, String sciper) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        TextView nameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.fullName);
        nameView.setText(fullName);

        TextView addressView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.mailAddress);
        addressView.setText(email);

        TextView sciperView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.sciper);
        sciperView.setText(sciper);
    }
}
