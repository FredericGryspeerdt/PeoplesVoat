package team4.howest.be.androidapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.model.User;


/**
 * Created by Srg on 28/10/2015.
 */
public class DataSource {

    private DatabaseHelper dbHelper;
    private List<Submission> savedSubmissions;

    public DataSource(Context context)
    {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long saveSubmission(Submission submission)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put(Contract.Submissions._ID, submission.getId());
        insertValues.put(Contract.Submissions.SUBMISSION_COMMENT_COUNT, submission.getCommentCount());
        insertValues.put(Contract.Submissions.SUBMISSION_DATE, submission.getDate().toString());
        insertValues.put(Contract.Submissions.SUBMISSION_UPVOTES, submission.getUpVotes());
        insertValues.put(Contract.Submissions.SUBMISSION_DOWNVOTES, submission.getDownVotes());
        insertValues.put(Contract.Submissions.SUBMISSION_LAST_EDIT_DATE, (submission.getLastEditDate() == null) ? null : submission.getLastEditDate().toString());
        insertValues.put(Contract.Submissions.SUBMISSION_USER_NAME, submission.getUserName());
        insertValues.put(Contract.Submissions.SUBMISSION_SUBVERSE, submission.getSubverse());
        insertValues.put(Contract.Submissions.SUBMISSION_THUMBNAIL, submission.getThumbnail());
        insertValues.put(Contract.Submissions.SUBMISSION_TITLE, submission.getTitle());
        insertValues.put(Contract.Submissions.SUBMISSION_TYPE, submission.getType());
        insertValues.put(Contract.Submissions.SUBMISSION_URL, submission.getUrl());
        insertValues.put(Contract.Submissions.SUBMISSION_CONTENT, (submission.getContent() == null) ? null : submission.getContent().toString());
        insertValues.put(Contract.Submissions.SUBMISSION_FORMATTED_CONTENT, submission.getFormattedContent());
        long result = db.insertOrThrow(Contract.Submissions.TABLE_NAME, null, insertValues);

        db.close();

        return result;
    }

    public long saveUser(User user)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();

        insertValues.put(Contract.User.USER_NAME, user.getUserName());
        insertValues.put(Contract.User.USER_COMMENT_POINTS, user.getCommentPoints().get("sum"));

        if(user.getCommentVoting()==null)
            insertValues.put(Contract.User.USER_COMMENT_VOTING, 0);
        else
            insertValues.put(Contract.User.USER_COMMENT_VOTING, user.getCommentVoting().get("sum"));

        insertValues.put(Contract.User.USER_SUBMISSION_POINTS, user.getSubmissionPoints().get("sum"));
        if(user.getSubmissionVoting()==null)
            insertValues.put(Contract.User.USER_SUBMISSION_VOTING, 0);
        else
            insertValues.put(Contract.User.USER_SUBMISSION_VOTING, user.getSubmissionVoting().get("sum"));

        long result = db.insertOrThrow(Contract.User.TABLE_NAME, null, insertValues);

        db.close();
        return result;
    }

    public int deleteUser(User user)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int res = 0;

        try {
            res = db.delete(Contract.User.TABLE_NAME, Contract.User.USER_NAME+"=?", new String[]{user.getUserName()} );
        }
        catch (Exception e)
        {
        }

        db.close();
        return res;
    }

    public int deleteSubmission(Submission submission)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int res = db.delete(Contract.Submissions.TABLE_NAME, Contract.Submissions._ID + "=" + submission.getId(), null);

        db.close();

        return res;
    }


    public List<Submission> getSavedSubmissions()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        savedSubmissions = cursorToSubmissions(db.query(Contract.Submissions.TABLE_NAME, Contract.Submissions.allSubmissionColumns, null, null, null, null, null));
        db.close();
        return savedSubmissions;
    }

    public User getUser()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user;
        try{
            user = cursorToUser(db.query(Contract.User.TABLE_NAME, Contract.User.allUserColumns, null, null, null, null, null));
        }
        catch (Exception e)
        {
            user = null;
        }

        db.close();

        return user;
    }

    private User cursorToUser(Cursor mData)
    {
        User user = new User();

        mData.moveToFirst();

        user.setUserName(mData.getString(mData.getColumnIndex(Contract.User.USER_NAME)));
        HashMap<String,String> commentPoints = new HashMap<>();
        HashMap<String,String> commentVoting = new HashMap<>();
        HashMap<String,String> submissionPoints = new HashMap<>();
        HashMap<String,String> submissionVoting = new HashMap<>();

        commentPoints.put("sum", mData.getString(mData.getColumnIndex(Contract.User.USER_COMMENT_POINTS)));
        commentVoting.put("sum", mData.getString(mData.getColumnIndex(Contract.User.USER_COMMENT_VOTING)));
        submissionPoints.put("sum", mData.getString(mData.getColumnIndex(Contract.User.USER_SUBMISSION_POINTS)));
        submissionVoting.put("sum", mData.getString(mData.getColumnIndex(Contract.User.USER_SUBMISSION_VOTING)));

        user.setCommentPoints(commentPoints);
        user.setCommentVoting(commentVoting);
        user.setSubmissionPoints(submissionPoints);
        user.setSubmissionVoting(submissionVoting);
mData.close();
        return user;
    }

    private List<Submission> cursorToSubmissions(Cursor mData)
    {
        List<Submission> submissions = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date;

        for(mData.moveToFirst(); !mData.isAfterLast(); mData.moveToNext())
        {
            Submission submission = new Submission();
            submission.setId(mData.getInt(mData.getColumnIndex(Contract.Submissions._ID)));
            submission.setCommentCount(mData.getInt(mData.getColumnIndex(Contract.Submissions.SUBMISSION_COMMENT_COUNT)));
            try{
                date = sdf.parse(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_DATE)));
                submission.setDate(date);
            }catch(Exception ex){
                date = new Date(0);
                submission.setDate(date);
            }
            submission.setUpVotes(mData.getInt(mData.getColumnIndex(Contract.Submissions.SUBMISSION_UPVOTES)));
            submission.setDownVotes(mData.getInt(mData.getColumnIndex(Contract.Submissions.SUBMISSION_DOWNVOTES)));
            try{
                date = sdf.parse(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_LAST_EDIT_DATE)));
                submission.setLastEditDate(date);
            }catch(Exception ex){
                date = new Date(0);
                submission.setLastEditDate(date);
            }
            submission.setUserName(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_USER_NAME)));
            submission.setSubverse(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_SUBVERSE)));
            submission.setThumbnail(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_THUMBNAIL)));
            submission.setTitle(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_TITLE)));
            submission.setType(mData.getInt(mData.getColumnIndex(Contract.Submissions.SUBMISSION_TYPE)));
            submission.setUrl(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_URL)));
            submission.setContent(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_CONTENT)));
            submission.setFormattedContent(mData.getString(mData.getColumnIndex(Contract.Submissions.SUBMISSION_FORMATTED_CONTENT)));

            submissions.add(submission);
        }
mData.close();
        return submissions;
    }
}
