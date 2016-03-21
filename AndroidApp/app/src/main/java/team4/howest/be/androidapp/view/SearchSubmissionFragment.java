package team4.howest.be.androidapp.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import de.greenrobot.event.EventBus;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchSubmissionFragment extends Fragment {


    public SearchSubmissionFragment() {
        // Required empty public constructor
    }

    public static SearchSubmissionFragment newInstance() {
        SearchSubmissionFragment fragment = new SearchSubmissionFragment();
        return fragment;
    }

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
        return inflater.inflate(R.layout.fragment_search_submission, container, false);
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
                SubverseFragment subverseFragment = SubverseFragment.newInstance(query);
                // Insert the fragment by replacing any existing fragment
                EventBus.getDefault().post(new NavigationEvent(subverseFragment));
                searchView.clearFocus();//zorgt ervoor dat deze methode niet 2 keer opgeroepen wordt
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }
}
