package team4.howest.be.androidapp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import team4.howest.be.androidapp.VoatApplication;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;

/**
 * Created by Frederic on 16/11/2015.
 */
public class VoatLegacyClient {

    //private static final String BASE_URL_LEGACY_API = "http://voat.co/"; --> werkt niet (geen trusted certificate)
    private static final String BASE_URL_LEGACY_API = "http://fakevout.azurewebsites.net/";
    private VoatLegacyApiService apiService;

    public VoatLegacyClient()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        //.setClient(new MockClient())
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Voat-ApiKey", VoatApiConstants.VOAT_APIKEY);
                        request.addHeader("Authorization", "Bearer" + " " +
                                ((VoatApplication) VoatApplication.getAppContext()).getBearer());
                    }
                })
                .setEndpoint(BASE_URL_LEGACY_API)
                //.setConverter(new GsonConverter(gson))
                .setConverter(new GsonConverter(new GsonBuilder() //--> hebben custom converter nodig want de root van ingeladen JSON is een array
                        .registerTypeAdapter(DefaultSubverseResponse.class,
                               new DefaultSubverseResponseDeserializerJson())
                        .create()))
                .build();

        apiService = restAdapter.create(VoatLegacyApiService.class);
    }

    public VoatLegacyApiService getApiService()
    {
        return apiService;
    }
}
