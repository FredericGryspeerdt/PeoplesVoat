package team4.howest.be.androidapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Frederic on 15/11/2015.
 */
public class SubmissionResponse {
    public Boolean success;

    @SerializedName("data")
    public Submission submission; //moet zelfde waarde zijn als in json tenzij je in @SerializedName aangeeft hoe het in
    //binnenkomende JSON response heet
}
