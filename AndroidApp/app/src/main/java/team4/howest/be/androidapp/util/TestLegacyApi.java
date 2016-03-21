package team4.howest.be.androidapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.service.VoatLegacyApiService;
import team4.howest.be.androidapp.service.VoatLegacyClient;

/**
 * Created by Frederic on 16/11/2015.
 */
public class TestLegacyApi {

    public static void main(String... args) throws IOException {

        // Create a very simple REST adapter which points the GitHub API endpoint.
        VoatLegacyClient client = new VoatLegacyClient();


        VoatLegacyApiService voatLegacyApiService = client.getApiService();

        voatLegacyApiService.getDefaultSubverses(new Callback<DefaultSubverseResponse>() {
            @Override
            public void success(DefaultSubverseResponse defaultSubverseResponse, Response response) {
                for(String subverse : defaultSubverseResponse.getDefaultSubverses()){
                    System.out.println(subverse);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });



    }
}
