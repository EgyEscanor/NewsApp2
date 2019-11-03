package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

 /** URL for earthquake data from the USGS dataset */
   public static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?";
   public static final int NEWS_LOADER_ID =1;

    @BindView(R.id.recyclerView)
    RecyclerView newsListView;
    @BindView(R.id.text_empty)
    TextView mEmptyStateTextView;
    @BindView(R.id.text_loading)
    ProgressBar loadingIndicator;

 /** Adapter for the list of news */
 private List<News> mList = new ArrayList<>();
 private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    /* Create a new adapter that takes an empty list of news as input
           Adapter for the list of news
        */
        NewsAdapter mAdapter = new NewsAdapter(mList, mContext);
        newsListView.setLayoutManager(new LinearLayoutManager(this));
        newsListView.setAdapter(mAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            // connected to the internet
            LoaderManager loaderManager = getSupportLoaderManager();
            getSupportLoaderManager().initLoader(1, null, this);

        } else {
            // not connected to the internet
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }


        // Overriding the onClick and onLongClick methods from RecyclerTouchListener
        newsListView.addOnItemTouchListener(new Recycler(getApplication(), newsListView, new Recycler.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                News news = mList.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getmUrl()));
                startActivity(intent);
            }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.order_by_key)) ||
        key.equals(getString(R.string.section_key))){
            mList.clear();
            mEmptyStateTextView.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);

            getSupportLoaderManager().restartLoader(NEWS_LOADER_ID,null,this);

        }

    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {

        SharedPreferences sharedPrefer =PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefer.getString(
                getString(R.string.order_by_key),
                getString(R.string.default_order_value)
        );
        String section = sharedPrefer.getString(
                getString(R.string.section_key),
                getString(R.string.default_section_value)

        );

        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api-key","8016d881-b1f6-4d45-b39e-ea837dac3de8");
        uriBuilder.appendQueryParameter("show-tags","contributor");
        uriBuilder.appendQueryParameter("order-by",orderBy);

        if (!section.equals("")){
            uriBuilder.appendQueryParameter("section",section);
        }
        return new NewsLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_news);
        newsListView.setVisibility(View.GONE);

        if (news != null && !news.isEmpty()) {
            newsListView.setVisibility(View.VISIBLE);
            mList.addAll(news);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_seetings){
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
