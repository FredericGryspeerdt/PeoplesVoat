package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederic on 15/11/2015.
 */
public class ReplyResponse {
    public Boolean success;

    @SerializedName("data")
    public Comment comment;

    @SerializedName("error")
    private ErrorInfo error;
}
