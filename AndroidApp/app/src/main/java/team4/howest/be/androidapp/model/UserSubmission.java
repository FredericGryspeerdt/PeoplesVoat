package team4.howest.be.androidapp.model;

/**
 * Created by Frederic on 21/12/2015.
 */
public class UserSubmission {
    private String title;
    private Boolean nsfw;
    private Boolean anon;
    private String url;
    private String content;

    public UserSubmission() {
    }

    public UserSubmission(String title, Boolean nsfw, Boolean anon, String url, String content) {
        this.title = title;
        this.nsfw = nsfw;
        this.anon = anon;
        this.url = url;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getNsfw() {
        return nsfw;
    }

    public void setNsfw(Boolean nsfw) {
        this.nsfw = nsfw;
    }

    public Boolean getAnon() {
        return anon;
    }

    public void setAnon(Boolean anon) {
        this.anon = anon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
