package com.example.kishanthprab.placehook.Utility;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.R;

import java.util.HashMap;


public class PlaceDetailsDialog extends DialogFragment implements View.OnClickListener {

    private Callback callback;

    ImageButton close, navigate;
    TextView txt_placeName, txt_totRating, txt_distance, txt_address, txt_numOfReviews;
    TabLayout tabLayout;

    public static PlaceDetailsDialog newInstance() {


        return new PlaceDetailsDialog();
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
        View view = inflater.inflate(R.layout.dialog_placedetails, container, false);

        close = (ImageButton) view.findViewById(R.id.plcDetails_dialog_Close);
        navigate = (ImageButton) view.findViewById(R.id.plcDetails_navigate);
        txt_placeName = (TextView) view.findViewById(R.id.plcDetails_txt_plcName);
        txt_totRating = (TextView) view.findViewById(R.id.plcDetails_totRating);
        txt_distance = (TextView) view.findViewById(R.id.plcDetails_dist);
        txt_address = (TextView) view.findViewById(R.id.plcDetails_address);
        txt_numOfReviews = (TextView) view.findViewById(R.id.plcDetails_NumOfReviews);
        tabLayout = (TabLayout) view.findViewById(R.id.plcDetails_tabLayout);


        close.setOnClickListener(this);
        navigate.setOnClickListener(this);


        callback.onActionCLickForAllViews(txt_placeName, txt_totRating, txt_distance, txt_address, txt_numOfReviews);

        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.plcDetails_dialog_Close:
                dismiss();
                break;

            case R.id.plcDetails_navigate:

                // callback.onActionClick("Whatever");
                //callback.onActionClick(txt_placeName);

                // dismiss();
                break;

        }

    }

    public interface Callback {

        //void onActionClick(String name);
        void onActionClick(TextView txtV);

        void onActionCLickForAllViews(
                TextView txt_placeName,
                TextView txt_totRating,
                TextView txt_distance,
                TextView txt_address,
                TextView txt_numOfReviews

        );

    }

}

