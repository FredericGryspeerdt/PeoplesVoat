package team4.howest.be.androidapp.oldcode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;

import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.Navigation.NavigationEvent;

import java.util.List;

import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.model.VoteResponse;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.util.TextDisplayer;
import team4.howest.be.androidapp.view.CommentsFragment;
import team4.howest.be.androidapp.view.UserFragment;
import team4.howest.be.androidapp.view.WebviewActivity;


/**
 * Created by Frederic on 15/11/2015.
 */
public class SubmissionsRVAdapter extends RecyclerView.Adapter<SubmissionsRVAdapter.SubmissionViewHolder>{

    List<Submission> submissionsForSubverse;
    private Context mContext;

    public SubmissionsRVAdapter(Context context, List<Submission> submissionsForSubverse){
        this.submissionsForSubverse = submissionsForSubverse;
        this.mContext = context;
    }


    //viewholder is an object attached to each row in the recyclerview
    public class SubmissionViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvTitle;
        TextView tvUser;
        TextView tvVoteCount;
        TextView tvTime;
        TextView tvCommentCount;
        TextView tvContent;
        public ProgressBar progContent;

        public ImageView imgThumbnail;
        ImageView imgPopupMenu;
        ImageView imgUpvote;
        ImageView imgDownVote;

        public SubmissionViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cvSubmission);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvUser = (TextView)itemView.findViewById(R.id.tvUser);
            tvVoteCount = (TextView) itemView.findViewById(R.id.tvVoteCount);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            progContent = (ProgressBar) itemView.findViewById(R.id.progressBarContent);

            imgThumbnail =(ImageView) itemView.findViewById(R.id.imgThumbnail);
            imgPopupMenu = (ImageView) itemView.findViewById(R.id.imgPopupMenu);
            imgUpvote = (ImageView) itemView.findViewById(R.id.imgUpvote);
            imgDownVote = (ImageView) itemView.findViewById(R.id.imgDownVote);
        }
    }



    @Override
    public int getItemViewType(int position) {
       Submission currentSubm = submissionsForSubverse.get(position);
       return currentSubm.getType();
    }

    @Override
    public SubmissionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //wordt opgeroepen wnn de custom ViewHolder geïnitialiseerd moet worden
        //we geven mee welke layout ieder item in de RecyclerView moet gebruiken
        //we doen dit door de layout te inflaten dmv LayoutInflater

        //View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_submission_cardview, viewGroup,false);
        View v;

        switch (viewType){
            case 1: //self post
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_submission_cardview_4, viewGroup,false);
                break;
            case 2: //link post
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_submission_cardview_3, viewGroup,false);
                break;
            default:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_submission_cardview_3, viewGroup,false);
        }

        //View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_submission_cardview_3, viewGroup,false);

        SubmissionViewHolder svh = new SubmissionViewHolder(v);


        return svh;
    }

    @Override
    public void onBindViewHolder(final SubmissionViewHolder submissionViewHolder, int i) {
        //hier wordt de inhoud van ieder item van de RecyclerView bepaald

        int itemviewType = submissionViewHolder.getItemViewType();

        final Submission submission = submissionsForSubverse.get(i);

        submissionViewHolder.tvCommentCount.setText("" + submission.getCommentCount());
        submissionViewHolder.tvVoteCount.setText("" + (submission.getVoteCount()));
        submissionViewHolder.tvTime.setText(submission.getPrettyDate());
        submissionViewHolder.tvUser.setText(submission.getUserName());
        submissionViewHolder.tvTitle.setText(submission.getTitle());

        //defaults
        ImageView thumbnail = submissionViewHolder.imgThumbnail;

        submissionViewHolder.tvContent.setVisibility(View.GONE);  //tekst gebied
        submissionViewHolder.imgThumbnail.setVisibility(View.GONE); //thumbnail gebied

        switch (submission.getType()){
            case 1: //self post --> geen thumbnail, wel tekst
                submissionViewHolder.tvContent.setVisibility(View.VISIBLE);
                submissionViewHolder.imgThumbnail.setVisibility(View.GONE);

                if (submission.getContent() != null){  //submission bevat tekst
                    submissionViewHolder.tvContent.setText(new TextDisplayer().getSpannedText(submission.getFormattedContent()));
                }

                submissionViewHolder.progContent.setVisibility(View.GONE);  //tekst is ingeladen --> progressbar verwijderen

                break;

            case 2: // link post --> kan thumbnail bevatten

                submissionViewHolder.imgThumbnail.setVisibility(View.VISIBLE);
                submissionViewHolder.progContent.setVisibility(View.VISIBLE);

                String url = submission.getUrl();

                //submission.loadThumbnail(url,submissionViewHolder, mContext, submission);

                break;
        }

        submissionViewHolder.tvCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentsFragment commentsFragment = new CommentsFragment();
                Bundle args = new Bundle();
                args.putString(CommentsFragment.SELECTED_SUBVERSE, submission.getSubverse());
                args.putString(CommentsFragment.SELECTED_SORT, "hot");
                args.putString(CommentsFragment.SELECTED_SUBMISSION, submission.getId().toString());
                commentsFragment.setArguments(args);

                EventBus.getDefault().post(new NavigationEvent(commentsFragment));
            }
        });
        //EventBus.getDefault().post(new SwipeEvent("Swippety swap!"));
        //submissionViewHolder.tvVoteCount.setText("" + submission.getVoteCount());


        submissionViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submission.getUrl() != null) {
                    Intent intent = new Intent(mContext, WebviewActivity.class);
                    intent.putExtra(WebviewActivity.URL, submission.getUrl());
                    mContext.startActivity(intent);
                }

            }
        });

        submissionViewHolder.imgPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_menu_card, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.popup_browser:
                                //Toast.makeText(mContext,submission.getUrl(),Toast.LENGTH_LONG).show();
                                if (submission.getUrl() != null) {
                                    Uri webpage = Uri.parse(submission.getUrl());

                                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                        mContext.startActivity(intent);
                                    }
                                }
                                break;

                            case R.id.popup_bookmark:
                                break;

                            case R.id.popup_comments:
                                CommentsFragment commentsFragment = new CommentsFragment();
                                Bundle args = new Bundle();
                                args.putString(CommentsFragment.SELECTED_SUBVERSE, submission.getSubverse());
                                args.putString(CommentsFragment.SELECTED_SORT, "hot");
                                args.putString(CommentsFragment.SELECTED_SUBMISSION, submission.getId().toString());
                                commentsFragment.setArguments(args);

                                EventBus.getDefault().post(new NavigationEvent(commentsFragment));

                                break;
                            case R.id.popup_hide:
                                break;

                            case R.id.popup_share:
                                break;

                            case R.id.popup_subverse:
                                Toast.makeText(mContext,submission.getSubverse(),Toast.LENGTH_LONG).show();
                                break;

                            case R.id.popup_user:
                                Toast.makeText(mContext,submission.getUserName(),Toast.LENGTH_LONG).show();

                                new VoatClient().getApiService().getUserInfo(submission.getUserName())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Subscriber<User.UserAPIResponse>() {
                                            @Override
                                            public void onCompleted() {
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e("ERROR OCCURRED", e.getMessage());
                                            }

                                            @Override
                                            public void onNext(User.UserAPIResponse userAPIResponse) {
                                                User user = userAPIResponse.getData();
                                                UserFragment userFragment = UserFragment.newInstance(user);

                                                EventBus.getDefault().post(new NavigationEvent(userFragment));

                                            }
                                        });
                                break;

                        }
                        return true;
                    }
                });
            }
        });

        submissionViewHolder.imgUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoatClient restClient = new VoatClient();
                restClient.getApiService().postVote("submission", Integer.toString(submission.getId()), "1")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<VoteResponse>() {
                            @Override
                            public void onCompleted() {
                                /*
                                Toast.makeText(mContext, "completed",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                        */
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(mContext, e.toString(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }

                            @Override
                            public void onNext(VoteResponse submissions) {
                                Toast.makeText(mContext, submissions.getData().getMessage(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });

        submissionViewHolder.imgDownVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoatClient restClient = new VoatClient();
                restClient.getApiService().postVote("comment", Integer.toString(submission.getId()), "-1")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<VoteResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(VoteResponse submissions) {

                                Toast.makeText(mContext, submissions.getData().getMessage(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });




    }

    @Override
    public int getItemCount() {
        //moet het aantal items die in de data aanwezig zijn teruggeven
        //in ons geval is data een list van submissions
        return submissionsForSubverse.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
