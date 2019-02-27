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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.R;

import java.util.HashMap;


public class FilterDialog extends DialogFragment implements View.OnClickListener {

    private Callback callback;

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
                SharedPreferences sp = getActivity().getSharedPreferences("com.example.kishanthprab.placehook",Context.MODE_PRIVATE);
                sp.edit().putString("key","value").apply();
                CheckBox checkBox = (CheckBox)getView().findViewById(R.id.checkBox1);

                Toast.makeText(getActivity(), "checked "+checkBox.isChecked(), Toast.LENGTH_SHORT).show();

                callback.onActionClick("Whatever");

                dismiss();
                break;

        }

    }
private HashMap<String,Boolean> checkBox(SharedPreferences sharedPreferences){

        HashMap<String,Boolean> map = new HashMap<String, Boolean>();

    CheckBox checkBox = (CheckBox)getView().findViewById(R.id.checkBox1);


return map;
}

    public interface Callback {

        void onActionClick(String name);

    }

}

