package com.example.newsapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private String mUrl;

    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        if (mUrl==null){
            return null;
        }
        // Perform the network request, parse the response, and extract a list of news.
        List<News>news= null;
        try {
            news = QueryUtils.fetchNewsData(mUrl);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return news;
    }
}
