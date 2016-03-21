package team4.howest.be.androidapp.model;

import android.databinding.ObservableField;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anthony on 15.18.10.
 */
public class Comment implements Serializable{
    private Integer id;
    private Integer parentID;
    private Integer submissionID;
    private String subverse;
    private Date date;
    private Object lastEditDate;
    private Integer upVotes;
    private Integer downVotes;

    public ObservableField<Boolean> getCollapsed() {return collapsed;}
    public void setCollapsed(Boolean isCollapsed) {this.collapsed.set(isCollapsed);}
    private ObservableField<Boolean> collapsed;

    public ObservableField<Boolean> getHidden() {
        return hidden;
    }
    public void setHidden(Boolean isHidden) {
        this.hidden.set(isHidden);
    }
    private ObservableField<Boolean> hidden;

    private String userName;
    private Integer childCount;
    private Integer level;
    private String content;
    private String formattedContent;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private List<Comment> data;

    public Comment(){
        collapsed = new ObservableField<>();
        hidden = new ObservableField<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return id.equals(comment.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     *

     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The parentID
     */
    public Integer getParentID() {
        return parentID;
    }

    /**
     *
     * @param parentID
     * The parentID
     */
    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }



    /**
     *
     * @return
     * The submissionID
     */
    public Integer getSubmissionID() {
        return submissionID;
    }

    /**
     *
     * @param submissionID
     * The submissionID
     */
    public void setSubmissionID(Integer submissionID) {
        this.submissionID = submissionID;
    }

    /**
     *
     * @return
     * The subverse
     */
    public String getSubverse() {
        return subverse;
    }

    /**
     *
     * @param subverse
     * The subverse
     */
    public void setSubverse(String subverse) {
        this.subverse = subverse;
    }

    /**
     *
     * @return
     * The date
     */
    public Date getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The lastEditDate
     */
    public Object getLastEditDate() {
        return lastEditDate;
    }

    /**
     *
     * @param lastEditDate
     * The lastEditDate
     */
    public void setLastEditDate(Object lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    /**
     *
     * @return
     * The upVotes
     */
    public Integer getUpVotes() {
        return upVotes;
    }

    /**
     *
     * @param upVotes
     * The upVotes
     */
    public void setUpVotes(Integer upVotes) {
        this.upVotes = upVotes;
    }

    /**
     *
     * @return
     * The downVotes
     */
    public Integer getDownVotes() {
        return downVotes;
    }

    /**
     *
     * @param downVotes
     * The downVotes
     */
    public void setDownVotes(Integer downVotes) {
        this.downVotes = downVotes;
    }

    /**
     *
     * @return
     * The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName
     * The userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return
     * The childCount
     */
    public Integer getChildCount() {
        if(childCount!=null)
            return childCount;
        else
            return 0;
    }

    /**
     *
     * @param childCount
     * The childCount
     */
    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    /**
     *
     * @return
     * The level
     */
    public Integer getLevel() {
        if(level!=null)
            return level;
        else
            return 0;
    }

    /**
     *
     * @param level
     * The level
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     * The formattedContent
     */
    public String getFormattedContent() {
        return formattedContent;
    }

    /**
     *
     * @param formattedContent
     * The formattedContent
     */
    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return getContent();
    }
}
