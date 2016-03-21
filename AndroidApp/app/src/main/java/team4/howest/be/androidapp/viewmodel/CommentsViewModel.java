package team4.howest.be.androidapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.model.Comment;
import team4.howest.be.androidapp.model.Comments;
import team4.howest.be.androidapp.model.CommentsMarshaller;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.model.SubmissionResponse;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.view.PostReplyFragment;

/**
 * Created by anthony on 15.18.10.
 */

public class CommentsViewModel implements ViewModel {
    private int ID;
    private String subverse;
    public ObservableInt recyclerViewVisibility;
    public ObservableInt progressVisibility;
    public ObservableInt fabVisibility;

    private Context context;
    private DataListener dataListener;

    private List<Comment> comments;

    public CommentsViewModel(Context context,DataListener dataListener){
        this.context = context;
        this.dataListener = dataListener;
        progressVisibility = new ObservableInt(View.INVISIBLE);
        recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
        fabVisibility = new ObservableInt(View.INVISIBLE);

    }

    public void onReply(View view ){
        PostReplyFragment postReplyFragment = PostReplyFragment.newInstance(
                subverse,
                ID,
                -1
        );

        EventBus.getDefault().post(new NavigationEvent(postReplyFragment));
    }

    public void loadComments(final String subverse, String sort, final String id) {
        this.ID = Integer.parseInt(id);
        this.subverse = subverse;
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.INVISIBLE);
        VoatClient restClient = new VoatClient();
        restClient.getApiService().getCommentsForSubmission(subverse, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Comments>() {
                    @Override
                    public void onCompleted() {
                        if (dataListener != null) dataListener.onCommentsChanged(comments, null);
                        progressVisibility.set(View.GONE);
                        if (comments != null) {
                            recyclerViewVisibility.set(View.VISIBLE);
                            fabVisibility.set(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Comments comments) {
                        CommentsViewModel.this.comments = new CommentsMarshaller()
                                .marshall(comments);
                        for (Comment comment : comments.getData()) {
                            Log.v("", "" + comment.toString());
                            dataListener.onLoadingDone();
                        }
                    }
                });
    }


    public void loadUserComments(final String user){

        VoatClient restClient = new VoatClient();
        restClient.getApiService().getCommentsForUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Comments>() {
                    @Override
                    public void onCompleted() {

                        if (dataListener != null) dataListener.onCommentsChanged(comments, null);
                        progressVisibility.set(View.GONE);
                        if (comments != null) {
                            recyclerViewVisibility.set(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Comments comments) {
                        CommentsViewModel.this.comments = new CommentsMarshaller()
                                .marshall(comments);
                        for (Comment comment:comments.getData()) {
                            Log.v("", "" + comment.toString());
                        }

                    }
                });
    }


    public void loadSubmission(String subverse, String id){
        VoatClient client = new VoatClient();

        VoatApiService voatApiService = client.getApiService();
        voatApiService.getSingleSubmission(subverse, id, new Callback<SubmissionResponse>() {
            @Override
            public void success(SubmissionResponse submissions, Response response) {
                if (dataListener != null) dataListener.onSubmissionChanged(submissions.submission);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public interface DataListener {
        void onCommentsChanged(List<Comment> repositories, Submission submission);

        void onSubmissionChanged(Submission submission);

        void onReload();

        void onLoadingDone();
    }
}
