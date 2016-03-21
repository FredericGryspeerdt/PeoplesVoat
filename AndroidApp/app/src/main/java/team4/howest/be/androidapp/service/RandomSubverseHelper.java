package team4.howest.be.androidapp.service;

import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.view.MainActivity;

/**
 * Created by Srg on 2/01/2016.
 */
public class RandomSubverseHelper {

    private static ArrayList<String> listSubverses; //top 200 subverses

    public static void getRandomSubverse(final MainActivity mainActivity)
    {
        VoatLegacyClient voatLegacyClient = new VoatLegacyClient();

        VoatLegacyApiService voatLegacyApiService = voatLegacyClient.getApiService();

        voatLegacyApiService.getTop200Subverses(new Callback<DefaultSubverseResponse>() {
            @Override
            public void success(DefaultSubverseResponse defaultSubverseResponse, Response response) {
                listSubverses = new ArrayList<String>();
                listSubverses.addAll(defaultSubverseResponse.getDefaultSubverses());

                Random r = new Random();
                int index = (r.nextInt(listSubverses.size()-1)+1);
                String subverseInfo = listSubverses.get(index);
                String[] subverseInfo2 = subverseInfo.split(Pattern.quote(","));
                String subverse = subverseInfo2[0].split(Pattern.quote(" "))[1];

                mainActivity.adapter.add(subverse);
                mainActivity.adapter.notifyDataSetChanged();
                mainActivity.spinner.setSelection(mainActivity.adapter.getCount()-1, true);

                //sluit de drawer nav
                DrawerLayout drawer = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);
                drawer.closeDrawers();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}
