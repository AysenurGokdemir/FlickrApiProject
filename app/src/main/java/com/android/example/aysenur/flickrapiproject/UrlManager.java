package com.android.example.aysenur.flickrapiproject;

import android.net.Uri;

public class UrlManager {
    private static final String TAG = UrlManager.class.getSimpleName();


    public static final String API_KEY = "9bc50a841892eedb6dac5a89f60a7101" ;
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/" ;
    private static final String METHOD_GETRECENT = "flickr.photos.getRecent" ;



    // Singleton, make sure only max. 1 instance exist, even for multi-thread

    private static volatile UrlManager instance = null;
    private UrlManager() {

    }

    public static UrlManager getInstance() {
        if (instance == null) {
            synchronized (UrlManager.class) {
                if (instance == null) {
                    instance = new UrlManager();
                }
            }
        }
        return instance;
    }

    public static String getItemUrl(int page) {
        String url;

            url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GETRECENT)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("page", String.valueOf(page))
                    .build().toString();

        return url;
    }

    private static final String FLICKR_URL = "http://flickr.com/photo.gne?id=%s";
    private static final String METHOD_GETINFO = "flickr.photos.getInfo";

    public static String getPhotoInfoUrl(String id) {
        return Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GETINFO)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("photo_id", id)
                .build().toString();
    }
}
