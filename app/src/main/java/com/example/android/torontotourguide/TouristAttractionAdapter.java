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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@link TouristAttractionAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link TouristAttraction} objects.
 */
public class TouristAttractionAdapter extends ArrayAdapter<TouristAttraction>  {

    /** Resource ID for the background color for this list of TouristAttractions */
    private int mColorResourceId;

    /**
     * Create a new {@link TouristAttractionAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param touristAttractions is the list of {@link TouristAttraction}s to be displayed.
     * @param colorResourceId is the resource ID for the background color for this list of tourist attractions
     */
    public TouristAttractionAdapter(Context context, ArrayList<TouristAttraction> touristAttractions, int colorResourceId) {
        super(context, 0, touristAttractions);
        mColorResourceId = colorResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link TouristAttraction} object located at this position in the list
        TouristAttraction currentTouristAttraction = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID tourist_attraction_name_text_view.
        TextView touristAttractionNameTextView = (TextView) listItemView.findViewById(R.id.tourist_attraction_name_text_view);
        // Get the name of the currentTouristAttraction object and set this text on the
        // touristAttractionTextView TextView.
        touristAttractionNameTextView.setText(currentTouristAttraction.getName());

        // Find the TextView in the list_item.xml layout with the ID tourist_attraction_location_text_view.
        TextView touristAttractionLocationTextView = (TextView) listItemView.findViewById(R.id.tourist_attraction_location_text_view);
        // Get the location of the currentTouristAttraction object and set this text on the
        // touristAttractionLocationTextView TextView.
        touristAttractionLocationTextView.setText(currentTouristAttraction.getLocation());

        // Set the theme color for the list item
        View textContainer = listItemView.findViewById(R.id.text_container);
        // Find the color that the resource ID maps to
        int color = ContextCompat.getColor(getContext(), mColorResourceId);
        // Set the background color of the text container View
        textContainer.setBackgroundColor(color);

        // Return the whole list item layout (containing 2 TextViews and 1 ImageView) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}