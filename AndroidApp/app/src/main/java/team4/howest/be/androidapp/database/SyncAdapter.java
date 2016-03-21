package team4.howest.be.androidapp.database;

/**
 * Created by Srg on 4/01/2016.
 */

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.service.VoatClient;


public class SyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    private Context context;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
        this.context = context;
    }


    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
        this.context = context;

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        fetchSubmissionsForUser(account.name);
    }

    private void fetchSubmissionsForUser(String user) {

        new VoatClient().getApiService().getUserInfo(user)
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
                        DataSource dataSource = new DataSource(context);
                        int del = dataSource.deleteUser(user);

                        long res = dataSource.saveUser(user);

                        User usr = dataSource.getUser();

                    }
                });
    }
}