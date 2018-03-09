/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.torontotourguide;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * {@link TouristAttractionCategoryAdapter} is a {@link FragmentPagerAdapter} that can provide the layout for
 * each list item based on a data source which is a list of {@link TouristAttraction} objects.
 */
public class TouristAttractionCategoryAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;
    private TabLayout tabLayout = null;

    /**
     * Create a new {@link TouristAttractionCategoryAdapter} object.
     *
     * @param context is the context of the app
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public TouristAttractionCategoryAdapter(Context context, FragmentManager fm, TabLayout tabLayout) {
        super(fm);
        mContext = context;
        this.tabLayout = tabLayout;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            tabLayout.getTabAt(position).setIcon(R.drawable.ic_photo_black_24dp);
            return new GalleryFragment();
        } else if (position == 1) {
            tabLayout.getTabAt(position).setIcon(R.drawable.ic_local_activity_black_24dp);
            return new MuseumFragment();
        } else if (position == 2) {
            tabLayout.getTabAt(position).setIcon(R.drawable.ic_restaurant_black_24dp);
            return new RestaurantFragment();
        } else {
            tabLayout.getTabAt(position).setIcon(R.drawable.ic_event_black_24dp);
            return new EventFragment();
        }
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 4;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        if (position == 0) {
//            return mContext.getString(R.string.galleries);
//        } else if (position == 1) {
//            return mContext.getString(R.string.museums);
//        } else if (position == 2) {
//            return mContext.getString(R.string.restaurants);
//        } else {
////            tabLayout.getTabAt(i).setIcon();
//            return mContext.getString(R.string.events);
//        }
//    }
}
