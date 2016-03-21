package team4.howest.be.androidapp.oldcode;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.model.Submission;


/**
 * Created by Frederic on 15/11/2015.
 */
public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.BindingHolder>{
    private List<Submission> submissions;
    private Context mContext;



    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;


        public BindingHolder(View v) {
            super(v);
            binding = DataBindingUtil.bind(v);

        }

        public ViewDataBinding getBinding(){
            return binding;
        }
    }

    public SubmissionAdapter(Context context, List<Submission> submissions){
        this.submissions = submissions;
        this.mContext = context;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //wordt opgeroepen wnn de custom ViewHolder geïnitialiseerd moet worden
        //we geven mee welke layout ieder item in de RecyclerView moet gebruiken
        //we doen dit door de layout te inflaten dmv LayoutInflater

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_submission_cardview_2, parent, false);

        BindingHolder holder = new BindingHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        //hier wordt de inhoud van ieder item van de RecyclerView bepaald
        final Submission aSubmission = submissions.get(position);

        holder.getBinding().setVariable(team4.howest.be.androidapp.BR.submission, aSubmission);
        try {
            holder.getBinding().executePendingBindings();
        } catch (Exception e){
            Log.v("binding", e.toString());
        }



    }

    @Override
    public int getItemCount() {
        //moet het aantal items die in de data aanwezig zijn teruggeven
        //in ons geval is data een list van submissions
        return submissions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
