package team4.howest.be.androidapp.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import team4.howest.be.androidapp.database.Contract;
import team4.howest.be.androidapp.view.MainActivity;

/**
 * Created by Srg on 9/11/2015.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private Context context;
    private Handler handler;

    public Authenticator(Context context) {
        super(context);
        this.context = context;

        handler = new Handler();
    }


    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        if(!accountType.equals(Contract.ACCOUNT_TYPE))
            throw new  IllegalArgumentException();

        if(!(requiredFeatures == null ||  requiredFeatures.length == 0))
            throw new IllegalArgumentException();

        Account[] accs = AccountManager.get(context).getAccountsByType(Contract.ACCOUNT_TYPE);
        Account acc = null;

        if(accs.length>0)
        {
            acc = (Account) accs[0];
        }

        if(acc!=null)//Als er al een account bestaat, (terug) naar MainActivity
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "An account already exists", Toast.LENGTH_LONG).show();
                }
            });
            return createMainActivityBundle();
        }

        return createAuthenticatorActivityBundle(response); //Account aanmaken als er nog geen account bestaat
    }


    public boolean deleteAccount()
    {
        boolean isDeleted = false;

        AccountManager accountManager = AccountManager.get(context);
        Account account = accountManager.getAccountsByType(Contract.ACCOUNT_TYPE)[0];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            isDeleted = accountManager.removeAccountExplicitly(account);
        }
        else
        {
            accountManager.removeAccount(account, null, null);
            return true; //zal wel oke zijn
        }

        return isDeleted;
    }

    private Bundle createAuthenticatorActivityBundle(AccountAuthenticatorResponse response) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    private Bundle createMainActivityBundle() {
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
