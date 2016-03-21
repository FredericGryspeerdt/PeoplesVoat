package team4.howest.be.androidapp.view;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import team4.howest.be.androidapp.R;

/**
 * Created by anthony on 15.17.11.
 */

//https://github.com/rohaanhamid/ScrollableItemList

public class CommentPagerAdapter extends PagerAdapter {

    public Object instantiateItem(ViewGroup collection, int position) {

        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.comment_container;
                break;
            case 1:
                resId = R.id.actions_container;
                break;
        }
        return collection.findViewById(resId);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }
}
