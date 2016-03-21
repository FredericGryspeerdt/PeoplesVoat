
package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vote {

    @SerializedName("recordedValue")
    @Expose
    private Object recordedValue;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("resultName")
    @Expose
    private String resultName;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     *
     * @return
     *     The recordedValue
     */
    public Object getRecordedValue() {
        return recordedValue;
    }

    /**
     *
     * @param recordedValue
     *     The recordedValue
     */
    public void setRecordedValue(Object recordedValue) {
        this.recordedValue = recordedValue;
    }

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
     *     The result
     */
    public Integer getResult() {
        return result;
    }

    /**
     *
     * @param result
     *     The result
     */
    public void setResult(Integer result) {
        this.result = result;
    }

    /**
     *
     * @return
     *     The resultName
     */
    public String getResultName() {
        return resultName;
    }

    /**
     *
     * @param resultName
     *     The resultName
     */
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    /**
     *
     * @return
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     *     The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}