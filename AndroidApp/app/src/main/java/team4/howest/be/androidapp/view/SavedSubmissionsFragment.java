package team4.howest.be.androidapp.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.VoatApplication;
import team4.howest.be.androidapp.adapters.SubmissionsAdapter;
import team4.howest.be.androidapp.database.Contract;
import team4.howest.be.androidapp.database.DataSource;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.model.SubmissionsForSubverse;
import team4.howest.be.androidapp.service.SearchService;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.viewmodel.Sortable;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedSubmissionsFragment extends Fragment implements Sortable, View.OnClickListener {

    //UI
    private Spinner spinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvSubmission;
    private ProgressBar progressBar;
    private FloatingActionButton fabLinkPost;
    private FloatingActionButton fabSelfPost;
    private FrameLayout bckgroundRefresh;

    //DATA
    private ArrayList<Submission> submissionList;
    private ArrayList<Submission> submissionListFull;
    private SubmissionsAdapter submissionsAdapter;

    LinearLayoutManager mLayoutManger;


    //DEFAULTS
    public static String subverse = "my_saved_submsn"; //subverse die getoond moet worden --> wordt de parameter in de getSubmissionsForSubverse

    private String accesstoken;

    private static final String TAG = "SubverseFragment";
    private SharedPreferences sharedPrefs;
    private boolean prefShowNSFW;

    //region CONSTRUCTORS
    public SavedSubmissionsFragment() {
    }

    public static SavedSubmissionsFragment newInstance() {
        SavedSubmissionsFragment fragment = new SavedSubmissionsFragment();
        return fragment;
    }
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().supportInvalidateOptionsMenu();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_subverse_saved, container, false);

        //ophalen argumenten om juiste subverse, sorteermethode en user te tonen
        if (getArguments() != null) {
            Bundle bundle = getArguments();

            sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getContext());

            prefShowNSFW = sharedPrefs.getBoolean("prefShowNSFW", true);
        }

        //ophalen views
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        bckgroundRefresh = (FrameLayout) v.findViewById(R.id.bckgroundRefresh);
        bckgroundRefresh.setVisibility(View.GONE);

        spinner = (Spinner) getActivity().findViewById(R.id.subverses_spinner);


        //recyclerview instellen
        initRecyclerView(v);

        //check of een gebruiker is ingelogd
        accesstoken = ((VoatApplication) getActivity().getApplicationContext()).getBearer();

        return v;
    }

    //region HELPER METHODS

    public boolean isLoggedIn(String accesstoken) {
        if (accesstoken != null) {
            return true; //gebruiker is ingelogd
        } else {
            return false; //gebruiker is niet ingelogd
        }
    }

    //endregion

    private void initRecyclerView(View v) {
        //submissionList = new ArrayList<Submission>();
        submissionList = new ObservableArrayList<Submission>();

        //Picasso.with(getActivity().getApplicationContext()).setIndicatorsEnabled(true);

        rvSubmission = (RecyclerView) v.findViewById(R.id.rv_submissionsInSubverse);
        mLayoutManger = new LinearLayoutManager(getActivity());
        rvSubmission.setLayoutManager(mLayoutManger);

        fetchSavedSubmissions();
    }

    private Submission filterNSFW(Submission submission){
        String s = submission.getTitle().toLowerCase();
        if (!prefShowNSFW){
            //NSFW field is momenteel nog niet beschikbaar in de Voat API dus filteren we via de title
            if(!s.contains("nsfw")) {
                return submission;
            } else {
                return null;
            }
        } else {
            return submission;
        }
    }

    private void fetchSavedSubmissions() {

        DataSource dataSource = new DataSource(getContext());
        submissionList.addAll(dataSource.getSavedSubmissions());

        submissionListFull = submissionList; //volle lijst bijhouden voor de search (om terug te gaan naar volle lijst)

        SubmissionsAdapter submissionsAdapter = new SubmissionsAdapter((MainActivity)getActivity(), submissionList);
        rvSubmission.setAdapter(submissionsAdapter);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {//menu voor de search aanpassen
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_search).setVisible(true);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();//zorgt ervoor dat deze methode niet 2 keer opgeroepen wordt
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    submissionList = (ArrayList<Submission>) SearchService.searchSubmissionsForText(submissionListFull, newText);
                } else {
                    submissionList = submissionListFull;
                }

                //SubmissionsRVAdapter adapter = new SubmissionsRVAdapter(getActivity(), submissionList);
                SubmissionsAdapter submissionsAdapter = new SubmissionsAdapter((MainActivity)getActivity(), submissionList);
                rvSubmission.setAdapter(submissionsAdapter);
                rvSubmission.getAdapter().notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                return true;
            }
        });
    }

    @Override
    public void setSortOrder(String sortOrder) {

    }


    @Override
    public void onResume() {
        super.onResume();
        Integer pos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "SELFPOST":
                //Toast.makeText(getActivity(), "SELFPOST",Toast.LENGTH_LONG).show();
                PostSubmissionFragment selfpostfragment = PostSubmissionFragment.newInstance("SELFPOST");
                EventBus.getDefault().post(new NavigationEvent(selfpostfragment));
                break;
            case "LINKPOST":
                //Toast.makeText(getActivity(), "LINKPOST",Toast.LENGTH_LONG).show();
                PostSubmissionFragment linkpostfragment = PostSubmissionFragment.newInstance("LINKPOST");
                EventBus.getDefault().post(new NavigationEvent(linkpostfragment));
                break;
        }
    }

    //region  LIFECYCLE METHODS

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //endregion
}
