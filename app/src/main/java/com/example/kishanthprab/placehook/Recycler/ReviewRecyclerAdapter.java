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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {

    private List<ReviewRecyclerListItem> Review_RecyclerListItem;
    private Context context;

    public ReviewRecyclerAdapter(List<com.example.kishanthprab.placehook.Recycler.ReviewRecyclerListItem> recyclerListItem, Context context) {
        this.Review_RecyclerListItem = recyclerListItem;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        ReviewRecyclerListItem listItem = Review_RecyclerListItem.get(i);

        viewHolder.authorName.setText(listItem.getAuthorName());
        viewHolder.givenRating.setText(listItem.getGivenRating() + "/5");
        viewHolder.reviewText.setText(listItem.getReview_text());
        viewHolder.relativeTime.setText(listItem.getRelativeTime());

        viewHolder.profileImage.setImageBitmap(Functions.getBitmapFromURL(listItem.getPhotoUrl()));

        if (listItem.getReviewIconType().equals("google")){

            viewHolder.reviewTypeIcon.setImageResource(R.drawable.google);

        }else {

            viewHolder.reviewTypeIcon.setImageResource(R.drawable.applogo);
        }

        double rat = listItem.getGivenRating();
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
        return Review_RecyclerListItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView authorName;
        public TextView givenRating;
        public TextView relativeTime;
        public TextView reviewText;


        public ImageView roundDot1;
        public ImageView roundDot2;
        public ImageView roundDot3;
        public ImageView roundDot4;
        public ImageView roundDot5;

        public CircleImageView profileImage;
        public ImageView reviewTypeIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorName = (TextView) itemView.findViewById(R.id.rev_authorName);
            givenRating = (TextView) itemView.findViewById(R.id.rev_givenRating);
            relativeTime = (TextView) itemView.findViewById(R.id.rev_relativeTime);
            reviewText = (TextView) itemView.findViewById(R.id.rev_review_text);

            roundDot1 = (ImageView) itemView.findViewById(R.id.rev_round1);
            roundDot2 = (ImageView) itemView.findViewById(R.id.rev_round2);
            roundDot3 = (ImageView) itemView.findViewById(R.id.rev_round3);
            roundDot4 = (ImageView) itemView.findViewById(R.id.rev_round4);
            roundDot5 = (ImageView) itemView.findViewById(R.id.rev_round5);

            profileImage = (CircleImageView) itemView.findViewById(R.id.rev_profileImage);
            reviewTypeIcon = (ImageView) itemView.findViewById(R.id.rev_imgV_ReviewIcon);
        }
    }
}
