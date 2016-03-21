package team4.howest.be.androidapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.model.Drawer;

public class MySubversesFragment extends ListFragment {

    public static MySubversesFragment newInstance() {
        MySubversesFragment fragment = new MySubversesFragment();
        return fragment;
    }

    public MySubversesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter listAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, Drawer.menuItems);
        setListAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_subverses, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
