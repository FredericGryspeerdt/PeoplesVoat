package team4.howest.be.androidapp.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Srg on 7/11/2015.
 */
public class User implements Serializable {

    private String userName;
    private String registrationDate;
    private String bio;
    private String profilePicture;
    private HashMap<String,String> commentPoints;
    private HashMap<String,String> submissionPoints;
    private HashMap<String,String> commentVoting;
    private HashMap<String,String> submissionVoting;
    private String[][] badges;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }


    public String[][] getBadges() {
        return badges;
    }

    public void setBadges(String[][] badges) {
        this.badges = badges;
    }

    @Override
    public String toString() {
        return getUserName() + " : "+ getBio();
    }

    public HashMap<String, String> getCommentPoints() {
        return commentPoints;
    }

    public void setCommentPoints(HashMap<String, String> commentPoints) {
        this.commentPoints = commentPoints;
    }

    public HashMap<String, String> getSubmissionPoints() {
        return submissionPoints;
    }

    public void setSubmissionPoints(HashMap<String, String> submissionPoints) {
        this.submissionPoints = submissionPoints;
    }

    public HashMap<String, String> getCommentVoting() {
        return commentVoting;
    }

    public void setCommentVoting(HashMap<String, String> commentVoting) {
        this.commentVoting = commentVoting;
    }

    public HashMap<String, String> getSubmissionVoting() {
        return submissionVoting;
    }

    public void setSubmissionVoting(HashMap<String, String> submissionVoting) {
        this.submissionVoting = submissionVoting;
    }

    //Helper class om de data van VOAT-API op te vangen
    public class UserAPIResponse{

        private boolean success;
        private User data;

        @Override
        public String toString() {
            return getData().toString();
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public User getData() {
            return data;
        }

        public void setData(User data) {
            this.data = data;
        }
    }
}
