package com.example.kishanthprab.placehook.Utility;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.R;

import java.util.Calendar;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;


public class TimePickerDialogFragment extends TimeDurationPickerDialogFragment {


        @Override
        protected long getInitialDuration() {
            return 0;
        }


        @Override
        protected int setTimeUnits() {
            return TimeDurationPicker.HH_MM;
        }



        @Override
        public void onDurationSet(TimeDurationPicker view, long duration) {
            //DurationToast.show(getActivity(), duration);

            Toast.makeText(getActivity(), "Time picker showed:"+duration , Toast.LENGTH_SHORT).show();
        }

}


