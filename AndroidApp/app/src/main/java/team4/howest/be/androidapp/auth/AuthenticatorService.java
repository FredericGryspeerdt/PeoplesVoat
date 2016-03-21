package team4.howest.be.androidapp.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Srg on 11/11/2015.
 */

public class AuthenticatorService extends Service {

    private Authenticator authenticator;

    public AuthenticatorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        authenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
