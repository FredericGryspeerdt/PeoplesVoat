
package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoteResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Vote data;

    /**
     *
     * @return
     *     The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     *     The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     *     The data
     */
    public Vote getData() {
        return data;
    }

    /**
     *
     * @param vote
     *     The vote
     */
    public void setData(Vote vote) {
        this.data = vote;
    }

}