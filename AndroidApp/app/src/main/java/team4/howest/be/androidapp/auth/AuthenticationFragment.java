package team4.howest.be.androidapp.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.database.Contract;
import team4.howest.be.androidapp.model.Authentication;
import team4.howest.be.androidapp.service.VoatClient;

public class AuthenticationFragment extends Fragment {

    private OnAuthenticatedListener activity;
    private AccountManager accountManager;

    public static AuthenticationFragment newInstance() {
        AuthenticationFragment fragment = new AuthenticationFragment();
        return fragment;
    }

    public AuthenticationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        Button btnLogIn = (Button) view.findViewById(R.id.authenticator_log_in);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtName = (TextView) view.findViewById(R.id.authenticator_name);
                String name = txtName.getText().toString();
                TextView txtPassword = (TextView) view.findViewById(R.id.authenticator_password);
                String password = txtPassword.getText().toString();

                if(name.length() >0 && password.length()>0)
                    authenticate(name, password);
                else
                    Toast.makeText(getActivity(), "You forgot to enter your credentials", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void authenticate(String name, String password) {
        VoatClient voatClient = new VoatClient();
        voatClient.getApiService().authenticate(AuthenticationUtils.getAuthenticationString(name, password), new Callback<Authentication>() {
            @Override
            public void success(Authentication authentication, Response response) {
                onAuthenticated(authentication);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Didn't work. Maybe wrong credentials?", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onAuthenticated(Authentication authenticationResult) {
        Account account = new Account(authenticationResult.getUserName(), Contract.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, "access_token", authenticationResult.getAccess_token());

        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, authenticationResult.getUserName());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Contract.ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authenticationResult.getAccess_token());
        intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, "access_token");

        saveExpiryTime(authenticationResult.getExpires_in()); //de expire-time opslaan op internal storage

        activity.onAuthenticated(intent);
    }

    private void saveExpiryTime(long expiresIn) {
        String FILENAME = "expiry_time";
        String expiryTime =  (Calendar.getInstance().getTime().getTime()/1000 + expiresIn - 100000)+"";

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = ((Context) activity).openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fileOutputStream.write(expiryTime.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (OnAuthenticatedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public interface OnAuthenticatedListener {
        public void onAuthenticated(Intent result);
    }
}
