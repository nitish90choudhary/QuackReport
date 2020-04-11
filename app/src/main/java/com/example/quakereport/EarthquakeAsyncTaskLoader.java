package com.example.quakereport;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.ArrayList;

public class EarthquakeAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = EarthquakeAsyncTaskLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    public EarthquakeAsyncTaskLoader(@NonNull Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Nullable
    @Override
    public ArrayList<Earthquake> loadInBackground() {
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrl == null) {
            return null;
        }
        try {
            return QueryUtil.getQuakeData(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}