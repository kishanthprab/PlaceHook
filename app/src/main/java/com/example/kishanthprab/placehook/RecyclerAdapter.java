package com.example.kishanthprab.placehook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<RecyclerListItem> RecyclerListItem;
    private Context context;

    public RecyclerAdapter(List<com.example.kishanthprab.placehook.RecyclerListItem> recyclerListItem, Context context) {
        RecyclerListItem = recyclerListItem;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.discover_feed,viewGroup,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        RecyclerListItem listItem = RecyclerListItem.get(i);

        viewHolder.placName.setText(listItem.getPlaceName());
        viewHolder.rating.setText(listItem.getRating() +"/5");
        viewHolder.dist.setText("~ "+listItem.getDist()+"KM NEARBY");

    }

    @Override
    public int getItemCount() {
        return RecyclerListItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView placName;
        public TextView rating;
        public TextView dist;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placName = (TextView)itemView.findViewById(R.id.disc_name);
            rating = (TextView)itemView.findViewById(R.id.disc_rating);
            dist = (TextView)itemView.findViewById(R.id.disc_dist);
        }
    }
}
