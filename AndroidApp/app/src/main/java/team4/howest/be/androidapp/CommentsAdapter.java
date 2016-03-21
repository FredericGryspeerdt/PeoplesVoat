package team4.howest.be.androidapp;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import team4.howest.be.androidapp.adapters.LinkPostViewHolder;
import team4.howest.be.androidapp.adapters.MainViewHolder;
import team4.howest.be.androidapp.adapters.SelfPostViewHolder;
import team4.howest.be.androidapp.adapters.SubmissionsAdapter;
import team4.howest.be.androidapp.databinding.ItemCommentBinding;
import team4.howest.be.androidapp.model.Comment;
import team4.howest.be.androidapp.model.CommentsMarshaller;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.util.TextDisplayer;
import team4.howest.be.androidapp.view.CommentPagerAdapter;
import team4.howest.be.androidapp.view.WrapContentHeightViewPager;
import team4.howest.be.androidapp.viewmodel.ItemCommentViewModel;

/**
 * Created by anthony on 15.18.10.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Comment> comments;
    private final List<Comment> commentsOriginal;
    private final CommentsMarshaller commentsMarshaller;
    private Context context;

    public Submission submission;

    private enum SwipedState {
        SHOWING_PRIMARY_CONTENT,
        SHOWING_SECONDARY_CONTENT
    }

    private static final int TYPE_ITEM = 3;

    private List<SwipedState> mItemSwipedStates;

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            if (submission != null) {
                switch (submission.getType()) { //kijk welk type de huidige submission is
                    case 1: //self post
                        return SubmissionsAdapter.TYPE_SELF_POST;
                    case 2: //link post
                        return SubmissionsAdapter.TYPE_LINK_POST;
                }
            }
            return SubmissionsAdapter.TYPE_SELF_POST;
        }
        return TYPE_ITEM;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ItemCommentViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private ItemCommentBinding binding;
        public TextView content;
        public TextView collapsed;

        public ItemCommentViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            content = (TextView) itemView.findViewById(R.id.status_name);
            collapsed = (TextView) itemView.findViewById(R.id.more_comments);
            view = (WrapContentHeightViewPager) itemView.findViewById(R.id.viewPager);
        }

        public void bind(Comment comment) {
            binding.setComment(
                    new ItemCommentViewModel(
                            itemView.getContext(),
                            comment,
                            new ClickHandlers(comment)
                    )
            );
        }

    }

    public class ClickHandlers implements ItemCommentViewModel.CommentEventHandler {

        public final Comment comment;

        public ClickHandlers(Comment comment) {
            this.comment = comment;
        }

        public void collapseComment(View view) {
            int position = -1;
            if (comment.getCollapsed().get() == false){
                    if(getPosition(comment, comments) < comments.size() && comments.get(getPosition
                        (comment,
                        comments)) != null
                    && comments.get(getPosition(comment, comments)).getLevel() >
                    comment.getLevel()) {
                        comment.setCollapsed(true);
                        position = getPosition(comment, comments);
                        int itemCount = 0;
                        while (
                                position < comments.size() &&  comments.get(position).getLevel() > comment.getLevel()) {
                            comments.get(position).setHidden(true);
                            comments.remove(position);
                            itemCount += 1;
                        }
                        notifyItemChanged(getPosition(comment, comments));
                        notifyItemRangeRemoved(getPosition(comment, comments) + 1, itemCount);
                    }
            } else if (commentsOriginal.get(getPosition(comment, commentsOriginal)) != null &&
                        commentsOriginal.get(getPosition(comment, commentsOriginal)).getLevel() > comment
                    .getLevel()) {
                comment.setCollapsed(false);
                position = getPosition(comment, commentsOriginal);

                int currentPos = position;
                ArrayList<Comment> datumArrayList = new ArrayList<>();
                while (currentPos < commentsOriginal.size() && commentsOriginal.get(currentPos) !=
                        null &&
                        commentsOriginal.get(currentPos)
                        .getLevel() > comment.getLevel()) {
                    //Skip comment trees die collapsed zijn
                    if (commentsOriginal.get(currentPos).getCollapsed().get()) {
                        int collapsedLevel = commentsOriginal.get(currentPos).getLevel();
                        datumArrayList.add(commentsOriginal.get(currentPos));
                        do {
                            currentPos += 1;
                        } while (commentsOriginal.get(currentPos + 1) != null && commentsOriginal
                                .get
                                (currentPos + 1).getLevel() > collapsedLevel);
                    } else {
                        datumArrayList.add(commentsOriginal.get(currentPos));
                    }
                    currentPos += 1;
                }
                notifyItemChanged(getPosition(comment, comments));
                comments.addAll(getPosition(comment, comments), datumArrayList);
                notifyItemRangeInserted(getPosition(comment, comments) + 1, datumArrayList.size());
            }
        }
    }

    public static CommentsAdapter newInstance() {
        List<Comment> comments = new ArrayList<>();
        CommentsMarshaller datumMarshaller = new CommentsMarshaller();
        return new CommentsAdapter(comments, datumMarshaller);
    }

    private int getPosition(Comment comment, List<Comment> commentList) {
        for (int i = 1; i < commentList.size()+1; i++) {
            if (comment.getId() == commentList.get(i-1).getId()) {
                return i;
            }
        }
        return -1;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentsAdapter(List<Comment> comments, CommentsMarshaller datumMarshaller) {
        this.comments = comments;
        this.commentsOriginal = new ArrayList<>();
        this.commentsMarshaller = datumMarshaller;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {

        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder

        /*
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View CommentContainer = layoutInflater.inflate(R.layout.item_comment, parent, false);
        return new ItemCommentViewHolder(CommentContainer);
        */


            // Create a new view which is basically just a ViewPager in this case
            View v = (View) layoutInflater.inflate(R.layout.item_comment, parent, false);

            CommentPagerAdapter adapter = new CommentPagerAdapter();

            ((WrapContentHeightViewPager) v.findViewById(R.id.viewPager)).setAdapter(adapter);

            //Perhaps the first most crucial part. The ViewPager loses its width information when it is put
            //inside a RecyclerView. It needs to be explicitly resized, in this case to the width of the
            //screen. The height must be provided as a fixed value.

            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            v.getLayoutParams().width = displayMetrics.widthPixels;
            v.requestLayout();

            ItemCommentViewHolder vh = new ItemCommentViewHolder(v);
            return vh;
        }

                if(viewType == SubmissionsAdapter.TYPE_SELF_POST) {
                    ViewGroup vSelfPost = (ViewGroup) layoutInflater.inflate(R.layout.item_self_submission_card_v2,
                            parent, false); //gebruik layout speciaal voor self posts

                    SelfPostViewHolder vhSelfPost = new SelfPostViewHolder(vSelfPost);
                    vhSelfPost.tvContent.setMaxLines(Integer.MAX_VALUE);
                    return vhSelfPost;
                } else {
                    ViewGroup vSelfPost = (ViewGroup) layoutInflater.inflate(R.layout.item_link_submission_card,
                            parent, false); //gebruik layout speciaal voor self posts

                    LinkPostViewHolder vhSelfPost = new LinkPostViewHolder(vSelfPost);
                    return vhSelfPost;
                }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ItemCommentViewHolder) {
            ItemCommentViewHolder currentHolder = (ItemCommentViewHolder) holder;
                Comment comment = comments.get(position-1);
            currentHolder.bind(comment);

            currentHolder.content.setText(new TextDisplayer().getSpannedText(
                    comment.getFormattedContent()),
                    TextView.BufferType.SPANNABLE
            );
            currentHolder.content.setMovementMethod(LinkMovementMethod.getInstance());

            if (comment.getCollapsed().get()) {
                currentHolder.collapsed.setVisibility(View.VISIBLE);
            } else {
                currentHolder.collapsed.setVisibility(View.GONE);
            }

            ((WrapContentHeightViewPager) currentHolder.view).setCurrentItem(mItemSwipedStates
                    .get(position-1).ordinal());
            ((WrapContentHeightViewPager) currentHolder.view).setOnPageChangeListener(new WrapContentHeightViewPager.OnPageChangeListener() {
                int previousPagePosition = 0;

                @Override
                public void onPageScrolled(int pagePosition, float positionOffset, int positionOffsetPixels) {
                    if (pagePosition == previousPagePosition)
                        return;

                    switch (pagePosition) {
                        case 0:
                            mItemSwipedStates.set(position, SwipedState.SHOWING_PRIMARY_CONTENT);
                            break;
                        case 1:
                            mItemSwipedStates.set(position, SwipedState.SHOWING_SECONDARY_CONTENT);
                            break;

                    }
                    previousPagePosition = pagePosition;

                }

                @Override
                public void onPageSelected(int pagePosition) {
                    //This method keep incorrectly firing as the RecyclerView scrolls.
                    //Use the one above instead
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

        } else if (holder instanceof MainViewHolder){
            MainViewHolder mainViewHolder = (MainViewHolder) holder;

            if(submission != null) {
                mainViewHolder.cv.setVisibility(View.VISIBLE);
                SubmissionsAdapter.bindSubmissionHolder(mainViewHolder, submission, context);
            } else {
                mainViewHolder.cv.setVisibility(View.GONE);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }

    public void setSubmission(Submission submission){
        this.submission = submission;
        notifyDataSetChanged();
    }

    public void setComments(List<Comment> comments, Submission submission) {
        this.comments.clear();
        //List<Comment> datumList = commentsMarshaller.marshall(comments);

        this.comments.addAll(comments);
        this.commentsOriginal.addAll(comments);

        //this.submission = submission;

        mItemSwipedStates = new ArrayList<>();
        for (int i = 0; i < this.comments.size()+1; i++) {
            mItemSwipedStates.add(i, SwipedState.SHOWING_PRIMARY_CONTENT);
        }

        notifyDataSetChanged();
    }
}
