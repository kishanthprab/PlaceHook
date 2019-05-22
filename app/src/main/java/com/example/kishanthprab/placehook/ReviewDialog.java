package com.example.kishanthprab.placehook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DataObjects.UserReview;
import com.example.kishanthprab.placehook.Recycler.ReviewRecyclerAdapter;
import com.example.kishanthprab.placehook.Recycler.ReviewRecyclerListItem;
import com.example.kishanthprab.placehook.Utility.FireDBUtil;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.Functions_Itinerary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class ReviewDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Callback callback;
    private static final String TAG = "ReviewDialog";

    ImageButton close, save;

    TextView txt_placeName, txt_reviewText;
    ImageView imgv_place;

    AlertDialog alertDialog;

    Spinner review_ratingcount;

    static Context mContext = null;

    //public

    public static ReviewDialog newInstance(Context context) {

        mContext = context;
        return new ReviewDialog();
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
        View view = inflater.inflate(R.layout.dialog_review, container, false);

        close = (ImageButton) view.findViewById(R.id.reviewDialog_dialog_Close);
        save = (ImageButton) view.findViewById(R.id.reviewDialog_done);
        txt_placeName = (TextView) view.findViewById(R.id.reviewDialog_txt_plcName);
        txt_reviewText = (TextView) view.findViewById(R.id.reviewDialog_txt_reviewText);
        review_ratingcount = (Spinner) view.findViewById(R.id.reviewDialog_spinner_rating);
        imgv_place = (ImageView) view.findViewById(R.id.reviewDialog_image);

        alertDialog = Functions.spotsDialog(mContext);
        alertDialog.setTitle("Saving Review Please wait...");

        close.setOnClickListener(this);
        save.setOnClickListener(this);

        ArrayList<String> numbersList = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, numbersList);
        review_ratingcount.setAdapter(arrayAdapter);


        //callback functions
        callback.onActionClick(imgv_place, txt_placeName);


        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.reviewDialog_dialog_Close:
                dismiss();
                break;

            case R.id.reviewDialog_done:
                alertDialog.show();
                storeReviewtoFirebase();

                break;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.reviewDialog_spinner_rating:

                Toast.makeText(getActivity(), "Item clicked :" + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position));
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface Callback {

        void onActionClick(
                ImageView placeImage, TextView placeName
        );

    }

    //store values to firebase
    private void storeReviewtoFirebase() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reviewRef;

        if ((mAuth.getCurrentUser() != null) && (Functions_Itinerary.CurrentPlaceDetails != null)) {

            reviewRef = rootRef.child("Reviews")
                    .child(mAuth.getCurrentUser().getUid())
                    .child(Functions_Itinerary.CurrentPlaceDetails.getResult().getPlace_id());

            Calendar currentTime = Calendar.getInstance();

            //review details
            UserReview reviewObj = new UserReview();
            reviewObj.setUserId(mAuth.getCurrentUser().getUid());
            reviewObj.setPlaceId(Functions_Itinerary.CurrentPlaceDetails.getResult().getPlace_id());
            reviewObj.setPlaceName(Functions_Itinerary.CurrentPlaceDetails.getResult().getName());
            reviewObj.setAuthorName(Functions_Itinerary.userName);
            reviewObj.setLatitude(Functions_Itinerary.CurrentPlaceDetails.getResult().getGeometry().getLocation().getLat());
            reviewObj.setLongitude(Functions_Itinerary.CurrentPlaceDetails.getResult().getGeometry().getLocation().getLng());

            double rating = Double.parseDouble(review_ratingcount.getSelectedItem().toString()) / 2.0;

            reviewObj.setRating(String.valueOf(rating));
            reviewObj.setReviewText(txt_reviewText.getText().toString());
            reviewObj.setTime(currentTime.getTime().toString());
            reviewObj.setUnixTime(String.valueOf(currentTime.getTimeInMillis()));

            //store review details to firebase
            reviewRef.setValue(reviewObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    alertDialog.dismiss();

                    //read app reviews from firebase
                    DashboardActivity.retrieveAllReviewsFirebase();

                    Toast.makeText(getActivity(), "Review saved successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                    Toast.makeText(getActivity(), "Can not save review", Toast.LENGTH_SHORT).show();
                }
            });

            Log.d(TAG, "storeReviewtoFirebase: success");


        }

    }

}

