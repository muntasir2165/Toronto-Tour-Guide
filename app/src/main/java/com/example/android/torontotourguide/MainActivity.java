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

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private static ArrayList<TouristAttraction> galleries = new ArrayList<TouristAttraction>();
    private static ArrayList<TouristAttraction> museums = new ArrayList<TouristAttraction>();
    private static ArrayList<TouristAttraction> restaurants = new ArrayList<TouristAttraction>();
    private static ArrayList<TouristAttraction> events = new ArrayList<TouristAttraction>();

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
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);

        // Create an adapter that knows which fragment should be shown on each page
        TouristAttractionCategoryAdapter adapter = new TouristAttractionCategoryAdapter(this, getSupportFragmentManager(), tabLayout);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        new GetTouristAttractionJSONData().execute(0);
        new GetTouristAttractionJSONData().execute(1);
        new GetTouristAttractionJSONData().execute(2);
        new GetTouristAttractionJSONData().execute(3);
    }

    static class GetTouristAttraction {
        GetTouristAttraction() {}
        static ArrayList<TouristAttraction> getGalleries() {
            return galleries;
        }
        static ArrayList<TouristAttraction> getMuseums() {
            return museums;
        }
        static ArrayList<TouristAttraction> getRestaurants() {
            return restaurants;
        }
        static ArrayList<TouristAttraction> getEvents() {
            return events;
        }
    }

    private class GetTouristAttractionJSONData extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... arg0) {
            ArrayList<TouristAttraction> result = new ArrayList<TouristAttraction>();
            HttpHandler sh = new HttpHandler();
            String url = "";

            if (arg0[0] == 0) {
                url = "https://nowtoronto.com/api/search/location/galleries/get_search_results";
                galleries = result;
            } else if (arg0[0] == 1) {
                url = "https://nowtoronto.com/api/search/location/museums/get_search_results";
                museums = result;
            } else if (arg0[0] == 2) {
                url = "https://nowtoronto.com/api/search/location/restaurants/get_search_results";
                restaurants = result;
            } else {
                url = "https://nowtoronto.com/api/search/event/all/get_search_results";
                events = result;
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
                        JSONObject currentGalleryItem = results.getJSONObject(i);
                        String galleryName = currentGalleryItem.getString("title");
                        String html = currentGalleryItem.getString("html");
                        String galleryLocation = "";
                        String galleryUrl = "";
                        if (arg0[0] == 3) {
                            //extract the url with more info about the event from the html string
                            //System.out.println(html.indexOf("<a href=")); //62
                            //find the index of the first occurrence of "<a href=\"" in the html string
                            int url_href_start_index = html.indexOf("<a href=\"");
                            //find the occurrence of the first of occurrence of "\">" right after "<a href=\"" in the html string
                            int url_href_end_index = html.indexOf("\">", url_href_start_index);
                            galleryUrl = html.substring(url_href_start_index + "<a href=\"".length(), url_href_end_index);

                            //extract the location of the event from the html string
                            //find the index of the second occurrence of "<a href=\"" in the html string
                            // int location_href_start_index = html.indexOf("<a href=\"", url_href_end_index);
                            int location_mention_start_index = html.indexOf("/locations/", url_href_end_index);
                            int location_href_start_index = html.indexOf("\">", location_mention_start_index);
                            //find the occurrence of the second of occurrence of "\">" right after "<a href=\"" in the html string
//                        int location_href_end_index = html.indexOf("\">", location_href_start_index);
                            int location_href_end_index = html.indexOf("</a>", location_href_start_index);

                            galleryLocation = html.substring(location_href_start_index + "\">".length(), location_href_end_index);

                        } else {
                            //extract the location of the gallery from the html string
                            //find the index of the first occurrence of "<p>" in the html string
                            int p_start_index = html.indexOf("<p>");
                            //find the occurrence of the first of occurrence of "</p>" right after "<p>" in the html string
                            int p_end_index = html.indexOf("</p>", p_start_index);
                            galleryLocation = html.substring(p_start_index + "<p>".length(), p_end_index);

                            //extract the url with more info about the gallery from the html string
                            //System.out.println(html.indexOf("<a href=")); //62
                            //find the index of the first occurrence of "<a href=\"" in the html string
                            int href_start_index = html.indexOf("<a href=\"");
                            //find the occurrence of the first of occurrence of "\">" right after "<a href=\"" in the html string
                            int href_end_index = html.indexOf("\">", href_start_index);
                            galleryUrl = html.substring(href_start_index + "<a href=\"".length(), href_end_index);
                        }
                        //create a tag object with the parsed data
                        TouristAttraction gallery = new TouristAttraction(galleryName, galleryLocation, galleryUrl);

                        // adding a gallery to our galleries list
                        result.add(gallery);
                    }
                } catch (final JSONException e) {
                    Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(LOG_TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
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
//                // Create an {@link TouristAttractionAdapter}, whose data source is a list of {@link TouristAttraction}s. The
//                // adapter knows how to create list items for each item in the list.
//                TouristAttractionAdapter adapter = new TouristAttractionAdapter(getActivity(), galleries, R.color.gallery_listing);
//
//                // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
//                // There should be a {@link ListView} with the view ID called list, which is declared in the
//                // tourist_attraction_list.xml layout file.
//                ListView listView = (ListView) rootView.findViewById(R.id.list);

            // Make the {@link ListView} use the {@link TouristAttractionAdapter} we created above, so that the
            // {@link ListView} will display list items for each {@link TouristAttraction} in the list.
//                listView.setAdapter(adapter);
//            TagAdapter tagAdapter = new TagAdapter(TagActivity.this, tagList, R.color.tag_listing);
//            listView.setAdapter(tagAdapter);
//
        }
    }
}
