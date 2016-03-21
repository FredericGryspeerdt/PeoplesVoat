package team4.howest.be.androidapp.service;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.view.MainActivity;
import team4.howest.be.androidapp.view.UserFragment;

/**
 * Created by Srg on 8/11/2015.
 */
public class DrawerHelper {

    private MainActivity mainActivity;
    private final FragmentManager fragmentManager;

    public DrawerHelper(MainActivity activity, FragmentManager manager)
    {
        mainActivity = activity;
        fragmentManager = manager;
    }

    public void showDrawerHeaderUserInfo(String userName)
    {
         new VoatClient().getApiService().getUserInfo(userName)
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

                        fillTextView(user);

                        mainActivity.setUser(user);
                    }
                });
    }

    private void fillTextView(final User user) {
        TextView txtUserName = (TextView) mainActivity.findViewById(R.id.drawer_header_user_name);
        txtUserName.setText(user.getUserName());

        try{
            showDrawerHeaderUserPicture(new URL(user.getProfilePicture()))
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
                            ImageView imgUserPicture = (ImageView) mainActivity.findViewById(R.id.drawer_header_user_image);
                            imgUserPicture.setImageBitmap(bitmap);
                            imgUserPicture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Insert the fragment by replacing any existing fragment
                                    UserFragment userFragment = UserFragment.newInstance(user);
                                    EventBus.getDefault().post(new NavigationEvent(userFragment));
                                    //fragmentManager.beginTransaction().replace(R.id.main_content, userFragment).commit();
                                    DrawerLayout drawer = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);
                                    drawer.closeDrawers();
                                }
                            });
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

    public static void showInfo(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Info");
        builder.setMessage("Icons made by 'Elegant Themes' at www.flaticon.com ");

        builder.setPositiveButton("Ok, cool!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showReviewThisApp(Context context, final AppCompatActivity activity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rate this app");
        builder.setMessage("Click to go to the Play Store");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String storeListing = "https://play.google.com/store/apps/details?id=team4.howest.be.androidapp";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeListing));
                 activity.startActivity(browserIntent);
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
