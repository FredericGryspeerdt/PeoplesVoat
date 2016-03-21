package team4.howest.be.androidapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.model.SubverseInfo;
import team4.howest.be.androidapp.view.MainActivity;
import team4.howest.be.androidapp.view.SubverseFragment;

/**
 * Created by Frederic on 3/01/2016.
 */
public class SubverseAdapter extends RecyclerView.Adapter<SubverseViewHolder> {

    private ArrayList<SubverseInfo> mListSubverseInfo;
    private Context mContext;

    public SubverseAdapter(Context context, ArrayList<SubverseInfo> mListSubverseInfo) {
        this.mListSubverseInfo = mListSubverseInfo;
        this.mContext = context;
    }

    @Override
    public SubverseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ViewGroup vSubverseInfo = (ViewGroup) mInflater.inflate(R.layout.item_subverse_info, parent, false);
        SubverseViewHolder vhSubverse = new SubverseViewHolder(vSubverseInfo);

        return vhSubverse;
    }

    @Override
    public void onBindViewHolder(SubverseViewHolder holder, int position) {
        final SubverseInfo subverseInfo = mListSubverseInfo.get(position);
        bindSubverseViewHolder(holder, subverseInfo, mContext);
    }

    public static void bindSubverseViewHolder(SubverseViewHolder viewHolder, final SubverseInfo subverseInfo,final Context mContext){
        SubverseViewHolder subverseViewHolder = viewHolder;

        subverseViewHolder.tvName.setText(subverseInfo.getName());
        subverseViewHolder.tvDate.setText(subverseInfo.getCreatedOn());
        subverseViewHolder.tvDescription.setText(subverseInfo.getDescription());
        subverseViewHolder.tvSubscriberCount.setText(Integer.toString(subverseInfo.getSubscriberCount()));

        //click events toevoegen

        subverseViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create fragment and give it an argument for the selected subverse
                SubverseFragment subverseFragment = SubverseFragment.newInstance(subverseInfo.getName(),"HOT");

                EventBus.getDefault().post(new NavigationEvent(subverseFragment));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListSubverseInfo.size();
    }
}
