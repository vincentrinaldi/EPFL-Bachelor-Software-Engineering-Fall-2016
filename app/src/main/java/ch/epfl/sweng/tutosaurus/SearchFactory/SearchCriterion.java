package ch.epfl.sweng.tutosaurus.SearchFactory;

import android.os.Bundle;

import com.google.firebase.database.Query;

/**
 * Created by albertochiappa on 09/12/16.
 */

public interface SearchCriterion {
    Query performSearch(String extraInfo);
}