package ch.epfl.sweng.tutosaurus.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.tutosaurus.model.User;

/**
 * Utility class to ease manipulatin of the local SQL database.
 */
public class LocalDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOGCAT = "LOGCATDB";

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;

    // table user
    public static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_SCIPER = "user_sciper";
    private static final String COLUMN_USER_USERNAME = "user_username";
    private static final String COLUMN_USER_FULLNAME = "user_full_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PICTURE = "user_picture";
    private static final String COLUMN_USER_GLOBAL_RATING = "user_global_rating";

    // create table of user
    private static final String CREATE_TABLE_USER = " CREATE TABLE " + TABLE_USER + "(" +
            COLUMN_USER_ID + " STRING PRIMARY KEY, " +
            COLUMN_USER_SCIPER + " TEXT, " +
            COLUMN_USER_USERNAME + " TEXT, " +
            COLUMN_USER_FULLNAME + " TEXT, " +
            COLUMN_USER_EMAIL + " TEXT, " +
            COLUMN_USER_PICTURE + " INTEGER, " +
            COLUMN_USER_GLOBAL_RATING + " REAL " + ")";

    // table courseé
    public static final String TABLE_COURSE = "course";
    private static final String COLUMN_COURSE_NAME = "course_name";
    private static final String COLUMN_COURSE_IS_TEACHED = "course_is_teached";
    private static final String COLUMN_COURSE_IS_STUDIED = "course_is_studied";
    private static final String COLUMN_COURSE_RATING = "course_rating";
    private static final String COLUMN_COURSE_HOURS = "course_hours";


    // create table of course
    private static final String CREATE_TABLE_COURSE = " CREATE TABLE " + TABLE_COURSE + "(" +
            COLUMN_COURSE_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_COURSE_IS_TEACHED + " INTEGER, " +
            COLUMN_COURSE_IS_STUDIED + " INTEGER, " +
            COLUMN_COURSE_RATING + " REAL, " +
            COLUMN_COURSE_HOURS + " INTEGER " + ")";


    public static final String TABLE_LANGUAGE = "language";
    private static final String COLUMN_LANGUAGE_NAME = "language_name";

    // create table of langugage
    private static final String CREATE_TABLE_LANGUAGE = " CREATE TABLE " + TABLE_LANGUAGE + "(" +
            COLUMN_LANGUAGE_NAME + " TEXT PRIMARY KEY " + ")";


    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        Log.d(LOGCAT, "User table has been created");
        db.execSQL(CREATE_TABLE_COURSE);
        Log.d(LOGCAT, "Course table has been created");
        db.execSQL(CREATE_TABLE_LANGUAGE);
        Log.d(LOGCAT, "Language table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LANGUAGE);
        onCreate(db);
    }

    /**
     * Clear the local database
     * @param db writable database
     */
    public static void clear(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LANGUAGE);
    }


    /**
     * Save an User object into the database.
     *
     * also check for the profile picture (user = int, db = string)
     * @param user the user to inject
     * @param db the SQL database in which to inject the user
     */
    public static void insertUser(User user,SQLiteDatabase db) {
        if (user == null) {
            throw new NullPointerException();
        } else {db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_COURSE);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LANGUAGE);
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_COURSE);
            db.execSQL(CREATE_TABLE_LANGUAGE);

            insertUserTable(user,db);
            insertCourseTable(user,db);
            insertLanguageTable(user,db);
        }
    }



    private static void insertUserTable(User user, SQLiteDatabase db) {
        ContentValues userValues = new ContentValues();

        // USER TABLE
        userValues.put(COLUMN_USER_SCIPER, user.getSciper());
        userValues.put(COLUMN_USER_USERNAME, user.getUsername());
        userValues.put(COLUMN_USER_FULLNAME, user.getFullName());
        userValues.put(COLUMN_USER_EMAIL, user.getEmail());
        userValues.put(COLUMN_USER_ID, user.getUid());
        userValues.put(COLUMN_USER_PICTURE, user.getPicture());
        userValues.put(COLUMN_USER_GLOBAL_RATING, user.getGlobalRating());

        db.insert(TABLE_USER, null, userValues);
    }

    /**
     * Clear the local database and add an user
     * @param user
     * @param db
     */
    private static void insertCourseTable(User user, SQLiteDatabase db) {

        // COURSE TABLE
        Set<String> courseNames = new HashSet<>();
        courseNames.addAll(user.getStudying().keySet());
        courseNames.addAll(user.getTeaching().keySet());

        for(String key : courseNames) {
            ContentValues courseValues = new ContentValues();

            courseValues.put(COLUMN_COURSE_NAME, key);

            if (user.getStudying().containsKey(key)) {
                courseValues.put(COLUMN_COURSE_IS_STUDIED, true);
            } else {
                courseValues.put(COLUMN_COURSE_IS_STUDIED, false);
            }

            if (user.isTeacher(key)) {
                courseValues.put(COLUMN_COURSE_IS_TEACHED, true);
                courseValues.put(COLUMN_COURSE_RATING, user.getCourseRating(key));
            } else {
                courseValues.put(COLUMN_COURSE_IS_TEACHED, false);
                courseValues.put(COLUMN_COURSE_RATING, 0.0);
            }

            courseValues.put(COLUMN_COURSE_HOURS, user.getHoursTaught(key));
            db.insert(TABLE_COURSE, null, courseValues);

        }

    }


    /**
     * Store the language of the user
     * @param user
     * @param db
     */
    private static void insertLanguageTable(User user, SQLiteDatabase db) {

        // LANGUAGE TABLE
        for(String key : user.getLanguages().keySet()) {
            ContentValues languageValues = new ContentValues();
            languageValues.put(COLUMN_LANGUAGE_NAME, key);
            db.insert(TABLE_LANGUAGE, null, languageValues);
        }

    }

    /**
     * Return the user of the local database
     * @param db a readable database
     * @return User or Null if error
     */
    @Nullable
    public static User getUser(SQLiteDatabase db) {
        User user = getUserTable(db);
        getCourse(user, db);
        getLanguage(user, db);
        return user;
    }

    /**
     * Return an User without language nor course
     * @param db
     * @return
     */
    @Nullable
    private static User getUserTable(SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            User user = new User(cursor.getString(1));
            user.setUsername(cursor.getString(2));
            user.setFullName(cursor.getString(3));
            user.setEmail(cursor.getString(4));
            user.setUid(cursor.getString(0));
            user.setPicture(cursor.getInt(5));
            user.setGlobalRating(cursor.getFloat(6));
            return user;
        } else {
            return null;
        }
    }

    /**
     * Set the course for the user
     * @param user
     * @param db readable database
     */
    private static void getCourse(User user, SQLiteDatabase db) {
        if(user == null) {
            return;
        }
        String query = "SELECT * FROM " + TABLE_COURSE;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                // if course is teached
                if(cursor.getInt(1) != 0) {
                    user.addTeaching(cursor.getString(0));
                    user.setCourseRating(cursor.getString(0), cursor.getDouble(3));
                    user.addHoursTaught(cursor.getString(0), cursor.getInt(4));
                }
                // if course is studied
                if (cursor.getInt(2) != 0) {
                    user.addStudying(cursor.getString(0));
                }
            }
        }
    }

    /**
     * Set the language for the user
     * @param user
     * @param db readable database
     */
    private static void getLanguage(User user, SQLiteDatabase db) {
        if (user == null) {
            return;
        }

        String query = "SELECT * FROM " + TABLE_LANGUAGE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                user.addLanguage(cursor.getString(0));
            }
        }
    }
}


