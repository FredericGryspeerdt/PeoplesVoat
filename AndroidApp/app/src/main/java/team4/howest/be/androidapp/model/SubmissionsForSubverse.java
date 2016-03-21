package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Frederic on 15/11/2015.
 */
public class SubmissionsForSubverse {
    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("data")
    private List<Submission> submissions; //moet zelfde waarde zijn als in json tenzij je in @SerializedName aangeeft hoe het in
    //binnenkomende JSON response heet

    @SerializedName("error")
    @Expose
    private ErrorInfo error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }
}
