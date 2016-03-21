package team4.howest.be.androidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.database.Contract;
import team4.howest.be.androidapp.database.DataSource;
import team4.howest.be.androidapp.model.ApiResponse;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.model.VoteResponse;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.util.TextDisplayer;
import team4.howest.be.androidapp.view.CommentsFragment;
import team4.howest.be.androidapp.view.MainActivity;
import team4.howest.be.androidapp.view.SavedSubmissionsFragment;
import team4.howest.be.androidapp.view.SubverseFragment;
import team4.howest.be.androidapp.view.UserFragment;
import team4.howest.be.androidapp.view.WebviewActivity;

/**
 * Created by Frederic on 19/12/2015.
 */
public class SubmissionsAdapter extends RecyclerView.Adapter<MainViewHolder> {

    public static final int TYPE_SELF_POST = 1;
    public static final int TYPE_LINK_POST = 2;

    List<Submission> submissionsForSubverse;

    //private Context mContext;
    private static Boolean isContentExpanded = false;

    private MainActivity mContext;
    private static MainActivity mainActivity;



    public SubmissionsAdapter(MainActivity context, List<Submission> submissionsForSubverse) {
        this.submissionsForSubverse = submissionsForSubverse;
        this.mContext = context;
        this.mainActivity = context;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        FragmentManager fm = mContext.getSupportFragmentManager();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        boolean prefBiggerFont = sharedPreferences.getBoolean("prefShowBiggerFont", true);

        switch (viewType) {
            case TYPE_SELF_POST: //self post
                //ViewGroup vSelfPost = (ViewGroup) mInflater.inflate(R.layout.item_self_submission_card, viewGroup, false); //gebruik layout speciaal voor self posts
                ViewGroup vSelfPost;
                if(prefBiggerFont)
                {
                    vSelfPost = (ViewGroup) mInflater.inflate(R.layout.item_self_submission_card_v2_bigger_font, viewGroup, false); //gebruik layout speciaal voor self posts
                }
                else
                {
                    vSelfPost = (ViewGroup) mInflater.inflate(R.layout.item_self_submission_card_v2, viewGroup, false); //gebruik layout speciaal voor self posts
                }

                SelfPostViewHolder vhSelfPost = new SelfPostViewHolder(vSelfPost);
                return vhSelfPost;

            case TYPE_LINK_POST: //link post
                //ViewGroup vLinkPost = (ViewGroup) mInflater.inflate(R.layout.item_submission_cardview_3, viewGroup, false); //gebruik layout speciaal voor link posts
                ViewGroup vLinkPost;
                if(prefBiggerFont)
                {
                    vLinkPost = (ViewGroup) mInflater.inflate(R.layout.item_link_submission_card_bigger_font, viewGroup, false); //gebruik layout speciaal voor link posts
                }
                else
                {
                    vLinkPost = (ViewGroup) mInflater.inflate(R.layout.item_link_submission_card, viewGroup, false); //gebruik layout speciaal voor link posts
                }
                LinkPostViewHolder vhLinkPost = new LinkPostViewHolder(vLinkPost);
                return vhLinkPost;

            default: //Als de VOAT API goed werkt, is dit onmogelijk!

                ViewGroup vDefault = (ViewGroup) mInflater.inflate(R.layout.item_self_submission_card_v2, viewGroup, false);

                LinkPostViewHolder vhDefault = new LinkPostViewHolder(vDefault);
                return vhDefault;
        }
    }

    @Override
    public void onBindViewHolder(MainViewHolder viewHolder, int position) {

        final Submission submission = submissionsForSubverse.get(position);

        bindSubmissionHolder(viewHolder, submission, mContext);
    }

