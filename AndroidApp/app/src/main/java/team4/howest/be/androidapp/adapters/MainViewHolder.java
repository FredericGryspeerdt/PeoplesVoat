package team4.howest.be.androidapp.adapters;

import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import team4.howest.be.androidapp.R;

/**
 * Created by Frederic on 19/12/2015.
 */
public class MainViewHolder extends RecyclerView.ViewHolder {

    public CardView cv;
    public TextView tvTitle;
    public TextView tvUser;
    public TextView tvVoteCount;
    public TextView tvTime;
    public TextView tvCommentCount;

    public ImageView imgUpvote;
    public ImageView imgDownVote;
    public ImageView imgPopupMenu;
    public ImageView imgUser;
    public ImageView imgComments;

    public ProgressBar progContent;

    public MainViewHolder(View itemView) {
        super(itemView);

        cv = (CardView)itemView.findViewById(R.id.card_submission);
        tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
        tvUser = (TextView) itemView.findViewById(R.id.tvUser);
        tvVoteCount = (TextView) itemView.findViewById(R.id.tvVoteCount);
        tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);

        imgUpvote = (ImageView) itemView.findViewById(R.id.imgUpvote);
        imgDownVote = (ImageView) itemView.findViewById(R.id.imgDownVote);
        imgPopupMenu = (ImageView) itemView.findViewById(R.id.imgPopupMenu);
        imgUser = (ImageView) itemView.findViewById(R.id.imgUser);
        imgComments = (ImageView) itemView.findViewById(R.id.imgComments);

        progContent = (ProgressBar) itemView.findViewById(R.id.progressBarContent);

    }
}
