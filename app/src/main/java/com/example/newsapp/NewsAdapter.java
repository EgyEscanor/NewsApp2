package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> newsItem;
    private Context mContext;

    NewsAdapter(List<News> newsList, Context mContext) {
        this.newsItem = newsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        News currentItem = newsItem.get(position);

        String title = currentItem.getmTitle();
        String author = currentItem.getmAuthor();
        String section = currentItem.getmSection();
        String publishedAt = currentItem.getmPublishedAt();

        viewHolder.titleView.setText(title);
        viewHolder.authorView.setText(author);
        viewHolder.sectionView.setText(section);
        viewHolder.publishedAt.setText(publishedAt);

    }

    @Override
    public int getItemCount() {
        return newsItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final NewsAdapter mAdapter;

        @BindView(R.id.text_title)
                 TextView titleView;
        @BindView(R.id.text_time)
                TextView publishedAt;
        @BindView(R.id.text_author)
                TextView authorView;
        @BindView(R.id.text_section)
                TextView sectionView;


        ViewHolder(View view, NewsAdapter adapter) {

            super(view);

            ButterKnife.bind(this, view);

            this.mAdapter = adapter;
        }
    }
}
