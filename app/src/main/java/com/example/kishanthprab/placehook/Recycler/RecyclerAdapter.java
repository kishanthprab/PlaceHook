package com.example.kishanthprab.placehook.Recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<RecyclerListItem> RecyclerListItem;
    private Context context;

    public RecyclerAdapter(List<com.example.kishanthprab.placehook.Recycler.RecyclerListItem> recyclerListItem, Context context) {
        RecyclerListItem = recyclerListItem;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.discover_feed, viewGroup, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        RecyclerListItem listItem = RecyclerListItem.get(i);

        viewHolder.placName.setText(listItem.getPlaceName());
        viewHolder.rating.setText(listItem.getRating() + "/5");
        viewHolder.dist.setText("~ " + listItem.getDist() + "KM NEARBY");

        String src = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                listItem.getPlacePhotos()[0].getPhoto_reference() +
                "&key=" +
                R.string.google_maps_key;


        //sample url = "https://wallpaper-gallery.net/images/image/image-13.jpg"
        Picasso.get()
                .load("https://wallpaper-gallery.net/images/image/image-13.jpg")
                .into(viewHolder.imgView);




        //viewHolder.imgView.setImageBitmap(Functions.getImageData());

        double rat = (double) listItem.getRating();
        //setroundColor(0.5, listItem.getRating(), viewHolder);

        double j = 0.5;
        if (rat < j) {

        } else if (rat < j + 1) {
            viewHolder.roundDot1.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot2.setImageResource(R.drawable.rounddot);
            viewHolder.roundDot3.setImageResource(R.drawable.rounddot);
            viewHolder.roundDot4.setImageResource(R.drawable.rounddot);
            viewHolder.roundDot5.setImageResource(R.drawable.rounddot);

        } else if (rat < j + 2) {

            viewHolder.roundDot1.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot2.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot3.setImageResource(R.drawable.rounddot);
            viewHolder.roundDot4.setImageResource(R.drawable.rounddot);
            viewHolder.roundDot5.setImageResource(R.drawable.rounddot);
        } else if (rat < j + 3) {
            viewHolder.roundDot1.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot2.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot3.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot4.setImageResource(R.drawable.rounddot);
            viewHolder.roundDot5.setImageResource(R.drawable.rounddot);

        } else if (rat < j + 4) {
            viewHolder.roundDot1.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot2.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot3.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot4.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot5.setImageResource(R.drawable.rounddot);

        } else {
            viewHolder.roundDot1.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot2.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot3.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot4.setImageResource(R.drawable.rounddotopposite);
            viewHolder.roundDot5.setImageResource(R.drawable.rounddotopposite);


        }


    }

    public void setroundColor(double i, double rat, ViewHolder viewHolder) {


        if (rat >= i) {

            viewHolder.roundDot1.setImageResource(R.drawable.rounddotopposite);

            if (rat >= i + 1) {

                viewHolder.roundDot2.setImageResource(R.drawable.rounddotopposite);

                if (rat >= i + 2) {

                    viewHolder.roundDot3.setImageResource(R.drawable.rounddotopposite);

                    if (rat >= i + 3) {

                        viewHolder.roundDot4.setImageResource(R.drawable.rounddotopposite);

                        if (rat >= i + 4) {

                            viewHolder.roundDot5.setImageResource(R.drawable.rounddotopposite);

                        }
                    }
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return RecyclerListItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView placName;
        public TextView rating;
        public TextView dist;
        public ImageView roundDot1;
        public ImageView roundDot2;
        public ImageView roundDot3;
        public ImageView roundDot4;
        public ImageView roundDot5;

        public ImageView imgView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placName = (TextView) itemView.findViewById(R.id.disc_name);
            rating = (TextView) itemView.findViewById(R.id.disc_rating);
            dist = (TextView) itemView.findViewById(R.id.disc_dist);
            roundDot1 = (ImageView) itemView.findViewById(R.id.disc_round1);
            roundDot2 = (ImageView) itemView.findViewById(R.id.disc_round2);
            roundDot3 = (ImageView) itemView.findViewById(R.id.disc_round3);
            roundDot4 = (ImageView) itemView.findViewById(R.id.disc_round4);
            roundDot5 = (ImageView) itemView.findViewById(R.id.disc_round5);

            imgView = (ImageView) itemView.findViewById(R.id.disc_image);
        }
    }
}
