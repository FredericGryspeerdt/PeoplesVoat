package team4.howest.be.androidapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Srg on 28/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static DatabaseHelper INSTANCE;
    private static Object lock = new Object();

    public DatabaseHelper(Context context)
    {
        super(context, Contract.DATABASE_NAME, null, 1);
    }

    public static DatabaseHelper getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (lock) {
                if(INSTANCE == null) {
                    INSTANCE = new DatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return  INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //createSubverses(db);
        createSubmissions(db);
        createUser(db);
        //createComments(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createSubverses(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Contract.Subverses.TABLE_NAME + "("
                +   Contract.Subverses._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +   Contract.Subverses.SUBVERSE_NAME + " TEXT,"
                +   Contract.Subverses.SUBVERSE_TITLE + " TEXT,"
                +   Contract.Subverses.SUBVERSE_DESCRIPTION + " TEXT,"
                +   Contract.Subverses.SUBVERSE_CREATION_DATE + " TEXT,"
                +   Contract.Subverses.SUBVERSE_SUBSCRIBER_COUNT + " INTEGER,"
                +   Contract.Subverses.SUBVERSE_RATED_ADULT + " TEXT,"
                +   Contract.Subverses.SUBVERSE_SIDEBAR + " TEXT,"
                +   Contract.Subverses.SUBVERSE_TYPE + " TEXT"
                +");";

        db.execSQL(sql);
    }

    private void createSubmissions(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Contract.Submissions.TABLE_NAME + "("
                +   Contract.Submissions._ID + " INTEGER PRIMARY KEY,"
                +   Contract.Submissions.SUBMISSION_COMMENT_COUNT + " INTEGER,"
                +   Contract.Submissions.SUBMISSION_DATE + " TEXT,"
                +   Contract.Submissions.SUBMISSION_UPVOTES + " INTEGER,"
                +   Contract.Submissions.SUBMISSION_DOWNVOTES + " INTEGER,"
                +   Contract.Submissions.SUBMISSION_LAST_EDIT_DATE + " TEXT,"
                +   Contract.Submissions.SUBMISSION_USER_NAME + " TEXT,"
                +   Contract.Submissions.SUBMISSION_SUBVERSE + " TEXT,"
                +   Contract.Submissions.SUBMISSION_THUMBNAIL + " TEXT,"
                +   Contract.Submissions.SUBMISSION_TITLE + " TEXT,"
                +   Contract.Submissions.SUBMISSION_TYPE + " TEXT,"
                +   Contract.Submissions.SUBMISSION_URL + " TEXT,"
                +   Contract.Submissions.SUBMISSION_CONTENT + " TEXT,"
                +   Contract.Submissions.SUBMISSION_FORMATTED_CONTENT + " TEXT"
                +");";

        db.execSQL(sql);
    }

    private void createUser(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Contract.User.TABLE_NAME + "("
                +   Contract.User._ID + " INTEGER PRIMARY KEY,"
                +   Contract.User.USER_NAME + " TEXT,"
                +   Contract.User.USER_COMMENT_POINTS + " INTEGER,"
                +   Contract.User.USER_COMMENT_VOTING + " INTEGER,"
                +   Contract.User.USER_SUBMISSION_POINTS + " INTEGER,"
                +   Contract.User.USER_SUBMISSION_VOTING + " INTEGER"
                +");";

        db.execSQL(sql);
    }

    private void createComments(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Contract.Comments.TABLE_NAME + "("
                +   Contract.Comments._ID + " INTEGER PRIMARY KEY,"
                +   Contract.Comments.COMMENT_PARENT_ID + " INTEGER,"
                +   Contract.Comments.COMMENT_SUBMISSION_ID + " INTEGER,"
                +   Contract.Comments.COMMENT_SUBVERSE + " TEXT,"
                +   Contract.Comments.COMMENT_DATE + " TEXT,"
                +   Contract.Comments.COMMENT_LAST_EDIT_DATE + " TEXT,"
                +   Contract.Comments.COMMENT_UPVOTES + " INTEGER,"
                +   Contract.Comments.COMMENT_DOWNVOTES + " INTEGER,"
                +   Contract.Comments.COMMENT_USER_NAME + " TEXT,"
                +   Contract.Comments.COMMENT_CHILD_COUNT + " INTEGER,"
                +   Contract.Comments.COMMENT_LEVEL + " INTEGER,"
                +   Contract.Comments.COMMENT_CONTENT + " TEXT,"
                +   Contract.Comments.COMMENT_FORMATTED_CONTENT + " TEXT"
                +");";

        db.execSQL(sql);
    }
}
