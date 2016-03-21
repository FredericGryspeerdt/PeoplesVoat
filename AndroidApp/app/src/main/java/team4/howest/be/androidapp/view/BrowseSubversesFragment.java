package team4.howest.be.androidapp.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.adapters.SubverseAdapter;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.model.SubverseInfo;
import team4.howest.be.androidapp.service.VoatLegacyApiService;
import team4.howest.be.androidapp.service.VoatLegacyClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseSubversesFragment extends Fragment {

    public static final String TAG = "BROWSESUBVERSESFRAG";

    //UI
    private RecyclerView rvSubverses;
    private ProgressBar progressBar;


    //DATA
    private ArrayList<String> listRawSubverses;
    //private ArrayList<String> listSubverseNames;
    private ArrayList<SubverseInfo> mListSubverseInfo;

    private SubverseAdapter mSubverseAdapter;
    private LinearLayoutManager mLayoutManager;


    public BrowseSubversesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_browse_subverses, container, false);

        //VIEWS OPHALEN
        rvSubverses = (RecyclerView) v.findViewById(R.id.rv_subverses);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar_subverses);

        loadSubverses();


        return v;

    }


    private void loadSubverses() {
        progressBar.setVisibility(View.VISIBLE);

        VoatLegacyClient voatLegacyClient = new VoatLegacyClient();
        VoatLegacyApiService voatLegacyApiService = voatLegacyClient.getApiService();

        voatLegacyApiService.getTop200Subverses(new Callback<DefaultSubverseResponse>() {
            @Override
            public void success(DefaultSubverseResponse defaultSubverseResponse, Response response) {
                listRawSubverses = new ArrayList<String>();
                listRawSubverses.addAll(defaultSubverseResponse.getDefaultSubverses());

                processRawListSubverses(); //ruwe info string omzetten naar SubverseInfo model en toevoegen aan mListSubverseInfo

                progressBar.setVisibility(View.INVISIBLE);

                if (!mListSubverseInfo.isEmpty()) {
                    initRecyclerView();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void processRawListSubverses() {

        /**
         * "Name: API,
         * Description: Pre-Release Api related discussion,
         * Subscribers: 91,
         * Created: 8-4-2015 22:46:02",
         *
         * subinfo = ["Name: API", "Description: Pre-Release Api related discussion",
         * "Subscribers: 91", "Created: 8-4-2015 22:46:02"]
         *
         * subsubinfo = ["Name", "API", "Description",
         * "Pre-Release Api related discussion", "Subscribers","91","Created",
         * "8-4-2015 22:46:02"]
         */
        mListSubverseInfo = new ArrayList<SubverseInfo>();

        String[] subInfo;
        String[] subSubInfo;
        //String[] namesSubverses;  //--> nu niet nodig
        ArrayList<String> arrInfo = new ArrayList<>();

        String name, description, createdOn;
        Integer subscriberCount;


            /*
            subSubInfo = subInfo[0].split(":");  // ["name", "API"];
            name = subSubInfo[1].trim();

            listSubverseNames.add(name);
            */

        for (String info : listRawSubverses) {
            subInfo = info.split(",");

            for (String sInfo : subInfo) {
                subSubInfo = sInfo.split(":");
                try {
                    arrInfo.add(subSubInfo[1].trim());
                } catch (Exception e){
                    Log.e(TAG, e.toString());
                }
            }

            name = arrInfo.get(0);
            description = arrInfo.get(1);
            subscriberCount = Integer.parseInt(arrInfo.get(2));
            createdOn = (arrInfo.get(3));

            SubverseInfo subverseInfo = new SubverseInfo(
                    name, description, subscriberCount, createdOn
            );

            mListSubverseInfo.add(subverseInfo);

            arrInfo.clear();  //array leegmaken
        }
    }


    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvSubverses.setLayoutManager(mLayoutManager);

        mSubverseAdapter = new SubverseAdapter(getActivity(), mListSubverseInfo);
        rvSubverses.setAdapter(mSubverseAdapter);
    }

}
