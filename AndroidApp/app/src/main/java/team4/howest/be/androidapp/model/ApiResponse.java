package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederic on 3/01/2016.
 */
public class ApiResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("error")
    @Expose
    private ErrorInfo error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }
}
