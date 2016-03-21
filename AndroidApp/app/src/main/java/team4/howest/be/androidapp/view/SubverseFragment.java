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
public class SubverseFragment extends Fragment implements Sortable, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

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

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    //DEFAULTS
    private String subverse = "_front"; //subverse die getoond moet worden --> wordt de parameter in de getSubmissionsForSubverse
    //standaard is dit _front
    private String sort = "hot"; //welke sortering moet toegepast worden? --> standaard is dit hot
    private String user = ""; //van welke user moeten de submissions getoond worden? --> standaard is dit leeg
    private String search = ""; //naar welke stuk text moet er gezocht worden? --> standaard is dit leeg
    private Integer page = 1;

    private String accesstoken;

    public static final String SELECTED_SUBVERSE = "SELECTED_SUBVERSE";
    public static final String SELECTED_SORT = "SELECTED_SORT";
    public static final String SELECTED_USER = "SELECTED_USER";
    public static final String SELECTED_SEARCH = "SELECTED_SEARCH";
    private static final String TAG = "SubverseFragment";
    private SharedPreferences sharedPrefs;
    private boolean prefShowNSFW;

    //region CONSTRUCTORS
    public SubverseFragment() {
    }

    public static SubverseFragment newInstance(String subverse, String sort) {
        SubverseFragment fragment = new SubverseFragment();

        Bundle args = new Bundle();
        args.putString(SELECTED_SUBVERSE, subverse);
        args.putString(SELECTED_SORT, sort);
        fragment.setArguments(args);

        return fragment;
    }

    public static SubverseFragment newInstance(String search) {
        SubverseFragment fragment = new SubverseFragment();

        Bundle args = new Bundle();
        args.putString(SELECTED_SEARCH, search);
        fragment.setArguments(args);

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
        View v = inflater.inflate(R.layout.fragment_subverse, container, false);

        //ophalen argumenten om juiste subverse, sorteermethode en user te tonen
        if (getArguments() != null) {
            Bundle bundle = getArguments();

            if (bundle.getString(SELECTED_SUBVERSE) != null) {
                subverse = bundle.getString(SELECTED_SUBVERSE);
            }
            if (bundle.getString(SELECTED_SORT) != null) {
                sort = bundle.getString(SELECTED_SORT);
            }
            if (bundle.getString(SELECTED_USER) != null) {
                user = bundle.getString(SELECTED_USER);
            }
            if (bundle.getString(SELECTED_SEARCH) != null) {
                search = bundle.getString(SELECTED_SEARCH);
            }
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
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshSubmissions);
        swipeRefreshLayout.setOnRefreshListener(this);

        fabLinkPost = (FloatingActionButton) v.findViewById(R.id.fab_LinkPost);
        fabSelfPost = (FloatingActionButton) v.findViewById(R.id.fab_SelfPost);

        fabLinkPost.setOnClickListener(this);
        fabSelfPost.setOnClickListener(this);

        //recyclerview instellen
        initRecyclerView(v);
        //Floating Action Button
        initFAB(v); //floating action button met submenu

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

        if (user.equals("") && search.equals(""))
        {
            fetchSubmissionsForSubverse();

            rvSubmission.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = rvSubmission.getChildCount();
                    totalItemCount = mLayoutManger.getItemCount();
                    firstVisibleItem = mLayoutManger.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        //end has been reached

                        page += 1;
                        //fetchSubmissionsForSubverse();
                        loadExtraSubmissions();

                        loading = true;
                    }
                }
            });
        }

        else if(!user.equals(""))
        {
            fetchSubmissionsForUser();
        }
        else if(!search.equals(""))
        {
            fetchSubmissionsForSearch();
        }

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

    private void loadExtraSubmissions() {
        //progressbar tonen tijdens laden submissions
        progressBar.setVisibility(View.VISIBLE);

        VoatClient client = new VoatClient();

        VoatApiService voatApiService = client.getApiService();

        voatApiService.getSubmissionsForSubverse(subverse, sort, page, new Callback<SubmissionsForSubverse>() {
            @Override
            public void success(SubmissionsForSubverse submissions, Response response) {

                for (Submission item : submissions.getSubmissions()) {
                    if (filterNSFW(item) != null) {
                        submissionList.add(item);
                    }
                }
                submissionListFull = submissionList; //volle lijst bijhouden voor de search (om terug te gaan naar volle lijst)

                submissionsAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(getContext(), "Unexpected error occured", Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initFAB(View v) {
        FloatingActionMenu famSubverse = (FloatingActionMenu) v.findViewById(R.id.fam_subverse);
        famSubverse.setClosedOnTouchOutside(true); //menu sluit wnn er naast het menu geklikt wordt
    }

    private void fetchSubmissionsForSubverse() {

        // Create a very simple REST adapter which points the GitHub API endpoint.
        VoatClient client = new VoatClient();

        VoatApiService voatApiService = client.getApiService();

        voatApiService.getSubmissionsForSubverse(subverse, sort, page, new Callback<SubmissionsForSubverse>() {
            @Override
            public void success(SubmissionsForSubverse submissionsResponse, Response response) {

                if (submissionsResponse.getSuccess()) {
                    if (swipeRefreshLayout.isRefreshing()) { //check of het door swipe to refresh event is
                        submissionList.clear();
                    }

                    for (Submission item : submissionsResponse.getSubmissions()) {
                        if (filterNSFW(item) != null) {
                            submissionList.add(item);
                        }
                    }

                    submissionListFull = submissionList; //volle lijst bijhouden voor de search (om terug te gaan naar volle lijst)

                    if (swipeRefreshLayout.isRefreshing()) {
                        submissionsAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        bckgroundRefresh.setVisibility(View.GONE);

                    } else {
                        submissionsAdapter = new SubmissionsAdapter((MainActivity) getActivity(), submissionList);
                        rvSubmission.setAdapter(submissionsAdapter);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    String errorType = submissionsResponse.getError().getType();
                    String errorMessage = submissionsResponse.getError().getMessage();

                    Toast.makeText(getActivity(), errorType + ": " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(getContext(), "Unexpected error occured", Toast.LENGTH_LONG).show();

                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchSubmissionsForUser() {


            // Create a very simple REST adapter which points the GitHub API endpoint.
            VoatClient client = new VoatClient();

            VoatApiService voatApiService = client.getApiService();
            //voatApiService.getSubmissionsForSubverse("_front", new Callback<SubmissionsForSubverse>() {
            voatApiService.getSubmissionsForUser(user, new Callback<SubmissionsForSubverse>() {
                @Override
                public void success(SubmissionsForSubverse submissions, Response response) {

                    for (Submission item : submissions.getSubmissions()) {
                        if(filterNSFW(item) != null){
                            submissionList.add(item);
                        }
                    }

                    submissionListFull = submissionList; //volle lijst bijhouden voor de search (om terug te gaan naar volle lijst)

                    SubmissionsAdapter submissionsAdapter = new SubmissionsAdapter((MainActivity)getActivity(), submissionList);
                    rvSubmission.setAdapter(submissionsAdapter);
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void failure(RetrofitError error) {
                    //Toast.makeText(getContext(), "Unexpected error occured", Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            });

    }

    private void fetchSubmissionsForSearch() {
        // Create a very simple REST adapter which points the GitHub API endpoint.
        VoatClient client = new VoatClient();

        VoatApiService voatApiService = client.getApiService();
        //voatApiService.getSubmissionsForSubverse("_front", new Callback<SubmissionsForSubverse>() {
        voatApiService.getSubmissionsForSearch("_all" , search, new Callback<SubmissionsForSubverse>() {
            @Override
            public void success(SubmissionsForSubverse submissions, Response response) {
                for (Submission item : submissions.getSubmissions()) {
                    if(filterNSFW(item) != null){
                        submissionList.add(item);
                    }
                }

                submissionListFull = submissionList; //volle lijst bijhouden voor de search (om terug te gaan naar volle lijst)

                SubmissionsAdapter submissionsAdapter = new SubmissionsAdapter((MainActivity)getActivity(), submissionList);
                rvSubmission.setAdapter(submissionsAdapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                //Toast.makeText(getContext(), "Unexpected error occured", Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "Try searching something else", Toast.LENGTH_LONG).show();
            }
        });
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
        //onBackNavigationListener.onBackNav(subverse);
        Integer pos;

        try {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();

            pos = adapter.getPosition(subverse);

            if (pos < 0 && (user.equals("") && search.equals(""))){   //subverse stond nog niet in de lijst van de spinner --> toevoegen
                adapter.add(subverse);
                adapter.notifyDataSetChanged();
            }

            spinner.setTag(adapter.getPosition(subverse));
            spinner.setSelection(adapter.getPosition(subverse));
        } catch (Exception e) {
        }

    }


    @Override
    public void onRefresh() {
        if (user.equals("") && search.equals("")) { //refresht zoals gewoon
            page = 1; //default waarde
            bckgroundRefresh.setVisibility(View.VISIBLE);
            fetchSubmissionsForSubverse();
        } else
            swipeRefreshLayout.setRefreshing(false); //refresht NIET op de Userfragment of SearchSubmissionsFragment
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
        //outState.putInt(SELECTED_SUBVERSE, spinner.getSelectedItemPosition());
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
