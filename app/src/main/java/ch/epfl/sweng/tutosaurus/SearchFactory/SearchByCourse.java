package ch.epfl.sweng.tutosaurus.SearchFactory;

import com.google.firebase.database.Query;

import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;

/**
 * Created by albertochiappa on 09/12/16.
 */

public class SearchByCourse implements SearchCriterion {

    private DatabaseHelper dbh;

    @Override
    public Query performSearch(String courseId) {
        dbh = DatabaseHelper.getInstance();
        Query ref = dbh.getUserRef();
        ref = findTutorBySubject(courseId, ref);
        return ref;
    }

    private Query findTutorBySubject(String subject, Query userRef){
        return userRef.orderByChild("teaching/" + subject).equalTo(true);
    }
}
