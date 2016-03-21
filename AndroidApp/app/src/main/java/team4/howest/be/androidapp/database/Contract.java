package team4.howest.be.androidapp.database;

import android.provider.BaseColumns;

/**
 * Created by Srg on 28/10/2015.
 */
public class Contract {
    public static final String DATABASE_NAME = "voatclient.db";
    public static final String ACCOUNT_TYPE = "team4.howest.be.androidapp.authentication";

    public static class Submissions implements BaseColumns {
        public static final String TABLE_NAME = "submissions";
        public static final String SUBMISSION_COMMENT_COUNT = "commentcount";
        public static final String SUBMISSION_DATE = "date";
        public static final String SUBMISSION_UPVOTES = "upvotes";
        public static final String SUBMISSION_DOWNVOTES = "downvotes";
        public static final String SUBMISSION_LAST_EDIT_DATE = "lasteditdate";
        public static final String SUBMISSION_USER_NAME = "username";
        public static final String SUBMISSION_SUBVERSE = "subverse";
        public static final String SUBMISSION_THUMBNAIL = "thumbnail";
        public static final String SUBMISSION_TITLE = "title";
        public static final String SUBMISSION_TYPE = "type";
        public static final String SUBMISSION_URL = "url";
        public static final String SUBMISSION_CONTENT = "content";
        public static final String SUBMISSION_FORMATTED_CONTENT = "formattedcontent";

        public static final String[] allSubmissionColumns = {
                _ID,
                SUBMISSION_COMMENT_COUNT,
                SUBMISSION_DATE,
                SUBMISSION_UPVOTES,
                SUBMISSION_DOWNVOTES,
                SUBMISSION_LAST_EDIT_DATE,
                SUBMISSION_USER_NAME,
                SUBMISSION_SUBVERSE,
                SUBMISSION_THUMBNAIL,
                SUBMISSION_TITLE,
                SUBMISSION_TYPE,
                SUBMISSION_URL,
                SUBMISSION_CONTENT,
                SUBMISSION_FORMATTED_CONTENT
        };
    }

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String USER_NAME = "username";
        public static final String USER_COMMENT_POINTS = "commentpoints";
        public static final String USER_COMMENT_VOTING = "commentvoting";
        public static final String USER_SUBMISSION_POINTS = "submissionpoints";
        public static final String USER_SUBMISSION_VOTING = "submissionvoting";

        public static final String[] allUserColumns = {

                USER_NAME,
                USER_COMMENT_POINTS,
                USER_COMMENT_VOTING,
                USER_SUBMISSION_POINTS,
                USER_SUBMISSION_VOTING,
        };
    }

    public static class Subverses implements BaseColumns {
        public static final String TABLE_NAME = "subverses";
        public static final String SUBVERSE_NAME = "name";
        public static final String SUBVERSE_TITLE = "title";
        public static final String SUBVERSE_DESCRIPTION = "description";
        public static final String SUBVERSE_CREATION_DATE = "creationdate";
        public static final String SUBVERSE_SUBSCRIBER_COUNT = "subscribercount";
        public static final String SUBVERSE_RATED_ADULT = "ratedadult";
        public static final String SUBVERSE_SIDEBAR = "sidebar";
        public static final String SUBVERSE_TYPE = "type";

        public static final String[] allSubverseColumns = {
                SUBVERSE_NAME,
                SUBVERSE_TITLE,
                SUBVERSE_DESCRIPTION,
                SUBVERSE_CREATION_DATE,
                SUBVERSE_SUBSCRIBER_COUNT,
                SUBVERSE_RATED_ADULT,
                SUBVERSE_SIDEBAR,
                SUBVERSE_TYPE
        };
    }

    public static class Comments implements BaseColumns{
        public static final String TABLE_NAME = "comments";
        public static final String COMMENT_PARENT_ID = "parentid";
        public static final String COMMENT_SUBMISSION_ID = "submissionid";
        public static final String COMMENT_SUBVERSE = "subverse";
        public static final String COMMENT_DATE = "date";
        public static final String COMMENT_LAST_EDIT_DATE = "lasteditdate";
        public static final String COMMENT_UPVOTES = "upvotes";
        public static final String COMMENT_DOWNVOTES = "downvotes";
        public static final String COMMENT_USER_NAME = "username";
        public static final String COMMENT_CHILD_COUNT = "childcount";
        public static final String COMMENT_LEVEL = "level";
        public static final String COMMENT_CONTENT = "content";
        public static final String COMMENT_FORMATTED_CONTENT = "formattedcontent";

        public static final String[] allCommentColumns = {
                _ID,
                COMMENT_PARENT_ID,
                COMMENT_SUBMISSION_ID,
                COMMENT_SUBVERSE,
                COMMENT_DATE,
                COMMENT_LAST_EDIT_DATE,
                COMMENT_UPVOTES,
                COMMENT_DOWNVOTES,
                COMMENT_USER_NAME,
                COMMENT_CHILD_COUNT,
                COMMENT_LEVEL,
                COMMENT_CONTENT,
                COMMENT_FORMATTED_CONTENT
        };
    }

}
