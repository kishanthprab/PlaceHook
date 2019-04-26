package com.example.kishanthprab.placehook.Recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kishanthprab.placehook.R;

import java.util.List;

public class ItinBottomSheetRecyclerAdapter extends RecyclerView.Adapter<ItinBottomSheetRecyclerAdapter.ViewHolder> {

    private List<ItinBottomSheetRecyclerListItem> ItinBottomSheet_RecyclerListItem;
    private Context context;

    public ItinBottomSheetRecyclerAdapter(List<ItinBottomSheetRecyclerListItem> ListItem, Context context) {
        this.ItinBottomSheet_RecyclerListItem = ListItem;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itinerary_bottomsheet_recycleritem, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        ItinBottomSheetRecyclerListItem item = ItinBottomSheet_RecyclerListItem.get(i);


        viewHolder.placeNumber2.setText(item.getItin_placeNumber2());
        viewHolder.placeName2.setText(item.getItin_placeName2());

        viewHolder.placeDuration.setText(item.getItin_placeDuration());
        viewHolder.placeDistance.setText(item.getItin_placeDistance());

        viewHolder.placeNumber1.setText(item.getItin_placeNumber1());
        viewHolder.placeName1.setText(item.getItin_placeName1());



    }


    @Override
    public int getItemCount() {
        return ItinBottomSheet_RecyclerListItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView placeNumber1;
        TextView placeName1;
        TextView placeNumber2;
        TextView placeName2;
        TextView placeDuration;
        TextView placeDistance;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            placeNumber1 = (TextView) itemView.findViewById(R.id.itin_btmsheetRecycler_txt_plc1);
            placeName1 = (TextView) itemView.findViewById(R.id.itin_btmsheetRecycler_txt_plcName1);

            placeNumber2 = (TextView) itemView.findViewById(R.id.itin_btmsheetRecycler_txt_plc2);
            placeName2 = (TextView) itemView.findViewById(R.id.itin_btmsheetRecycler_txt_plcName2);

            placeDuration = (TextView) itemView.findViewById(R.id.itin_btmsheetRecycler_txt_time);
            placeDistance = (TextView) itemView.findViewById(R.id.itin_btmsheetRecycler_txt_dist);

        }
    }
}
