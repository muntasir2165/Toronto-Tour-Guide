package com.example.android.torontotourguide;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * {@link Fragment} that displays a list of galleries.
 */
public class GalleryFragment extends Fragment {

    private View rootView = null;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tourist_attraction_list, container, false);

        // Get a list of galleries from the MainActivity and set it to the galleries variable when
        // we receive some data from the MainActivity.GetTouristAttraction.getGalleryListing() method
        while (MainActivity.GetTouristAttraction.getGalleryListing().size() == 0) {}
        final ArrayList<TouristAttraction> galleryList = MainActivity.GetTouristAttraction.getGalleryListing();

        // Create an {@link TouristAttractionAdapter}, whose data source is a list of {@link TouristAttraction}s. The
        // adapter knows how to create list items for each item in the list.
        TouristAttractionAdapter adapter = new TouristAttractionAdapter(getActivity(), galleryList, R.color.gallery_listing);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // tourist_attraction_list.xml layout file.
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        // Make the {@link ListView} use the {@link TouristAttractionAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link TouristAttraction} in the list.
        listView.setAdapter(adapter);

        // Set a click listener to open the tourist attraction's nowtoronto.com webpage when the list item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the {@link TouristAttraction} object at the given position the user clicked on
                TouristAttraction touristAttraction = galleryList.get(position);
                String touristAttractionInfoUrl = touristAttraction.getUrl();
                // Create a new intent to view the tourist attraction info URI
                Intent openWebPageIntent = new Intent(Intent.ACTION_VIEW);
                // Convert the String URL into a URI object (to set data on the Intent openWebPageIntent)
                openWebPageIntent.setData(Uri.parse(touristAttractionInfoUrl));
                // Send the intent to launch a new activity
                startActivity(openWebPageIntent);
            }
        });

        return rootView;
    }
}