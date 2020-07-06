package ch.epfl.sweng.tutosaurus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.Query;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.SearchFactory.SearchCriterion;
import ch.epfl.sweng.tutosaurus.SearchFactory.SearchCriterionFactory;
import ch.epfl.sweng.tutosaurus.adapter.FirebaseTutorAdapter;
import ch.epfl.sweng.tutosaurus.model.User;

/**
 * An activity where we diplay a list of tutors that match a certain criterion.
 */
public class FindTutorResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tutor_result);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String methodToCall = extras.getString("METHOD_TO_CALL");
        ListView tutorList = (ListView) findViewById(R.id.tutorList);

        SearchCriterion searchCriterion = new SearchCriterionFactory().findSearchCriterionNamed(methodToCall);
        Query ref = searchCriterion.performSearch(extras.getString("EXTRA_INFO"));
        FirebaseTutorAdapter adapter = new FirebaseTutorAdapter(this, User.class, R.layout.listview_tutor_row, ref);
        tutorList.setAdapter(adapter);
    }


}

