package team4.howest.be.androidapp.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anthony on 15.18.10.
 */
public class CommentsMarshaller {
    public List<Comment> marshall(Comments comments) {
        List<Comment> newComments = new ArrayList<>(comments.getData().size());
        for (Comment comment : comments.getData()) {
            if (comment.getLevel() != null) {
                if (comment.getParentID() != null) {
                    Comment parentComment = new Comment();
                    parentComment.setId(comment.getParentID());
                    newComments.add(newComments.indexOf(parentComment) + 1, marshall(comment));
                } else {
                    newComments.add(marshall(comment));
                }
            } else {
                comment = marshall(comment);
                comment.setLevel(0);
                comment.setChildCount(0);
                newComments.add(comment);
            }
        }
        return newComments;
    }

    private Comment marshall(Comment comment) {
        Comment mComment = comment;
        mComment.setCollapsed(false);
        mComment.setHidden(false);
        return mComment;
    }
}
