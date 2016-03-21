package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederic on 3/01/2016.
 */
public class SubverseInfoResponse {
    @SerializedName("success")
    @Expose
    public Boolean success;

    @SerializedName("data")
    @Expose
    public SubverseInfo data;

    @SerializedName("error")
    @Expose
    public ErrorInfo error;

}
