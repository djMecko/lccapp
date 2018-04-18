package com.lcc.tyf.lcc.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lcc.tyf.lcc.DeliverInfoStatusActivity;

import java.util.Calendar;

/**
 * Created by max on 4/10/18.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public DeliverInfoStatusActivity deliverInfoStatusActivity;

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

    public void setDeliverInfoStatusActivity(DeliverInfoStatusActivity deliverInfoStatusActivity){
        this.deliverInfoStatusActivity = deliverInfoStatusActivity;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        EditDialogListener activity = (EditDialogListener) getActivity();
        activity.updateResult(year, month, day);
    }

    public interface EditDialogListener {
        void updateResult(int year, int month, int day);
    }

}
