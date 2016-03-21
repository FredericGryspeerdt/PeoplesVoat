package team4.howest.be.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Frederic on 2/01/2016.
 */
public class SubverseInfo {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("creationDate")
    @Expose
    private Date creationDate;

    private String createdOn;

    @SerializedName("subscriberCount")
    @Expose
    private Integer subscriberCount;

    @SerializedName("ratedAdult")
    @Expose
    private Boolean ratedAdult;

    @SerializedName("sidebar")
    @Expose
    private String sidebar;

    @SerializedName("type")
    @Expose
    private String link;


    public SubverseInfo(String name, String description, Integer subscriberCount, String createdOn) {
        this.name = name;
        this.description = description;
        this.subscriberCount = subscriberCount;
        this.createdOn = createdOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(Integer subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getRatedAdult() {
        return ratedAdult;
    }

    public void setRatedAdult(Boolean ratedAdult) {
        this.ratedAdult = ratedAdult;
    }

    public String getSidebar() {
        return sidebar;
    }

    public void setSidebar(String sidebar) {
        this.sidebar = sidebar;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

