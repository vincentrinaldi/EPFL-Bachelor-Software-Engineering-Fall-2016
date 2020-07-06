package ch.epfl.sweng.tutosaurus.SearchFactory;

import com.google.firebase.database.Query;

import ch.epfl.sweng.tutosaurus.helper.DatabaseHelper;

/**
 * A criterion to search for tutors by their name.
 */
public class SearchByName implements SearchCriterion {

    private DatabaseHelper dbh;

    @Override
    public Query performSearch(String nameToSearch) {
        dbh = DatabaseHelper.getInstance();
        Query ref = dbh.getUserRef();
        ref = ref.orderByChild("fullName").equalTo(nameToSearch);
        return ref;
    }
}
