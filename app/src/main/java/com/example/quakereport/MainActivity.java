package com.example.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {
    //private static final String requestURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02";
    private static final String requestURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2015-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=2";
    ListView rootListView;
    QuakeAdapter earthquakeArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootListView = findViewById(R.id.rootListView);
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();

        earthquakeArrayAdapter = new QuakeAdapter(MainActivity.this, new ArrayList<Earthquake>());

        rootListView.setAdapter(earthquakeArrayAdapter);

        rootListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ACTION_VIEW, Uri.parse(earthquakeArrayAdapter.getItem(position).getUrl()));
                startActivity(intent);
            }
        });

        // EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        //task.execute(requestURL);

    }

    @NonNull
    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, @Nullable Bundle args) {
        return new EarthquakeAsyncTaskLoader(this, requestURL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> data) {
        earthquakeArrayAdapter.clear();
        earthquakeArrayAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Earthquake>> loader) {
        earthquakeArrayAdapter.clear();
        earthquakeArrayAdapter.addAll(new ArrayList<Earthquake>());
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {

        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            try {
                return QueryUtil.getQuakeData(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<Earthquake> result) {
            earthquakeArrayAdapter.clear();
            // If there is no result, do nothing.
            if (result == null)
                return;

            earthquakeArrayAdapter.addAll(result);
        }
    }


}
