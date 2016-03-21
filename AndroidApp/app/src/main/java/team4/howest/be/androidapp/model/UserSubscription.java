package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederic on 1/01/2016.
 */
public class UserSubscription {
    @SerializedName("type")
    @Expose
    private Integer type;

    @SerializedName("typeName")
    @Expose
    private String typeName;

    @SerializedName("name")
    @Expose
    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
