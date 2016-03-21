package team4.howest.be.androidapp.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import team4.howest.be.androidapp.R;

/**
 * Created by Frederic on 3/01/2016.
 */
public class SubverseViewHolder extends RecyclerView.ViewHolder {

    public CardView cv;

    public TextView tvName;
    public TextView tvDate;
    public TextView tvDescription;
    public TextView tvSubscriberCount;


    public SubverseViewHolder(View itemView) {
        super(itemView);

        cv = (CardView) itemView.findViewById(R.id.card_subverse_info);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        tvSubscriberCount = (TextView) itemView.findViewById(R.id.tvSubscriberCount);
    }
}
