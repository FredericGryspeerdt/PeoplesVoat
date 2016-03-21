package team4.howest.be.androidapp.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.adapters.SubmissionsAdapter;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.model.SubmissionResponse;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubmissionFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Submission submissionsForSubverse;
    private ProgressBar progressBar;

    public static final String SELECTED_SUBVERSE = "SELECTED_SUBVERSE";
    public static final String SELECTED_ID = "SELECTED_ID";

    private String subverse = "_front";
    private String id = "0";

    public SubmissionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_subverse, container, false);

        //ophalen geselecteerde subverse uit toolbar spinner
        if (getArguments()!= null){
            Bundle bundle = getArguments();

            if (bundle.getString(SELECTED_SUBVERSE) != null){
                subverse = bundle.getString(SELECTED_SUBVERSE);
            }
            if (bundle.getString(SELECTED_ID) != null){
                id = bundle.getString(SELECTED_ID);
            }

        }

        //initialize recycler view
        mRecyclerView = (RecyclerView)v.findViewById(R.id.rv_submissionsInSubverse);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(false);

        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        try {

            initializeData();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int visibility = v.getVisibility();
        v.setVisibility(View.GONE);
        v.setVisibility(visibility);
        v.requestLayout();
        return v;
    }


    private void initializeData() throws IOException {

        // Create a very simple REST adapter which points the GitHub API endpoint.
        VoatClient client = new VoatClient();

        VoatApiService voatApiService = client.getApiService();
        //voatApiService.getSubmissionsForSubverse("_front", new Callback<SubmissionsForSubverse>() {
        voatApiService.getSingleSubmission(subverse, id, new Callback<SubmissionResponse>() {
            @Override
            public void success(SubmissionResponse submissions, Response response) {
                submissionsForSubverse = submissions.submission;
                initializeAdapter();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void initializeAdapter() {
        ArrayList<Submission> submissions = new ArrayList<Submission>();
        submissions.add(submissionsForSubverse);

        //SubmissionsRVAdapter adapter = new SubmissionsRVAdapter(getActivity(), submissions );
        //SubmissionAdapter adapter = new SubmissionAdapter(getActivity(), submissions );
        SubmissionsAdapter submissionsAdapter = new SubmissionsAdapter((MainActivity)getActivity(), submissions);


        mRecyclerView.setAdapter(submissionsAdapter);
    }

}
