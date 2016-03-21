package team4.howest.be.androidapp.model;

import android.content.Context;
import android.view.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import team4.howest.be.androidapp.adapters.LinkPostViewHolder;
import team4.howest.be.androidapp.OpenGraphTask;

/**
 * Created by Frederic on 15/11/2015.
 */
public class Submission {

    private PrettyTime p = new PrettyTime();

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("commentCount")
    @Expose
    private Integer commentCount;

    @SerializedName("date")

    @Expose
    private Date date;
    @SerializedName("upVotes")
    @Expose
    private Integer upVotes;
    @SerializedName("downVotes")
    @Expose
    private Integer downVotes;
    @SerializedName("lastEditDate")
    @Expose
    private Object lastEditDate;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("subverse")
    @Expose
    private String subverse;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("content")
    @Expose
    private Object content;
    @SerializedName("formattedContent")
    @Expose
    private String formattedContent;



    //extra

    private Date prettyDate;

    private Integer voteCount;

    public void loadThumbnail(final String url, final LinkPostViewHolder viewHolder, final Context context){

        Picasso.with(context)
                .load(url)
                .fit()
                .centerCrop()
                .into(viewHolder.imgThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        //url linkt rechtstreeks naar een afbeelding
                        //img is geladen --> progressbar mag verdwijnen
                        viewHolder.progContent.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //img kan niet geladen worden vanuit de url (url verwijst niet rechtstreeks naar een afbeelding)
                        //--> probeer via OpenGraph toch een afbeelding op te halen van de site
                        OpenGraphTask task = new OpenGraphTask(url, viewHolder, context);
                        task.execute();
                    }
                });
    }

    public String getPrettyDate() {
        return p.format(this.getDate());
    }

    public void setPrettyDate(Date prettyDate) {
        this.prettyDate = prettyDate;
    }

    public Integer getVoteCount() {
        return this.upVotes - this.downVotes;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
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
     * The commentCount
     */
    public Integer getCommentCount() {
        return commentCount;
    }

    /**
     *
     * @param commentCount
     * The commentCount
     */
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
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
     * The views
     */
    public Integer getViews() {
        return views;
    }

    /**
     *
     * @param views
     * The views
     */
    public void setViews(Integer views) {
        this.views = views;
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
     * The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     *
     * @param thumbnail
     * The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The type
     */
    public Integer getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The content
     */
    public Object getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(Object content) {
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

}
