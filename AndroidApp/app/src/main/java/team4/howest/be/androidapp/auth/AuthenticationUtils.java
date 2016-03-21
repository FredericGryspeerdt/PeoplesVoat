package team4.howest.be.androidapp.auth;

import retrofit.mime.TypedString;

/**
 * Created by Srg on 11/11/2015.
 */

public class AuthenticationUtils {

    public static TypedString getAuthenticationString(String username, String password)
    {   //return type = Typed String (@Body van retrofit werkt met TypedString)
        return new TypedString("grant_type=password&username="+username+"&password="+password);
    }

    public void isTokenExpired(String accessToken) throws Exception {

    }
}
