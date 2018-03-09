package com.example.android.torontotourguide;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private String LOG_TAG = GalleryFragment.class.getSimpleName();
    private ArrayList<TouristAttraction> galleries;
    private View rootView = null;

    public EventFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = getActivity().getApplication();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tourist_attraction_list, container, false);

        // Create a list of galleries
        galleries = new ArrayList<TouristAttraction>();
        new GetGalleries().execute();

//        // Create an {@link TouristAttractionAdapter}, whose data source is a list of {@link TouristAttraction}s. The
//        // adapter knows how to create list items for each item in the list.
//        TouristAttractionAdapter adapter = new TouristAttractionAdapter(getActivity(), galleries, R.color.gallery_listing);
//
//        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
//        // There should be a {@link ListView} with the view ID called list, which is declared in the
//        // tourist_attraction_list.xml layout file.
//        ListView listView = (ListView) rootView.findViewById(R.id.list);
//
//        // Make the {@link ListView} use the {@link TouristAttractionAdapter} we created above, so that the
//        // {@link ListView} will display list items for each {@link TouristAttraction} in the list.
//        listView.setAdapter(adapter);

        // Set a click listener to play the audio when the list item is clicked on
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                //open webpage
//            }
//        });

        return rootView;
    }

    private class GetGalleries extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            //url to get the top trending tags
            String url = "https://nowtoronto.com/api/search/event/all/get_search_results";

            String jsonString = "";
            try {
                //make a request to the URL
                jsonString = sh.makeHttpRequest(createUrl(url));

            } catch (IOException e) {
                return null;
            }

            Log.e(LOG_TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
                    //create a new JSONObject
                    JSONObject jsonObject = new JSONObject(jsonString);
                    //get the JSONArray node and name it "results"
                    JSONArray results = jsonObject.getJSONArray("results");

                    // looping through all JSON objects in results
                    for (int i = 0; i < results.length(); i++) {
                        //get the JSONObject and its three attributes
                        JSONObject currentGalleryItem = results.getJSONObject(i);
                        String galleryName = currentGalleryItem.getString("title");
                        String html = currentGalleryItem.getString("html");

                        //extract the url with more info about the event from the html string
                        //System.out.println(html.indexOf("<a href=")); //62
                        //find the index of the first occurrence of "<a href=\"" in the html string
                        int url_href_start_index = html.indexOf("<a href=\"");
                        //find the occurrence of the first of occurrence of "\">" right after "<a href=\"" in the html string
                        int url_href_end_index = html.indexOf("\">", url_href_start_index);
                        String galleryUrl = html.substring(url_href_start_index + "<a href=\"".length(), url_href_end_index);

                        //extract the location of the event from the html string
                        //find the index of the second occurrence of "<a href=\"" in the html string
                       // int location_href_start_index = html.indexOf("<a href=\"", url_href_end_index);
                        int location_mention_start_index = html.indexOf("/locations/", url_href_end_index);
                        int location_href_start_index = html.indexOf("\">", location_mention_start_index);
                        //find the occurrence of the second of occurrence of "\">" right after "<a href=\"" in the html string
//                        int location_href_end_index = html.indexOf("\">", location_href_start_index);
                        int location_href_end_index = html.indexOf("</a>", location_href_start_index);

                        String galleryLocation = html.substring(location_href_start_index + "\">".length(), location_href_end_index);

                        //create a tag object with the parsed data
                        TouristAttraction gallery = new TouristAttraction(galleryName, galleryLocation, galleryUrl);
                        // adding a gallery to our galleries list
                        galleries.add(gallery);
                    }
                } catch (final JSONException e) {
                    Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(LOG_TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                return null;
            }
            return url;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Create an {@link TouristAttractionAdapter}, whose data source is a list of {@link TouristAttraction}s. The
            // adapter knows how to create list items for each item in the list.
            TouristAttractionAdapter adapter = new TouristAttractionAdapter(getActivity(), galleries, R.color.gallery_listing);

            // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
            // There should be a {@link ListView} with the view ID called list, which is declared in the
            // tourist_attraction_list.xml layout file.
            ListView listView = (ListView) rootView.findViewById(R.id.list);

            // Make the {@link ListView} use the {@link TouristAttractionAdapter} we created above, so that the
            // {@link ListView} will display list items for each {@link TouristAttraction} in the list.
            listView.setAdapter(adapter);
//            TagAdapter tagAdapter = new TagAdapter(TagActivity.this, tagList, R.color.tag_listing);
//            listView.setAdapter(tagAdapter);
//
//            // Set a click listener to open the tag's www.last.fm webpage when the list item is clicked on
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    // Get the {@link Tag} object at the given position the user clicked on
//                    Tag tag = tagList.get(position);
//                    String tagUrl = tag.getTagUrl();
//                    // Create a new intent to view the tag URI
//                    Intent openWebPage = new Intent(Intent.ACTION_VIEW);
//                    // Convert the String URL into a URI object (to set data on the Intent openWebPage)
//                    openWebPage.setData(Uri.parse(tagUrl));
//                    // Send the intent to launch a new activity
//                    startActivity(openWebPage);
//                }
//            });
        }
    }

}
