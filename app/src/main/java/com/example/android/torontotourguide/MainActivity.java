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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Displays the listing of various galleries, museums, restaurants, and upcoming events in Toronto
 * using a ViewPager and TabLayout
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //static variables that get populated with the tourist attraction site / event values after the
    //asynchronous web api calls to nowtoronto.com
    private static ArrayList<TouristAttraction> galleryList = new ArrayList<TouristAttraction>();
    private static ArrayList<TouristAttraction> museumList = new ArrayList<TouristAttraction>();
    private static ArrayList<TouristAttraction> restaurantList = new ArrayList<TouristAttraction>();
    private static ArrayList<TouristAttraction> eventList = new ArrayList<TouristAttraction>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Find the tab layout that shows the tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab icons with the view pager's adapter's icons by calling
        //      getItem()
        tabLayout.setupWithViewPager(viewPager);

        // Create an adapter that knows which fragment should be shown on each page
        TouristAttractionCategoryAdapter adapter = new TouristAttractionCategoryAdapter(this, getSupportFragmentManager(), tabLayout);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        /*make the asynchronous api calls to get data on all the tourist attractions where
            0: code for galleriesList
            1: code for museumsList
            2: code for restaurantsList
            3: code for eventsList
        */
        new GetTouristAttractionJSONData().execute(0);
        new GetTouristAttractionJSONData().execute(1);
        new GetTouristAttractionJSONData().execute(2);
        new GetTouristAttractionJSONData().execute(3);
    }

    /* A static inner class to deliver data acquired via the asynchronous calls in the MainActivity to
       each of the fragments when the corresponding static methods get called
    */
    static class GetTouristAttraction {

        GetTouristAttraction() {}

        static ArrayList<TouristAttraction> getGalleryListing() {
            return galleryList;
        }
        static ArrayList<TouristAttraction> getMuseumListing() {
            return museumList;
        }
        static ArrayList<TouristAttraction> getRestaurantListing() {
            return restaurantList;
        }
        static ArrayList<TouristAttraction> getEventListing() {
            return eventList;
        }
    }

    /* A private inner class that executes the asynchronous web api calls, parses the data and
       populates the corresponding static variables
    */
    private class GetTouristAttractionJSONData extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... arg0) {
            ArrayList<TouristAttraction> resultList = new ArrayList<TouristAttraction>();
            HttpHandler sh = new HttpHandler();
            String url = "";

            if (arg0[0] == 0) {
                url = "https://nowtoronto.com/api/search/location/galleries/get_search_results";
                galleryList = resultList;
            } else if (arg0[0] == 1) {
                url = "https://nowtoronto.com/api/search/location/museums/get_search_results";
                museumList = resultList;
            } else if (arg0[0] == 2) {
                url = "https://nowtoronto.com/api/search/location/restaurants/get_search_results";
                restaurantList = resultList;
            } else {
                url = "https://nowtoronto.com/api/search/event/all/get_search_results";
                eventList = resultList;
            }

            //if ALL the TouristAttraction variables are already populated, then no more web API
            //calls will be required/made for the current session of the running app
            if (galleryList.size() != 0 && museumList.size() != 0 && restaurantList.size() != 0 && eventList.size() != 0) {
                return null;
            }

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
                        JSONObject currentTouristAttraction = results.getJSONObject(i);
                        String galleryName = currentTouristAttraction.getString("title");
                        String html = currentTouristAttraction.getString("html");
                        String currentTouristAttractionLocation = "";
                        String currentTouristAttractionUrl = "";
                        if (arg0[0] == 3) {
                            //extract the url with more info about the event from the html string
                            //find the index of the first occurrence of "<a href=\"" in the html string
                            int url_href_start_index = html.indexOf("<a href=\"");
                            //find the index of the first of occurrence of "\">" right after "<a href=\"" in the html string
                            int url_href_end_index = html.indexOf("\">", url_href_start_index);
                            currentTouristAttractionUrl = html.substring(url_href_start_index + "<a href=\"".length(), url_href_end_index);

                            //extract the location of the event from the html string
                            //find the index of the second occurrence of "<a href=\"" in the html string
                            int location_mention_start_index = html.indexOf("/locations/", url_href_end_index);
                            int location_href_start_index = html.indexOf("\">", location_mention_start_index);
                            //find the index of the second of occurrence of "\">" right after "<a href=\"" in the html string
                            int location_href_end_index = html.indexOf("</a>", location_href_start_index);

                            currentTouristAttractionLocation = html.substring(location_href_start_index + "\">".length(), location_href_end_index);

                        } else {
                            //extract the location of the tourist attraction from the html string
                            //find the index of the first occurrence of "<p>" in the html string
                            int p_start_index = html.indexOf("<p>");
                            //find the index of the first of occurrence of "</p>" right after "<p>" in the html string
                            int p_end_index = html.indexOf("</p>", p_start_index);
                            currentTouristAttractionLocation = html.substring(p_start_index + "<p>".length(), p_end_index);

                            //extract the url with more info about the tourist attraction from the html string
                            //find the index of the first occurrence of "<a href=\"" in the html string
                            int href_start_index = html.indexOf("<a href=\"");
                            //find the index of the first of occurrence of "\">" right after "<a href=\"" in the html string
                            int href_end_index = html.indexOf("\">", href_start_index);
                            currentTouristAttractionUrl = html.substring(href_start_index + "<a href=\"".length(), href_end_index);
                        }
                        //create a TouristAttraction object with the parsed data
                        TouristAttraction touristAttraction = new TouristAttraction(galleryName, currentTouristAttractionLocation, currentTouristAttractionUrl);

                        // adding a tourist attraction to our result ArrayList of TouristAttractions
                        resultList.add(touristAttraction);
                    }
                } catch (final JSONException e) {
                    Log.e(LOG_TAG, getString(R.string.json_parsing_error) + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.json_parsing_error) + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(LOG_TAG, getString(R.string.json_server_error));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.json_server_error),
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
        }
    }
}
