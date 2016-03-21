package team4.howest.be.androidapp.oldcode;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import team4.howest.be.androidapp.adapters.LinkPostViewHolder;
import team4.howest.be.androidapp.opengraph.OpenGraph;

/**
 * Created by Frederic on 20/12/2015.
 */
public class OpenGraphThumbnailLoader extends android.support.v4.content.AsyncTaskLoader<String> {

    String url;

    public OpenGraphThumbnailLoader(Context context, String url) {
        super(context);
        this.url = url;
    }


    @Override
    public String loadInBackground() {
        OpenGraph openGraph = null;

        try {
            openGraph = new OpenGraph(url,true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (openGraph != null && openGraph.getContent("image") != null){
            return openGraph.getContent("image");  //url naar image gevonden
        }

        return null; //geen url naar image gevonden
    }
}
