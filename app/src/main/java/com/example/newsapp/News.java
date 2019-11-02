package com.example.newsapp;

public class News {
    private String mTitle;
    private String mAuthor;
    private String mSection;
    private String mUrl;
    private String mPublishedAt;

    public News(String title, String author,String section, String url, String publishedAt){
        mTitle = title;
        mAuthor = author;
        mSection=section;
        mUrl = url;
        mPublishedAt=publishedAt;
    }

    public String getmTitle(){
        return mTitle;
    }
    public String getmAuthor(){
        return mAuthor;
    }
    public String getmSection(){
        return mSection;
    }
    public String getmUrl(){
        return mUrl;
    }
    public String getmPublishedAt(){
        return mPublishedAt;
    }
}
