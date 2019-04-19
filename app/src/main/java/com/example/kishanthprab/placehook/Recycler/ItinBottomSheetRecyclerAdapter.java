package com.example.kishanthprab.placehook.Recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Utility.Functions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItinBottomSheetRecyclerAdapter extends RecyclerView.Adapter<ItinBottomSheetRecyclerAdapter.ViewHolder> {

    private List<ReviewRecyclerListItem> BottomSheet_RecyclerListItem;
    private Context context;

    public ItinBottomSheetRecyclerAdapter(List<ReviewRecyclerListItem> ListItem, Context context) {
        this.BottomSheet_RecyclerListItem = ListItem;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bottomsheet_itinerary, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {




    }


    @Override
    public int getItemCount() {
        return BottomSheet_RecyclerListItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
