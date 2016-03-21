package team4.howest.be.androidapp.adapters;

import android.view.View;
import android.widget.ImageView;

import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.adapters.MainViewHolder;

/**
 * Created by Frederic on 19/12/2015.
 */
public class LinkPostViewHolder extends MainViewHolder {

    public ImageView imgThumbnail;

    public LinkPostViewHolder(View itemView) {
        super(itemView);
        imgThumbnail =(ImageView) itemView.findViewById(R.id.imgThumbnailNew);
    }
}
