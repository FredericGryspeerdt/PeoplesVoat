package team4.howest.be.androidapp.Navigation;


import android.support.v4.app.Fragment;

/**
 * Created by anthony on 15.18.11.
 */
public class NavigationEvent {
    public final Fragment fragment;


    public NavigationEvent(Fragment fragment){
        this.fragment = fragment;
    }
}