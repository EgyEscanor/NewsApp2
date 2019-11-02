package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

 /** URL for earthquake data from the USGS dataset */
   private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?";
   private static final int NEWS_LOADER_ID =1;

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
    NewsAdapter mAdapter = new NewsAdapter(mList,mContext);
    newsListView.setLayoutManager(new LinearLayoutManager(this));
    newsListView.setAdapter(mAdapter);

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    NewsAsyncTask task = new NewsAsyncTask();
    task.execute(NEWS_REQUEST_URL);



     // Overriding the onClick and onLongClick methods from RecyclerTouchListener
     newsListView.addOnItemTouchListener(new Recycler(getApplication(), newsListView, new Recycler.ClickListener() {
      @Override
      public void onClick(View view, int position) {
       News news = mList.get(position);
       Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getmUrl()));
       startActivity(browserIntent);
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
                ""
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_seetings){
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class NewsAsyncTask extends AsyncTask<String,Void,List<News>>{

        @Override
        protected List<News> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<News> result = null;
            try {
                result = QueryUtils.fetchNewsData(urls[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}
