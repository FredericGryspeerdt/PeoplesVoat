package team4.howest.be.androidapp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import team4.howest.be.androidapp.VoatApplication;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;


/**
 * Created by anthony on 15.18.10.
 */
public class VoatClient {
    private static final String BASE_URL = "https://fakevout.azurewebsites.net/api/";
    private VoatApiService apiService;

    public VoatClient()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .disableHtmlEscaping()
                .create();

        //.setClient(new MockClient())
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Voat-ApiKey", VoatApiConstants.VOAT_APIKEY);
                        request.addHeader("Authorization", "Bearer" + " " +
                                ((VoatApplication) VoatApplication.getAppContext()).getBearer());
                    }
                })
                .setConverter(new GsonConverter(gson))
                .build();

        apiService = restAdapter.create(VoatApiService.class);
    }

    public VoatApiService getApiService()
    {
        return apiService;
    }
}
