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
 * A fragment showing support information.
 */
public class HelpFragment extends Fragment {

    private View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.help_layout, container, false);
        ((HomeScreenActivity) getActivity()).setActionBarTitle("Help");

        return myView;
    }

}
