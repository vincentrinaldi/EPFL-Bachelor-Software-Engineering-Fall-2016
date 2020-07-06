package ch.epfl.sweng.tutosaurus.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import ch.epfl.sweng.tutosaurus.R;

import static android.app.DatePickerDialog.OnDateSetListener;

/**
 * A dialog fragment whose purpose is to pick a date.
 */
public class DatePickerFragment extends DialogFragment
        implements OnDateSetListener {

    private int meetingYear;
    private int meetingMonth;
    private int meetingDay;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        meetingYear = year;
        meetingDay = dayOfMonth;
        meetingMonth = month;

        TextView dateView = (TextView) getActivity().findViewById(R.id.dateView);
        dateView.setVisibility( View.VISIBLE);
        String date = getDate();
        dateView.setText("  " + date);

    }


    public String getDate() {
        return meetingYear + ", " + meetingMonth + "/" + meetingDay;
    }


    public int getMeetingYear() {
        return meetingYear;
    }


    public int getMeetingMonth() {
        return meetingMonth;
    }


    public int getMeetingDay() {
        return meetingDay;
    }
}
