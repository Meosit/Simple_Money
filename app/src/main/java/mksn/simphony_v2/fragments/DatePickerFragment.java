package mksn.simphony_v2.fragments;

/**
 * Created by Mike on 11.11.2015.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import mksn.simphony_v2.R;

/**
 * Created by jahid on 12/10/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        EditText todayDate = (EditText) getActivity().findViewById(R.id.date_container);
        String[] parseDate = todayDate.getText().toString().split("\\.");
        int year = Integer.parseInt(parseDate[2]);
        int month = Integer.parseInt(parseDate[1]);
        month--;
        int day = Integer.parseInt(parseDate[0]);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        StringBuilder resulsSB = new StringBuilder("");
        EditText result = (EditText) getActivity().findViewById(R.id.date_container);
        if (view.getDayOfMonth() < 10) {
            resulsSB.append("0").append(view.getDayOfMonth());
        } else {
            resulsSB.append(view.getDayOfMonth());
        }
        resulsSB.append(".");
        if (view.getMonth() + 1 < 10) {
            resulsSB.append("0").append(view.getMonth() + 1);
        } else {
            resulsSB.append(view.getMonth() + 1);
        }
        resulsSB.append(".");
        resulsSB.append(view.getYear());
        result.setText(resulsSB.toString());
    }
}