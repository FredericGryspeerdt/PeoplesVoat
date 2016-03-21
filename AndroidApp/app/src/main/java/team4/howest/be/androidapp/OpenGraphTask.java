package team4.howest.be.androidapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import team4.howest.be.androidapp.adapters.LinkPostViewHolder;
import team4.howest.be.androidapp.opengraph.OpenGraph;

/**
 * Created by Frederic on 18/12/2015.
 */
public class OpenGraphTask extends AsyncTask<Void,Integer, OpenGraph> {

    String url;
    LinkPostViewHolder submissionViewHolder;
    Context context;

    public OpenGraphTask(String url,LinkPostViewHolder submissionViewHolder, Context context) {
        this.url = url;
        this.submissionViewHolder = submissionViewHolder;
        this.context = context;
    }

    @Override
    protected OpenGraph doInBackground(Void... params) {
        OpenGraph openGraph = null;

        try {
            openGraph = new OpenGraph(url,true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return openGraph;
    };

    @Override
    protected void onPostExecute(OpenGraph openGraph) {

        try {
            if (openGraph != null && openGraph.getContent("image") != null){

                Picasso.with(context)
                        .load(openGraph.getContent("image"))
                        .fit()
                        .centerCrop()
                        .into(submissionViewHolder.imgThumbnail, new Callback() {
                            @Override
                            public void onSuccess() {
                                //toch nog een thumbnail gevonden --> progress bar mag weg
                                submissionViewHolder.progContent.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                //geen thumbnail meer gevonden --> progress bar weg en vervang door place holder img

                                submissionViewHolder.progContent.setVisibility(View.GONE);
                                submissionViewHolder.imgThumbnail.setImageResource(R.mipmap.ic_launcher);
                            }
                        });
            } else {
                //geen thumbnail meer gevonden --> progress bar weg en vervang door place holder img

                submissionViewHolder.progContent.setVisibility(View.GONE);
                submissionViewHolder.imgThumbnail.setImageResource(R.mipmap.ic_launcher);
            }
        } catch (Exception e){

        }

    }
}
