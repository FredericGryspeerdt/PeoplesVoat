package team4.howest.be.androidapp.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.database.DataSource;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.service.Serializer;

public class UserFragment extends Fragment {
    private static final String USER_INFO = "team4.howest.be.androidapp.view.USER_INFO";

    public User user;

    private ViewPager viewPager;
    private CustomFragmentPagerAdapter customFragmentPagerAdapter;

    public static UserFragment newInstance(User user) {
        UserFragment fragment = new UserFragment();
        try
        {
            Bundle args = new Bundle();
            args.putSerializable(USER_INFO, Serializer.serialize(user));
            fragment.setArguments(args);
        }
        catch (IOException e)
        {
        }

        return fragment;
    }

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try{
                user = (User) Serializer.deserialize((byte[])getArguments().getSerializable(USER_INFO));
            }
            catch (Exception e)
            {
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        if (getArguments() != null) {
            try{
                user = (User) Serializer.deserialize((byte[])getArguments().getSerializable(USER_INFO));
            }
            catch (Exception e)
            {

            }
        }

        MainActivity ma = (MainActivity) getContext();

        if(!ma.isConnected)
        {
            DataSource dataSource = new DataSource(getContext());
            user = dataSource.getUser();
        }
        else
        {
            showUserProfilePicture((ImageView)view.findViewById(R.id.user_profile_picture), user.getProfilePicture());
        }

        try
        {
            //NAME
            TextView txtUserName = (TextView) view.findViewById(R.id.user_name);
            txtUserName.setText(user.getUserName());

            //SUBMISSION POINTS
            TextView txtSPs = (TextView) view.findViewById(R.id.user_submission_points_sum);
            txtSPs.setText(user.getSubmissionPoints().get("sum"));

            //COMMENT POINTS
            TextView txtCPs = (TextView) view.findViewById(R.id.user_comment_points_sum);
            txtCPs.setText(user.getCommentPoints().get("sum"));

            //SUBMISSION VOTES
            if(user.getSubmissionVoting()!=null)
            {
                TextView txtSVs = (TextView) view.findViewById(R.id.user_submission_votes_sum);
                txtSVs.setText(user.getSubmissionVoting().get("sum"));
            }
            else
            {
                TextView txtSVs = (TextView) view.findViewById(R.id.user_submission_votes_sum);
                txtSVs.setText("0");
            }


            //COMMENT VOTES
            if(user.getCommentVoting()!=null)
            {
                TextView txtCVs = (TextView) view.findViewById(R.id.user_comment_votes_sum);
                txtCVs.setText(user.getCommentVoting().get("sum"));
            }
            else
            {
                TextView txtCVs = (TextView) view.findViewById(R.id.user_comment_votes_sum);
                txtCVs.setText("0");
            }

            //ViewPager opvullen met de Adapter (tab view)
            customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getActivity().getSupportFragmentManager());
            viewPager = (ViewPager) view.findViewById(R.id.user_tab_view);
            viewPager.setAdapter(customFragmentPagerAdapter);
        }
        catch (Exception e)
        {

        }
        
        return view;
    }

    private void showUserProfilePicture(final ImageView userImg, String url)
    {
        try{
            showDrawerHeaderUserPicture(new URL(url))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Bitmap>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            userImg.setImageBitmap(bitmap);
                        }
                    });
        }
        catch (MalformedURLException e)
        {

        }
    }

    private Observable<Bitmap> showDrawerHeaderUserPicture(URL url)
    {
        return Observable.defer(() -> Observable.just(getImageFromUrl(url)));
    }

    private Bitmap getImageFromUrl(URL url)
    {
        Bitmap bitmap = null;

        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        }
        catch (IOException e)
        {

        }

        return bitmap;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().supportInvalidateOptionsMenu(); //doet de search-button weg indien niet meer van toepassing
    }

    public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
        public CustomFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
            {
                SubverseFragment subverseFragment = new SubverseFragment();
                Bundle args = new Bundle();
                args.putString(subverseFragment.SELECTED_USER, user.getUserName());
                subverseFragment.setArguments(args);

                return subverseFragment;
            }
            else
            {
                CommentsFragment commentsFragment = new CommentsFragment();
                Bundle args = new Bundle();

                args.putBoolean(CommentsFragment.USERTHREAD, true);
                args.putString(CommentsFragment.USER, user.getUserName());

                commentsFragment.setArguments(args);

                return commentsFragment;
            }
        }

        @Override
        public CharSequence getPageTitle (int position) {
            if(position==0)
            {
                return "My submissions";
            }
            else
            {
                return "My comments";
            }
        }
    }
}
