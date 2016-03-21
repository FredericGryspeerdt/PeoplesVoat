package team4.howest.be.androidapp.view;


import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.Navigation.KillFragmentEvent;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.adapters.SearchAdapter;
import team4.howest.be.androidapp.model.CommentPostRequest;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.model.ReplyResponse;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.model.UserSubmission;
import team4.howest.be.androidapp.model.UserSubmissionResponse;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.service.VoatLegacyApiService;
import team4.howest.be.androidapp.service.VoatLegacyClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostReplyFragment extends Fragment {


    public static final String SUBVERSE = "SUBVERSE";
    public static final String SUBMISSION_ID = "SUBMISSION_ID";
    public static final String COMMENT_ID = "COMMENT_ID";
    private boolean isCommentReply;

    private String subverse;
    private int submissionID, commentID;

    private EditText inputMessage;
    private TextInputLayout inputLayoutMessage;
    private ActionProcessButton btnPost;

    String url;
    UserSubmission newSubmission;
    String postSubverse;

    //region CONSTRUCTORS
    public PostReplyFragment() {
        // Required empty public constructor
    }

    public static PostReplyFragment newInstance(String subverse,
                                                int submissionID,
                                                int commentID)
    {

        Bundle args = new Bundle();
        args.putString(SUBVERSE, subverse);
        args.putInt(SUBMISSION_ID, submissionID);
        args.putInt(COMMENT_ID, commentID);

        PostReplyFragment fragment = new PostReplyFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        //View v = inflater.inflate(R.layout.fragment_post_submission, container, false);
        View v = inflater.inflate(R.layout.fragment_post_reply, container, false);


        //ophalen argumenten om juiste subverse, sorteermethode en user te tonen
        if (getArguments() != null) {
            Bundle bundle = getArguments();

            if (bundle.getInt(COMMENT_ID) != -1) {
                isCommentReply = true;
                commentID = bundle.getInt(COMMENT_ID);
            }

            if(bundle.getInt(SUBMISSION_ID) != -1){
                submissionID = bundle.getInt(SUBMISSION_ID);
            }

            if(bundle.getString(SUBVERSE) != null){
                subverse = bundle.getString(SUBVERSE);
            }

        }

        inputMessage = (EditText) v.findViewById(R.id.inputMessage);
        btnPost = (ActionProcessButton) v.findViewById(R.id.btnSubmit);

        inputLayoutMessage = (TextInputLayout) v.findViewById(R.id.input_layout_message);
        //inputLayoutURLProtocol = (TextInputLayout) v.findViewById(R.id.input_layout_URL_protocol);

        inputLayoutMessage.setErrorEnabled(true);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        inputMessage.addTextChangedListener(new MyTextWatcher(inputMessage));

        return v;


    }

    private void submitForm() {

        btnPost.setMode(ActionProcessButton.Mode.ENDLESS);
        btnPost.setProgress(1);

        if (!validateMessage()) {
            return;
        }

        // Create a very simple REST adapter which points the GitHub API endpoint.
        VoatClient client = new VoatClient();
        VoatApiService voatApiService = client.getApiService();
        Observable<ReplyResponse> replyResponseObservable;
        if(isCommentReply) {
            replyResponseObservable = voatApiService.postReplyToComment(
                    subverse,
                    Integer.toString(submissionID),
                    Integer.toString(commentID),
                    new CommentPostRequest(inputMessage.getText().toString())
            );
        } else {
            replyResponseObservable = voatApiService.postReplyToSubmission(
                    subverse,
                    Integer.toString(submissionID),
                    new CommentPostRequest(inputMessage.getText().toString())
            );
        }

        replyResponseObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<ReplyResponse>() {
                    @Override
                    public void call(ReplyResponse replyResponse) {
                        btnPost.setProgress(100);
                    }
                })
                .delay(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<ReplyResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ReplyResponse replyResponse) {
                        EventBus.getDefault().post(new KillFragmentEvent());
                    }
                });
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.inputMessage:
                    validateMessage();
                    break;
            }
        }
    }

    private boolean validateMessage() {
        if (inputMessage.getText().toString().trim().length() > 1000) {
            inputMessage.setError("Message must have a maximum length of 1000 characters");
            requestFocus(inputMessage);
            return false;
        } else {
            inputLayoutMessage.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        //ActionBar actionBar =((AppCompatActivity) getActivity()).getSupportActionBar();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.subverses_spinner).setVisibility(View.GONE);
        toolbar.setTitle("New submission");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem search = menu.findItem(R.id.action_search);
        search.setVisible(false);
        MenuItem sort = menu.findItem(R.id.action_sort);
        sort.setVisible(false);

        super.onPrepareOptionsMenu(menu);


    }
}
