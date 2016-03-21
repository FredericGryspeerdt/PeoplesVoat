package team4.howest.be.androidapp.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Frederic on 16/11/2015.
 */
public class DefaultSubverseResponse {
    ArrayList<String> defaultSubverses;

    public ArrayList<String> getDefaultSubverses() {
        return defaultSubverses;
    }

    public void add(String defaultSubverse){
        if (defaultSubverses == null){
            defaultSubverses = new ArrayList<>();
        }

        defaultSubverses.add(defaultSubverse);
    }
}
