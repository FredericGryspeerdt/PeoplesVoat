package team4.howest.be.androidapp.model;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import team4.howest.be.androidapp.R;

/**
 * Created by Srg on 2/11/2015.
 */
public class Drawer {

    public static ArrayList menuItems = new ArrayList<>();

    static {
        Collections.addAll( menuItems,"Subscriptions","My subverses","Manage subverses",
                "Account","Messages","Saved items","Delete account",
                "Voat","Browse subverses", "Search submissions", "Random subverse",
                "Settings", "Help", "Review app");
    }

}
