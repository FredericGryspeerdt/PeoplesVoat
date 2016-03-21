package team4.howest.be.androidapp.adapters;

import android.view.View;
import android.widget.TextView;

import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.adapters.MainViewHolder;

/**
 * Created by Frederic on 19/12/2015.
 */
public class SelfPostViewHolder extends MainViewHolder {
    public TextView tvContent;

    public SelfPostViewHolder(View itemView) {
        super(itemView);

        tvContent = (TextView) itemView.findViewById(R.id.tvContent);
    }
}
