package team4.howest.be.androidapp.service;

import retrofit.Callback;
import retrofit.http.GET;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;

/**
 * Created by Frederic on 16/11/2015.
 */
public interface VoatLegacyApiService {
    @GET("/api/defaultsubverses")
    void getDefaultSubverses(Callback<DefaultSubverseResponse> callback);

    @GET("/api/top200subverses")
    void getTop200Subverses(Callback<DefaultSubverseResponse> callback);
}
