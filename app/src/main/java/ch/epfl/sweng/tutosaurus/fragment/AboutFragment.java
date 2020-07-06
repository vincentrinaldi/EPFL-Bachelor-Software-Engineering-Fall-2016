package ch.epfl.sweng.tutosaurus.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.activity.HomeScreenActivity;

/**
 * A fragment showing various legal information about the app.
 */
public class AboutFragment extends Fragment {

    private View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.about_layout, container, false);
        ((HomeScreenActivity) getActivity()).setActionBarTitle("About");
        return myView;
    }
}