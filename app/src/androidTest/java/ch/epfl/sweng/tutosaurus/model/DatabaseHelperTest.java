package ch.epfl.sweng.tutosaurus.model;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;

import static junit.framework.Assert.assertEquals;

/**
 * Created by albertochiappa on 17/12/16.
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
    String einsteinId = "TLL2vWfIytQUDidJbIy1hFv0mqC3";
    DatabaseHelper dbh = DatabaseHelper.getInstance();

    @Test
    public void canSetGlobalRating(){
        dbh.setRating(einsteinId, 3.5f);
        DatabaseReference einsteinRef = dbh.getUserRef().child(einsteinId);
        einsteinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User einstein = dataSnapshot.getValue(User.class);
                final float einsteinCurrentRating = einstein.getGlobalRating();
                assertEquals(einsteinCurrentRating, 3.5f);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    @Test
    public void canSetNumRAtings(){
        dbh.setNumRatings(einsteinId, 17);
        DatabaseReference einsteinRef = dbh.getUserRef().child(einsteinId);
        einsteinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User einstein = dataSnapshot.getValue(User.class);
                final int einsteinCurrentNumRating = einstein.getNumRatings();
                assertEquals(einsteinCurrentNumRating, 17);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    @Test
    public void canRemoveTeacherFromCourse(){
        dbh.removeTeacherFromCourse(einsteinId, "mathematics");
        dbh.addTeacherToCourse(einsteinId, "mathematics");
    }

    @Test
    public void canRemoveLanguage(){
        dbh.removeLanguageFromUser(einsteinId, "english");
        dbh.addLanguageToUser(einsteinId, "english");
    }


}
