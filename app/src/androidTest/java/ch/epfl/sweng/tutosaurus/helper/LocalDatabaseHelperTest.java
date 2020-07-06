package ch.epfl.sweng.tutosaurus.helper;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ch.epfl.sweng.tutosaurus.model.User;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LocalDatabaseHelperTest {

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;
    private User albert;
    private HashMap<String, Boolean> languages;
    private HashMap <String, Boolean>meetings;
    private HashMap <String, String>coursePresentation;

    @Before
    public void setup() {
        getTargetContext().deleteDatabase(LocalDatabaseHelper.TABLE_COURSE);
        getTargetContext().deleteDatabase(LocalDatabaseHelper.TABLE_USER);
        getTargetContext().deleteDatabase(LocalDatabaseHelper.TABLE_LANGUAGE);
        dbHelper = new LocalDatabaseHelper(getTargetContext());
        database = dbHelper.getWritableDatabase();

        albert = new User("111111");
        // characteristics
        albert.setUsername("Albert");
        albert.setFullName("Albert Einstein");
        albert.setEmail("albert.einstein@epfl.ch");
        albert.setUid("uid");
        albert.setGlobalRating(1.0F);
        albert.setPicture(0);
        // Course
        albert.addTeaching("Physics");
        albert.addTeaching("French");
        albert.setCourseRating("Physics", 1.0);
        albert.setCourseRating("French", 0.5);
        albert.addHoursTaught("Physics", 100);
        albert.addHoursTaught("French", 10);
        albert.addStudying("Java");
        // Course Presentation
        coursePresentation = new HashMap<>();
        coursePresentation.put("Physics", "Relativity");
        coursePresentation.put("French", "Chocolatine");
        albert.setCoursePresentation(coursePresentation);
        // Languages
        languages = new HashMap<>();
        languages.put("German", true);
        languages.put("French", true);
        languages.put("Chinese", false);
        albert.setSpeaking(languages);
        // Meetings
        meetings = new HashMap<>();
        meetings.put("meet1", true);
        meetings.put("meet2", true);
        albert.setMeetings(meetings);
    }

    @After
    public void tearDown() {
        LocalDatabaseHelper.clear(database);
    }

    @Test
    public void testGetDatabase() {
        dbHelper.getReadableDatabase();
        dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(database,1,1);
        LocalDatabaseHelper.clear(database);
    }

    @Test(expected = NullPointerException.class)
    public void insertNullUserTest() {
        User user = null;
        LocalDatabaseHelper.insertUser(user,database);
    }

    // Compare basic attribute of albert and dbAlbert
    @Test
    public void basicAttributeTest() {
        LocalDatabaseHelper.insertUser(albert,database);
        User dbAlbert = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertTrue(albert.getSciper().equals(dbAlbert.getSciper()));
        assertTrue(albert.getUsername().equals(dbAlbert.getUsername()));
        assertTrue(albert.getFullName().equals(dbAlbert.getFullName()));
        assertTrue(albert.getEmail().equals(dbAlbert.getEmail()));
        assertTrue(albert.getUid().equals(dbAlbert.getUid()));
        assertEquals(albert.getGlobalRating(),dbAlbert.getGlobalRating());
        assertEquals(albert.getPicture(), dbAlbert.getPicture());
    }

    // Compare the language Hashmap of albert and dbAlbert
    @Test
    public void userLanguagesTest() {
        LocalDatabaseHelper.insertUser(albert,database);
        User dbAlbert = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertTrue(albert.getLanguages().equals(dbAlbert.getLanguages()));
    }


    // Compare the teaching Hashmap of albert and dbAlbert
    @Test
    public void userCourseTeachingTest() {
        LocalDatabaseHelper.insertUser(albert,database);
        User dbAlbert = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertTrue(albert.getTeaching().equals(dbAlbert.getTeaching()));
    }

    @Test
    public void userCourseStudyingTest() {
        LocalDatabaseHelper.insertUser(albert,database);
        User dbAlbert = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertTrue(albert.getStudying().equals(dbAlbert.getStudying()));
    }

    @Test
    public void userCourseRatingTest() {
        LocalDatabaseHelper.insertUser(albert,database);
        User dbAlbert = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertEquals(albert.getCourseRating("Physics"), dbAlbert.getCourseRating("Physics"));
        assertEquals(albert.getCourseRating("French"), dbAlbert.getCourseRating("French"));
    }


    @Test
    public void userCourseHourTaughtTest() {
        LocalDatabaseHelper.insertUser(albert,database);
        User dbAlbert = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertEquals(albert.getCourseRating("Physics"), dbAlbert.getCourseRating("Physics"));
        assertEquals(albert.getCourseRating("French"), dbAlbert.getCourseRating("French"));
    }

    @Test
    public void emptyLanguageTest() {
        User userNoLanguage = new User("111111");
        LocalDatabaseHelper.insertUser(userNoLanguage, database);
        User dbUserNoLanguage = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertTrue(dbUserNoLanguage.getLanguages().isEmpty());
    }

    @Test
    public void emptyCourseTest() {
        User userNoLanguage = new User("111111");
        LocalDatabaseHelper.insertUser(userNoLanguage, database);
        User dbUserNoLanguage = LocalDatabaseHelper.getUser(dbHelper.getReadableDatabase());

        assertTrue(dbUserNoLanguage.getTeaching().isEmpty());
        assertTrue(dbUserNoLanguage.getStudying().isEmpty());
    }


}
