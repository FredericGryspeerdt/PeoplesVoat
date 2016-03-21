package team4.howest.be.androidapp.view;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.List;

import team4.howest.be.androidapp.CommentsAdapter;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.databinding.FragmentCommentBinding;
import team4.howest.be.androidapp.model.Comment;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.viewmodel.CommentsViewModel;
import team4.howest.be.androidapp.viewmodel.Sortable;

public class CommentsFragment extends Fragment implements CommentsViewModel.DataListener,
        SwipeRefreshLayout.OnRefreshListener, Sortable{

    public static final String USERTHREAD = "USERTHREAD";
    public static final String USER = "USER";

    public static final String SUBMISSIONTHREAD = "SUBMISSIONTHREAD";
    public static final String SELECTED_SUBVERSE = "SELECTED_SUBVERSE";
    public static final String SELECTED_SORT = "SELECTED_SORT";
    public static final String SELECTED_SUBMISSION = "SELECTED_SUBMISSION";
    public static final String SELECTED_USER = "SELECTED_USER";

    private static final String STATE_ITEMS = "STATE_ITEMS";
    private static final String STATE_HEADER = "STATE_HEADER";
    private FragmentCommentBinding binding;
    private CommentsViewModel commentsViewModel;
    private String subverse;
    private String sort;
    private String submission;
    private String user;
    private Submission submissionState;
    private List<Comment> commentsState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_ITEMS, (Serializable) commentsState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        commentsViewModel = new CommentsViewModel(getActivity(), this);
        binding.setViewModel(commentsViewModel);
        View v = binding.getRoot();

        //setupSubmissionView(binding.submissionView);
        setData();

        return v;
    }

    private void setData() {
        setupRecylerView(binding.commentsRecyclerView);
        if (getArguments()!= null) {
            Bundle bundle = getArguments();

            if(!bundle.getBoolean(USERTHREAD)) {
                if (bundle.getString(SELECTED_SUBVERSE) != null) {
                    subverse = bundle.getString(SELECTED_SUBVERSE);
                }
                if (bundle


                        .getString(SELECTED_SORT) != null) {
                    sort = bundle.getString(SELECTED_SORT);
                }
                if (bundle.getString(SELECTED_SUBMISSION) != null) {
                    submission = bundle.getString(SELECTED_SUBMISSION);
                }

                commentsViewModel.loadComments(subverse, "hot", submission);
                commentsViewModel.loadSubmission(subverse, submission);

                binding.swipeRefreshComments.setOnRefreshListener(this);

            } else if(bundle.getString(USER) != null) {
                commentsViewModel.loadUserComments(bundle.getString(USER));

                binding.swipeRefreshComments.setEnabled(false);
            } else {
                throw new UnsupportedOperationException("USER must be supplied if a USERTHREAD is" +
                        " being requested");
            }

            /*
            if(user==null)
            {
                commentsViewModel.loadComments(subverse, "hot", submission);

                SubmissionFragment subverseFragment = new SubmissionFragment();
                Bundle args = new Bundle();
                args.putString(SubmissionFragment.SELECTED_SUBVERSE, bundle.getString(SELECTED_SUBVERSE));
                args.putString(SubmissionFragment.SELECTED_ID, bundle.getString(SELECTED_SUBMISSION));
                subverseFragment.setArguments(args);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.submission_view, subverseFragment).commit();
            }
            else
            {   //comments in de user fragment
                LinearLayout linLay = (LinearLayout) v.findViewById(R.id.submission_view).getParent();
                linLay.removeView(v.findViewById(R.id.submission_view));
                commentsViewModel.loadUserComments(user);
            }*/
        }
    }

    private void setupRecylerView(RecyclerView recyclerView){
        CommentsAdapter commentsAdapter = CommentsAdapter.newInstance();
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onCommentsChanged(List<Comment> comments, Submission submission) {
        CommentsAdapter commentsAdapter = (CommentsAdapter) binding.commentsRecyclerView
                .getAdapter();
        this.commentsState = comments;
        commentsAdapter.setComments(comments, submission);
    }

    @Override
    public void onSubmissionChanged(Submission submission) {
        CommentsAdapter commentsAdapter = (CommentsAdapter) binding.commentsRecyclerView
                .getAdapter();
        this.submissionState = submission;
        commentsAdapter.setSubmission(submission);
    }

    @Override
    public void onReload() {
        setData();
    }

    @Override
    public void onLoadingDone() {
        binding.swipeRefreshComments.setRefreshing(false);
    }

    @Override
    public void setSortOrder(String sortOrder) {
        this.sort = sortOrder;
        commentsViewModel.loadComments(subverse, this.sort, submission);
    }

    @Override
    public void onRefresh() {
        onReload();
    }
}
