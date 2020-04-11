package com.example.quakereport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    //private static final String requestURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02";
    private static final String USGS_GOV_BASE_URI = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final String LOG_TAG = "MainActivity";
    ListView rootListView;
    TextView emptyStateView;
    View loadingProgress;
    QuakeAdapter earthquakeArrayAdapter;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootListView = findViewById(R.id.rootListView);
        emptyStateView = findViewById(R.id.empty_view);
        rootListView.setEmptyView(emptyStateView);
        loadingProgress = findViewById(R.id.dataProgress);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null)
            emptyStateView.setText("No Internet connection!");
        else if (networkInfo.isConnected())
            getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        else
            emptyStateView.setText("No Internet connection!");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu() stage");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "onOptionsItemSelected() stage");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        Log.v(LOG_TAG, "on Restart() stage");
        if (networkInfo == null)
            emptyStateView.setText("No Internet connection!");
        else if (networkInfo.isConnected())
            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        else
            emptyStateView.setText("No Internet connection!");
        super.onRestart();
    }


    @NonNull
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader() stage");
        loadingProgress.setVisibility(View.VISIBLE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String mMagnitude = preferences.getString(getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String limit = preferences.getString(getString(R.string.settings_min_limit_key),
                getString(R.string.settings_min_limit_default));

        String orderBy = preferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_GOV_BASE_URI);
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter("format", "geojson");
        builder.appendQueryParameter("limit", limit);
        builder.appendQueryParameter("minmag", mMagnitude);
        builder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeAsyncTaskLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Earthquake>> loader, List<Earthquake> data) {
        Log.v(LOG_TAG, "onLoadFinished() stage");
        emptyStateView.setText("There is no earthquake data to load");
        earthquakeArrayAdapter.clear();
        if (data != null && !data.isEmpty()) {
            earthquakeArrayAdapter.addAll(data);
        }
        loadingProgress.setVisibility(View.GONE);
        getSupportLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Earthquake>> loader) {
        Log.v(LOG_TAG, "onLoadFinished() stage");
        earthquakeArrayAdapter.clear();
        earthquakeArrayAdapter.addAll(new ArrayList<Earthquake>());
        emptyStateView.setText("There is no earthquake data to load");
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
