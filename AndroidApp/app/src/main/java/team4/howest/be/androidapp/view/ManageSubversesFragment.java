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
import android.widget.Toast;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.adapters.SubverseAdapter;
import team4.howest.be.androidapp.model.Comment;
import team4.howest.be.androidapp.model.Comments;
import team4.howest.be.androidapp.model.CommentsMarshaller;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.model.SubverseInfo;
import team4.howest.be.androidapp.model.SubverseInfoResponse;
import team4.howest.be.androidapp.model.UserSubscription;
import team4.howest.be.androidapp.model.UserSubscriptionsResponse;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.service.VoatLegacyApiService;
import team4.howest.be.androidapp.service.VoatLegacyClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageSubversesFragment extends Fragment {

    public static final String TAG = "BROWSESUBVERSESFRAG";
    public static final java.lang.String USER = "USER";

    //UI
    private RecyclerView rvSubverses;
    private ProgressBar progressBar;


    //DATA
    private ArrayList<String> listRawSubverses;
    //private ArrayList<String> listSubverseNames;
    private ArrayList<SubverseInfo> mListSubverseInfo;

    private SubverseAdapter mSubverseAdapter;
    private LinearLayoutManager mLayoutManager;


    public ManageSubversesFragment() {
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
        if (getArguments()!= null) {
            Bundle bundle = getArguments();

            if (bundle.getString(USER) != null) {
                progressBar.setVisibility(View.VISIBLE);
                mListSubverseInfo = new ArrayList<SubverseInfo>();

                VoatClient voatClient = new VoatClient();
                final VoatApiService voatApiService = voatClient.getApiService();
                voatApiService.getUserSubscriptions(bundle.getString(USER), new
                        Callback<UserSubscriptionsResponse>() {
                    @Override
                    public void success(UserSubscriptionsResponse userSubscriptionsResponse, Response response) {
                        if (userSubscriptionsResponse.getSuccess()) {

                            ArrayList<String> listSubverses = new ArrayList<String>();
                            ArrayList<UserSubscription> listUserSubscriptions = userSubscriptionsResponse.getData();
                            final int lastSize = listUserSubscriptions.size();
                            for (final UserSubscription subscription : listUserSubscriptions) {
                                voatApiService.getSubverseInfo(subscription.getName())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Subscriber<SubverseInfoResponse>() {
                                            @Override
                                            public void onCompleted() {
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(SubverseInfoResponse subverseInfoResponse) {
                                                mListSubverseInfo.add(
                                                        new SubverseInfo(
                                                                subverseInfoResponse.data.getName(),
                                                                subverseInfoResponse.data.getDescription(),
                                                                subverseInfoResponse.data.getSubscriberCount(),
                                                                subverseInfoResponse.data.getCreatedOn()
                                                        )
                                                );
                                                if (mListSubverseInfo.size() == lastSize) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    initRecyclerView();
                                                }
                                            }
                                        });
                            }

                        } else {
                            String errorType = userSubscriptionsResponse.getError().getType();
                            String errorMessage = userSubscriptionsResponse.getError().getMessage();
                        }
                        //if (!mListSubverseInfo.isEmpty()) {

                        //}
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
        }
    }


    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvSubverses.setLayoutManager(mLayoutManager);

        mSubverseAdapter = new SubverseAdapter(getActivity(), mListSubverseInfo);
        rvSubverses.setAdapter(mSubverseAdapter);
    }

}