    public static void bindSubmissionHolder(MainViewHolder viewHolder, final Submission submission, final Context mContext) {
        MainViewHolder mainViewHolder = viewHolder;

        //beide card layouts bevatten deze views
        mainViewHolder.tvCommentCount.setText("" + submission.getCommentCount());
        mainViewHolder.tvVoteCount.setText("" + (submission.getVoteCount()));
        mainViewHolder.tvTime.setText(submission.getPrettyDate());
        mainViewHolder.tvUser.setText(submission.getUserName());
        mainViewHolder.tvTitle.setText(submission.getTitle());

        switch (viewHolder.getItemViewType()) {
            case TYPE_SELF_POST:
                final SelfPostViewHolder selfPostViewHolder = (SelfPostViewHolder) mainViewHolder;

                if (submission.getContent() != null) {  //submission bevat tekst
                    selfPostViewHolder.tvContent.setText(new TextDisplayer().getSpannedText(submission.getFormattedContent()));

                    selfPostViewHolder.tvContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*
                            if (isContentExpanded){
                                selfPostViewHolder.tvContent.setMaxLines(4);
                                isContentExpanded = false;
                            } else {
                                selfPostViewHolder.tvContent.setMaxLines(Integer.MAX_VALUE);
                                isContentExpanded = true;
                            }
                            */

                            //selfPostViewHolder.tvContent.postInvalidate();
                            //v.requestLayout();

                            goToComments(submission);
                        }
                    });
                }

                selfPostViewHolder.progContent.setVisibility(View.GONE);  //tekst is ingeladen --> progressbar verwijderen
                break;

            case TYPE_LINK_POST:

                LinkPostViewHolder linkPostViewHolder = (LinkPostViewHolder) mainViewHolder;

                final String url = submission.getUrl();

                submission.loadThumbnail(url, linkPostViewHolder, mContext);
                linkPostViewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWebview(mContext, url);
                    }
                });

                break;
        }


        mainViewHolder.tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserProfile(submission.getUserName());
            }
        });


        mainViewHolder.imgComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToComments(submission);
            }
        });

        mainViewHolder.tvCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToComments(submission);
            }
        });
        //EventBus.getDefault().post(new SwipeEvent("Swippety swap!"));
        //submissionViewHolder.tvVoteCount.setText("" + submission.getVoteCount());


        mainViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submission.getUrl() != null) { //link post --> go to link

                    goToWebview(mContext, submission.getUrl());

                } else { //self post --> go to comments
                    goToComments(submission);
                }

            }
        });

        mainViewHolder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserProfile(submission.getUserName());

            }
        });

        mainViewHolder.imgPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                MenuInflater inflater = popup.getMenuInflater();

                if(mainActivity.watchingSavedSubmissions)//saved submissions menu
                    inflater.inflate(R.menu.popup_menu_card_saved, popup.getMenu());
                else
                    inflater.inflate(R.menu.popup_menu_card, popup.getMenu()); //normale menu

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
                                        mContext.startActivity(Intent.createChooser(intent, "Choose a browser to open this link ..."));
                                    }
                                } else {
                                    Toast.makeText(mContext, "This is not a link post", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.popup_bookmark:

                                DataSource dataSource = new DataSource(mContext);//slaat lokaal op
                                dataSource.saveSubmission(submission);

                                bookmarkSubmission(mContext, submission.getId());//slaat op Voat.co op

                                break;

                            case R.id.popup_comments:
                                goToComments(submission);
                                break;

                            case R.id.popup_hide:
                                break;

                            case R.id.popup_share:
                                String message =
                                        submission.getUserName() + ": \"" + submission.getTitle() + submission.getContent() + "\" (" + submission.getPrettyDate() + ", " + "http://fakevout.azurewebsites.net/v/" + submission.getSubverse() + "/comments/" + submission.getId() + ")";
                                String messageHtml =
                                        "<p>" + submission.getUserName() + ": \"[" + submission.getTitle() + "] " + submission.getContent() + "\" (" + submission.getPrettyDate() + ", <a href='" + "http://fakevout.azurewebsites.net/v/" + submission.getSubverse() + "/comments/" + submission.getId() + "'>" + submission.getSubverse() + "/" + submission.getId() + "</a>)</p>";
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                                sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, messageHtml);
                                sendIntent.setType("text/*");
                                mContext.startActivity(Intent.createChooser(sendIntent, "Share this post with.."));
                                break;

                            case R.id.popup_subverse:
                                SubverseFragment redirectFrag = SubverseFragment.newInstance
                                        (submission.getSubverse(), "new");
                                EventBus.getDefault().post(new NavigationEvent(redirectFrag));

                                Toast.makeText(mContext, submission.getSubverse(), Toast.LENGTH_LONG).show();
                                break;

                            case R.id.popup_user:
                                //Toast.makeText(mContext,submission.getUserName(),Toast.LENGTH_LONG).show();
                                goToUserProfile(submission.getUserName());
                                break;
                            case R.id.popup_delete_bookmark:
                                DataSource dataSrc = new DataSource(mContext);
                                int res = dataSrc.deleteSubmission(submission);
                                if(res>0)
                                {
                                    Toast.makeText(mContext, "Succesfully deleted", Toast.LENGTH_LONG).show();

                                    SavedSubmissionsFragment savedSubmissionsFragment = new SavedSubmissionsFragment();
                                    EventBus.getDefault().post(new NavigationEvent(savedSubmissionsFragment));
                                }
                                else
                                {
                                    Toast.makeText(mContext,"Could not delete",Toast.LENGTH_LONG).show();
                                }

                                break;

                        }
                        return true;
                    }
                });
            }
        });

        mainViewHolder.imgUpvote.setOnClickListener(new View.OnClickListener() {
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
                                //Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();

                                Toast.makeText(mContext, "You must be logged in to vote",
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

        mainViewHolder.imgDownVote.setOnClickListener(new View.OnClickListener() {
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
                                //Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();

                                Toast.makeText(mContext, "You must be logged in to vote",
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
    }



    private static void bookmarkSubmission(final Context context, Integer submissionID) {
        VoatClient voatClient = new VoatClient();
        VoatApiService voatApiService = voatClient.getApiService();

        voatApiService.saveSubmission(submissionID, new Callback<ApiResponse>() {
            @Override
            public void success(ApiResponse apiResponse, Response response) {
                if (apiResponse.getSuccess()){
                    Toast.makeText(context, "Submission bookmarked",Toast.LENGTH_LONG).show();

                } else {
                    String errorType = apiResponse.getError().getType();
                    String errorMessage= apiResponse.getError().getMessage();

                    Toast.makeText(context, errorType + ": " + errorMessage,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "You must be logged in to vote",Toast.LENGTH_LONG).show();
            }
        });



    }

    //region HELPER METHODS
    private static void goToWebview(Context context, String url) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra(WebviewActivity.URL, url);
        context.startActivity(intent);
    }

    private static void goToComments(Submission submission) {
        CommentsFragment commentsFragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(CommentsFragment.SELECTED_SUBVERSE, submission.getSubverse());
        args.putString(CommentsFragment.SELECTED_SORT, "hot");
        args.putString(CommentsFragment.SELECTED_SUBMISSION, submission.getId().toString());
        commentsFragment.setArguments(args);

        EventBus.getDefault().post(new NavigationEvent(commentsFragment));
    }

    private static void goToUserProfile(String username) {
        new VoatClient().getApiService().getUserInfo(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<User.UserAPIResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(User.UserAPIResponse userAPIResponse) {
                        User user = userAPIResponse.getData();
                        UserFragment userFragment = UserFragment.newInstance(user);

                        EventBus.getDefault().post(new NavigationEvent(userFragment));

                    }
                });
    }

    //endregion

    @Override
    public int getItemCount() {
        //moet het aantal items die in de data aanwezig zijn teruggeven
        //in ons geval is data een list van submissions
        return submissionsForSubverse.size();
    }

    @Override
    public int getItemViewType(int position) {
        Submission currentSubm = submissionsForSubverse.get(position);

        switch (currentSubm.getType()) { //kijk welk type de huidige submission is
            case 1: //self post
                return TYPE_SELF_POST;
            case 2: //link post
                return TYPE_LINK_POST;
        }

        return currentSubm.getType();
    }

    public void clear() {
        submissionsForSubverse.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Submission> list) {
        submissionsForSubverse.addAll(list);
        notifyDataSetChanged();
    }

}
