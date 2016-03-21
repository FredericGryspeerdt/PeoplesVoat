package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Frederic on 1/01/2016.
 */
public class UserSubscriptionsResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("data")
    @Expose
    private ArrayList<UserSubscription> data;

    @SerializedName("error")
    @Expose
    private ErrorInfo error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ArrayList<UserSubscription> getData() {
        return data;
    }

    public void setData(ArrayList<UserSubscription> data) {
        this.data = data;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }
}
