package team4.howest.be.androidapp.view;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.adapters.SearchAdapter;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.model.UserSubmission;
import team4.howest.be.androidapp.model.UserSubmissionResponse;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.service.VoatLegacyApiService;
import team4.howest.be.androidapp.service.VoatLegacyClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostSubmissionFragment extends Fragment {


    public static final String POST_TYPE = "POST_TYPE";
    private String postType;


    private AutoCompleteTextView inputSearchSubverses;
    private EditText inputTitle, inputMessage, inputURL;
    private TextInputLayout inputLayoutTitle, inputLayoutSubverse, inputLayoutMessage, inputLayoutURL, inputLayoutURLProtocol;
    private Button btnPost;
    private Switch inputNSFW;
    private Switch inputAnonymous;
    private Spinner inputUrlProtocol;


    List<String> allSubverses;
    SearchAdapter adapter;

    String url;
    UserSubmission newSubmission;
    String postSubverse;

    //region CONSTRUCTORS
    public PostSubmissionFragment() {
        // Required empty public constructor
    }

    public static PostSubmissionFragment newInstance(String postType) {

        Bundle args = new Bundle();
        args.putString(POST_TYPE, postType);

        PostSubmissionFragment fragment = new PostSubmissionFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        //View v = inflater.inflate(R.layout.fragment_post_submission, container, false);
        View v = inflater.inflate(R.layout.fragment_post_submission_v2, container, false);


        //ophalen argumenten om juiste subverse, sorteermethode en user te tonen
        if (getArguments() != null) {
            Bundle bundle = getArguments();

            if (bundle.getString(POST_TYPE) != null) {
                postType = bundle.getString(POST_TYPE);
            }

        }

        inputNSFW = (Switch) v.findViewById(R.id.switchNSFW);
        inputAnonymous = (Switch) v.findViewById(R.id.switchAnonymous);
        inputTitle = (EditText) v.findViewById(R.id.inputTitle);
        inputMessage = (EditText) v.findViewById(R.id.inputMessage);
        inputURL = (EditText) v.findViewById(R.id.inputUrl);
        inputSearchSubverses = (AutoCompleteTextView) v.findViewById(R.id.inputSearch);
        inputUrlProtocol = (Spinner) v.findViewById(R.id.inputURLProtocol);
        btnPost = (Button) v.findViewById(R.id.btnSubmit);

        inputLayoutMessage = (TextInputLayout) v.findViewById(R.id.input_layout_message);
        inputLayoutSubverse = (TextInputLayout) v.findViewById(R.id.input_layout_search);
        inputLayoutTitle = (TextInputLayout) v.findViewById(R.id.input_layout_title);
        inputLayoutURL = (TextInputLayout) v.findViewById(R.id.input_layout_url);
        //inputLayoutURLProtocol = (TextInputLayout) v.findViewById(R.id.input_layout_URL_protocol);

        inputLayoutTitle.setErrorEnabled(true);
        inputLayoutMessage.setErrorEnabled(true);
        inputLayoutURL.setErrorEnabled(true);

        // Inflate the layout for this fragment obv post type
        if (postType == "SELFPOST") {
            //inputLayoutURL.setVisibility(View.GONE);
            v.findViewById(R.id.layout_link).setVisibility(View.GONE);
        } else {
            inputLayoutMessage.setVisibility(View.GONE);
        }


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        initSearchBox();
        initSpinnerURLprotocols();

        inputTitle.addTextChangedListener(new MyTextWatcher(inputTitle));
        inputMessage.addTextChangedListener(new MyTextWatcher(inputMessage));
        inputSearchSubverses.addTextChangedListener(new MyTextWatcher(inputSearchSubverses));
        inputURL.addTextChangedListener(new MyTextWatcher(inputURL));

        return v;


    }

    private void initSpinnerURLprotocols() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.url_protocols_arrays, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        inputUrlProtocol.setAdapter(adapter);
    }

    private void submitForm() {
        if (!validateTitle()) {
            return;
        }
        if (!validateMessage()) {
            return;
        }

        if (!validateSubverse()) {
            return;
        }

        if (postType == "LINKPOST") {
            if (!validateURL()) {
                return;
            }
        }


        //maak een nieuwe newSubmission
        newSubmission = new UserSubmission();

        newSubmission.setTitle(inputTitle.getText().toString());
        newSubmission.setAnon(inputAnonymous.isChecked());
        newSubmission.setNsfw(inputNSFW.isChecked());
        newSubmission.setContent(inputMessage.getText().toString());
        if(postType == "LINKPOST"){
            url = inputUrlProtocol.getSelectedItem().toString() + inputURL.getText().toString();
            newSubmission.setUrl(url);
        } else {
            newSubmission.setUrl(null);
        }

        // Create a very simple REST adapter which points the GitHub API endpoint.
        VoatClient client = new VoatClient();

        VoatApiService voatApiService = client.getApiService();

        voatApiService.postSubmission(inputSearchSubverses.getText().toString(), newSubmission, new Callback<UserSubmissionResponse>() {
            UserSubmissionResponse userSubmResponse;

            @Override
            public void success(UserSubmissionResponse userSubmissionResponse, Response response) {
                //Submission postedSubmission = userSubmissionResponse.getData();
                userSubmResponse = userSubmissionResponse;

                if (userSubmissionResponse.getSuccess()) {
                    Toast.makeText(getActivity(), "Submitted succesfully!", Toast.LENGTH_SHORT).show();
                    SubverseFragment redirectFrag = SubverseFragment.newInstance(inputSearchSubverses.getText().toString(), "new");
                    EventBus.getDefault().post(new NavigationEvent(redirectFrag));
                } else {

                }
            }

            @Override
            public void failure(RetrofitError error) {

                //Toast.makeText(getActivity(), userSubmResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean validateURL() {
        String url = inputUrlProtocol.getSelectedItem().toString() + inputURL.getText().toString();

        if (!Patterns.WEB_URL.matcher(url).matches()) {
            inputURL.setError("Please enter a valid http, https or ftp URL");
            requestFocus(inputURL);
            return false;
        }
        /*
        if(!URLUtil.isValidUrl(inputURL.getText().toString())){
            inputURL.setError("Please enter a valid http, https or ftp URL");
            requestFocus(inputURL);
            return false;
        }
        */

        return true;
    }

    private boolean validateSubverse() {
        if (inputSearchSubverses.getText().toString().isEmpty()) {
            inputSearchSubverses.setError("You must choose a subverse");
            requestFocus(inputSearchSubverses);
            return false;
        } else {
            inputLayoutSubverse.setErrorEnabled(false);
        }
        return true;
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

    private boolean validateTitle() {

        if (inputTitle.getText().toString().trim().isEmpty()) {
            inputTitle.setError("Enter a title");
            requestFocus(inputTitle);
            return false;
        } else if (inputTitle.getText().toString().trim().length() < 5) {
            inputTitle.setError("Title must have a minimum length of 5 characters");
            requestFocus(inputTitle);
            return false;

        } else if (inputTitle.getText().toString().trim().length() > 200) {
            inputTitle.setError("Title must have a maximum length of 200 characters");
            requestFocus(inputTitle);
            return false;
        } else {
            inputLayoutTitle.setErrorEnabled(false);
        }

        return true;
    }

    private void initSearchBox() {
        VoatLegacyClient voatLegacyClient = new VoatLegacyClient();

        VoatLegacyApiService voatLegacyApiService = voatLegacyClient.getApiService();
        voatLegacyApiService.getDefaultSubverses(new Callback<DefaultSubverseResponse>() {
            @Override
            public void success(DefaultSubverseResponse defaultSubverseResponse, Response response) {
                allSubverses = defaultSubverseResponse.getDefaultSubverses();
                inputSearchSubverses.setThreshold(1);
                initializeSearchBoxAdapter();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void initializeSearchBoxAdapter() {
        adapter = new SearchAdapter(this.getContext(), R.layout.fragment_post_submission, R.id.lbl_name, allSubverses);
        inputSearchSubverses.setAdapter(adapter);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.inputTitle:
                    validateTitle();
                    break;
                case R.id.inputMessage:
                    validateMessage();
                    break;
                case R.id.inputSearch:
                    validateSubverse();
                    break;
                case R.id.inputUrl:
                    validateURL();
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        //ActionBar actionBar =((AppCompatActivity) getActivity()).getSupportActionBar();
        /*
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.subverses_spinner).setVisibility(View.GONE);
        toolbar.setTitle("New submission");
        */
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem search = menu.findItem(R.id.action_search);
        search.setVisible(false);
        MenuItem sort = menu.findItem(R.id.action_sort);
        sort.setVisible(false);
    }
}
