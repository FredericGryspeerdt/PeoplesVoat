package team4.howest.be.androidapp.util;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.media.Image;
import android.os.AsyncTask;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.opengraph.OpenGraph;

/**
 * Created by Frederic on 24/11/2015.
 */
public class BindingUtils {



    @BindingAdapter("bind:imageUrl")
    public static void loadImage(final ImageView view, String url) {

        //parent layout ophalen van 'progressBarThumbnail'
        final RelativeLayout layout = (RelativeLayout)((ViewGroup) view.getParent());
        //'progressBarThumbnail' ophalen
        final ProgressBar prog = (ProgressBar) layout.getChildAt(1);
        //tonen dat de img bezig is met laden
        prog.setVisibility(View.VISIBLE);

        view.setVisibility(View.VISIBLE);

            Picasso.with(view.getContext())
                    .load(url)
                    .into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            //img is geladen --> progressbar mag verdwijnen
                            prog.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            //img kan niet geladen worden vanuit de url --> place holder
                            view.setImageResource(R.mipmap.ic_launcher);
                            //view.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);


                        }
                    });
        }

    @BindingAdapter("bind:showLayout")
    public static void showLayout(final RelativeLayout layout, Integer type){
        //standaard zichtbaar
        //layout.setVisibility(View.VISIBLE);

        //child views ophalen
        ImageView imgThumbnail = (ImageView) layout.getChildAt(0);
        ProgressBar progContent = (ProgressBar) layout.getChildAt(1);
        TextView tvContent = (TextView) layout.getChildAt(2);

        //standaard onzichtbaar zetten
        imgThumbnail.setVisibility(View.GONE);
        tvContent.setVisibility(View.GONE);

        switch (type){
            case 1: //self post
                //layout.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                progContent.setVisibility(View.GONE);
                break;
            case 2: //link post
                //layout.setVisibility(View.VISIBLE);
                imgThumbnail.setVisibility(View.VISIBLE);
                break;

        }
    }


}
