package ch.epfl.sweng.tutosaurus.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.activity.FindTutorResultActivity;
import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;
import ch.epfl.sweng.tutosaurus.adapter.ClassicCourseAdapter;
import ch.epfl.sweng.tutosaurus.model.Course;
import ch.epfl.sweng.tutosaurus.model.FullCourseList;

/**
 * A fragment where the user can search for tutors according to a variety of criteria.
 */
public class FindTutorsFragment extends Fragment {
    private View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.find_tutors_layout, container, false);
        ((HomeScreenActivity) getActivity()).setActionBarTitle("Find Tutors");

        // Search by name listener
        Button searchByName = (Button) myView.findViewById(R.id.searchByName);
        searchByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FindTutorResultActivity.class);
                EditText nameToSearch=(EditText) getView().findViewById(R.id.nameToSearch);
                String name=nameToSearch.getText().toString();
                Bundle extras = new Bundle();
                extras.putString("EXTRA_INFO", name);
                extras.putString("METHOD_TO_CALL", "findTutorByName");
                intent.putExtras(extras);
                startActivity(intent);
                }
        });


        // Display search by name listener
        setDisplayByNameListener((Button) myView.findViewById(R.id.byName));


        // Display search by subject listener
        setDisplayBySubjectListener((Button) myView.findViewById(R.id.bySubject));

        // Populate list of subjects with search methods
        ArrayList<Course> courses = FullCourseList.getInstance().getListOfCourses();
        ClassicCourseAdapter courseAdapter = new ClassicCourseAdapter(  getActivity().getBaseContext(),
                                                                        R.layout.listview_course_row,
                                                                        courses);

        ListView courseList= (ListView) myView.findViewById(R.id.courseList);
        courseList.setAdapter(courseAdapter);

        // Show full list
        TextView showFullList = (TextView) myView.findViewById(R.id.showFullList);
        showFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FindTutorResultActivity.class);
                Bundle extras = new Bundle();
                extras.putString("METHOD_TO_CALL", "showFullList");
                extras.putString("EXTRA_INFO", "noExtraInfo");
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        Intent resumeIntent = new Intent(getActivity(), HomeScreenActivity.class);
        resumeIntent.setAction("OPEN_TAB_FIND_TUTORS");
        getActivity().setIntent(resumeIntent);

        return myView;
    }

    private void setDisplayByNameListener(final Button byNameButton) {
        byNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEverything();
                setDisplayBySubjectListener((Button) myView.findViewById(R.id.bySubject));
                LinearLayout nameLayout = (LinearLayout) getView().findViewById(R.id.nameLayout);
                nameLayout.setVisibility(View.VISIBLE);
                setShowFullListListener(byNameButton);
            }
        });
    }

    private void setDisplayBySubjectListener(final Button bySubjectButton) {
        bySubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEverything();
                setDisplayByNameListener((Button) myView.findViewById(R.id.byName));
                ListView subjectList = (ListView) getView().findViewById(R.id.courseList);
                subjectList.setVisibility(View.VISIBLE);
                setShowFullListListener(bySubjectButton);
            }
        });
    }


    private void setShowFullListListener(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEverything();
                myView.findViewById(R.id.showFullList).setVisibility(View.VISIBLE);
                setDisplayByNameListener((Button) myView.findViewById(R.id.byName));
                setDisplayBySubjectListener((Button) myView.findViewById(R.id.bySubject));

            }
        });
    }


    private void hideEverything(){
        myView.findViewById(R.id.courseList).setVisibility(View.GONE);
        myView.findViewById(R.id.nameLayout).setVisibility(View.GONE);
        myView.findViewById(R.id.showFullList).setVisibility(View.GONE);
    }
}
