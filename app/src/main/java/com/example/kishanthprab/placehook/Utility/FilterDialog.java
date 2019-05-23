package com.example.kishanthprab.placehook.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FilterDialog extends DialogFragment implements View.OnClickListener {

    private Callback callback;

    CheckBox checkBox_busStops, checkBox_restaurant, checkBox_atm,
            checkBox_cafe, checkBox_shopping_mall, checkBox_food;

    SeekBar seekBar;
    TextView seekbarText;

    List<CheckBox> checkBoxList;

    public static FilterDialog newInstance() {


        return new FilterDialog();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);
        ImageButton close = view.findViewById(R.id.fullscreen_dialog_close);
        TextView action = view.findViewById(R.id.fullscreen_dialog_action);

        checkBox_busStops = (CheckBox) view.findViewById(R.id.busStops);
        checkBox_restaurant= (CheckBox) view.findViewById(R.id.restaurant);
        checkBox_atm = (CheckBox) view.findViewById(R.id.atm);
        checkBox_cafe = (CheckBox) view.findViewById(R.id.cafe);
        checkBox_shopping_mall = (CheckBox) view.findViewById(R.id.shopping_mall);
        checkBox_food = (CheckBox) view.findViewById(R.id.food);

        seekBar = (SeekBar)view.findViewById(R.id.seekbar);
        seekbarText = (TextView) view.findViewById(R.id.seekbar_progressText);

        checkBoxList = new ArrayList<>();
        checkBoxList.add(checkBox_busStops);
        checkBoxList.add(checkBox_restaurant);
        checkBoxList.add(checkBox_atm);
        checkBoxList.add(checkBox_cafe);
        checkBoxList.add(checkBox_shopping_mall);
        checkBoxList.add(checkBox_food);


        seekBar.setMax(9500);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double d = (progress+500)/1000.0 ;
                String str = String.format("%1.2f", d);
                d= Double.valueOf(d);
                seekbarText.setText("Radius : " + d +" km");
                
                //Toast.makeText(getActivity(), "seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        close.setOnClickListener(this);
        action.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.fullscreen_dialog_close:
                dismiss();
                break;

            case R.id.fullscreen_dialog_action:

                int seebarValue = seekBar.getProgress() + 500;
                callback.onActionClick(checkBoxValues(checkBoxList),seebarValue);

                dismiss();
                break;

        }

    }

    private HashMap<String, Boolean> checkBoxValues(List<CheckBox> checkBoxList) {

        HashMap<String, Boolean> checkBoxValues = new HashMap<>();
        checkBoxValues.clear();

        for (CheckBox checkBox:checkBoxList){

            if (checkBox.isChecked()){

                checkBoxValues.put(checkBox.getTag().toString(), checkBox.isChecked());
            }

        }


        return checkBoxValues;
    }

    public interface Callback {

        void onActionClick(HashMap<String, Boolean> checkBoxValues,int seekbarValue);

    }

}

