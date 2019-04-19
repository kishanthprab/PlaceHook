package com.example.kishanthprab.placehook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Recycler.RecyclerAdapter;
import com.example.kishanthprab.placehook.Recycler.ReviewRecyclerListItem;

import java.util.ArrayList;
import java.util.List;


public class PlaceDetailsFragment extends Fragment implements View.OnClickListener {

    private Callback callback;

    ImageButton close, navigate;
    TextView txt_placeName, txt_totRating, txt_distance, txt_address, txt_numOfReviews;
    TabLayout tabLayout;

    RecyclerView review_recyclerView;
    RecyclerAdapter review_recyclerAdapter;

    private List<ReviewRecyclerListItem> reviewsArrayList;


    public static PlaceDetailsFragment newInstance() {

        return new PlaceDetailsFragment();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
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


        //recycler view
        review_recyclerView = (RecyclerView) view.findViewById(R.id.disc_recyclerView);
        review_recyclerView.setHasFixedSize(true);
        review_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        reviewsArrayList = new ArrayList<>();
        reviewsArrayList.clear();

        //review_recyclerAdapter = new RecyclerAdapter(reviewsArrayList, getActivity());
        review_recyclerView.setAdapter(review_recyclerAdapter);


        callback.onActionCLickForAllViews(txt_placeName, txt_totRating, txt_distance, txt_address, txt_numOfReviews);

        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.plcDetails_dialog_Close:
                //dismiss();
                getChildFragmentManager().beginTransaction().remove(this);
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

