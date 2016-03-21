package team4.howest.be.androidapp.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.ocpsoft.prettytime.PrettyTime;

import de.greenrobot.event.EventBus;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.BR;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.model.Comment;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.model.VoteResponse;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.view.PostReplyFragment;
import team4.howest.be.androidapp.view.UserFragment;

/**
 * Created by anthony on 15.18.10.
 */
public final class ItemCommentViewModel extends BaseObservable implements ViewModel {

    private Comment comment;
    private Context context;
    private CommentEventHandler commentEventHandler;

    public ItemCommentViewModel(Context context, Comment comment, CommentEventHandler commentEventHandler) {
        this.comment = comment;
        this.context = context;
        this.commentEventHandler = commentEventHandler;
    }

    public void onCollapse(View view){
        commentEventHandler.collapseComment(view);
    }

    public void onReply(View view ){
        PostReplyFragment postReplyFragment = PostReplyFragment.newInstance(
                comment.getSubverse(),
                comment.getSubmissionID(),
                comment.getId()
        );

        EventBus.getDefault().post(new NavigationEvent(postReplyFragment));
    }

    public void onShare(View view){
        String message =
                comment.getUserName() + ": \"" + comment.getContent() + "\" (" + getDate() + ", " + "http://fakevout.azurewebsites.net/v/" + comment.getSubverse() + "/comments/" + comment.getSubmissionID() + ")";
        String messageHtml =
                "<p>" + comment.getUserName() + ": \"" + comment.getContent() + "\" (" + getDate() + ", <a href='" + "http://fakevout.azurewebsites.net/v/" + comment.getSubverse() + "/comments/" + comment.getSubmissionID() + "'>" + comment.getSubverse() + "/" + comment.getId() + "</a>)</p>"; Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, message);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Share this comment with.."));
    }

    public void onUserSelected(View view){
        new VoatClient().getApiService().getUserInfo(comment.getUserName())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<User.UserAPIResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR OCCURRED", e.getMessage());
                    }

                    @Override
                    public void onNext(User.UserAPIResponse userAPIResponse) {
                        User user = userAPIResponse.getData();
                        UserFragment userFragment = UserFragment.newInstance(user);

                        EventBus.getDefault().post(new NavigationEvent(userFragment));

                    }
                });
    }

    public void onDownvote(View view) {
        VoatClient restClient = new VoatClient();
        restClient.getApiService().postVote("comment", Integer.toString(comment.getId()), "-1")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<VoteResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(VoteResponse comments) {
                        Log.i("ItemCommentViewModel",comments.getData().getResult().toString());
                        if(comments.getData().getSuccess()){
                            setKarma(0,1);
                        } else {
                            setKarma(0,0);
                            Toast.makeText(context, "You need more CCP to vote on this comment.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onUpvote(View view) {
        VoatClient restClient = new VoatClient();
        restClient.getApiService().postVote("comment", Integer.toString(comment.getId()), "1")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<VoteResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(VoteResponse comments) {
                        Log.i("ItemCommentViewModel",comments.getData().getResult().toString());
                        if(comments.getData().getSuccess()){
                            setKarma(1,0);
                        } else {
                            setKarma(0,0);
                            Toast.makeText(context, "You need more CCP to vote on this comment.", Toast
                                    .LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Bindable
    public String getUserName(){
        return "u/" + comment.getUserName();
    }

    @Bindable
    public String getChildCount(){
        if(comment.getChildCount() == 1){
            return Integer.toString(comment.getChildCount()) + " reply hidden";
        }
        return Integer.toString(comment.getChildCount()) + " replies hidden";
    }

    @Bindable
    public Integer getLevel(){
        return comment.getLevel();
    }

    @Bindable
    public String getKarma(){
        return Integer.toString(comment.getUpVotes() - comment.getDownVotes()) +
                " (+" + comment.getUpVotes() +
                ", -" + comment.getDownVotes() +
                ")";
    }

    public void setKarma(int upVotes, int downVotes){
        comment.setUpVotes(comment.getUpVotes() + upVotes);
        comment.setDownVotes(comment.getDownVotes() + downVotes);
        notifyPropertyChanged(BR.karma);
    }

    @Bindable
    public String getDate(){
        return new PrettyTime().format(comment.getDate());
    }


    @BindingAdapter("android:paddingLeft")
    public static void setPaddingLeft(View view, Integer level) {
        view.setPadding(level*40, 0, 0, 0);
        /*
        ViewPager.MarginLayoutParams lp = (ViewPager.MarginLayoutParams)view.getLayoutParams();
        if (lp != null) {
            lp.setMargins(level * 50 , lp.topMargin, lp.rightMargin, lp.bottomMargin );
            view.setLayoutParams(lp);
        }*/
    }

    @BindingAdapter("android:layout_marginLeft")
    public static void setLayoutMarginLeft(View view, Integer level) {
/*
        ViewPager.MarginLayoutParams lp = (ViewPager.MarginLayoutParams)view.getLayoutParams().
        if (lp != null) {
            lp.setMargins(level * 10, lp.topMargin, lp.rightMargin, lp.bottomMargin);
            view.setLayoutParams(lp);
        }
*/
    }


    @BindingAdapter("android:background")
    public static void setBackground(View view, Integer level) {
        //% 2 ? @color/cardview_dark_background : @color/cardview_light_background
        if(level % 2 == 0) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.cardBackground));
        } else if (level % 2 == 1) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.cardBackgroundDark));
        }
    }

    public interface CommentEventHandler{
        void collapseComment(View view);
    }

}
