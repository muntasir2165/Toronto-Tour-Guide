package com.example.android.torontotourguide;

/**
 * {@link TouristAttraction} represents a tourist attraction in Toronto.
 * It contains the attraction name, the location of the place/event, and the nowtoronto.com webpage
 * url with more information about it.
 */
public class TouristAttraction {
    private String mName;
    private String mLocation;
    private String mUrl;

    public TouristAttraction(String name, String location, String url) {
        this.mName = name;
        this.mLocation = location;
        this.mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public String toString() {
        return "TouristAttraction{" +
                "mName='" + mName + '\'' +
                ", mLocation='" + mLocation + '\'' +
                ", mUrl='" + mUrl + '\'' +
                '}';
    }
}
