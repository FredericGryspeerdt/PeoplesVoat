package team4.howest.be.androidapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.view.MainActivity;

public class AuthenticationActivity extends AppCompatActivity implements AuthenticationFragment.OnAuthenticatedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authentication);
    }

    //Interface van Authenticator Fragment
    @Override
    public void onAuthenticated(Intent result) {
        setResult(RESULT_OK, result);

        Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
