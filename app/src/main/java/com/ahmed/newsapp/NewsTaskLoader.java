package com.ahmed.newsapp;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;


public class NewsTaskLoader extends AsyncTaskLoader<List<NewsModel>> {
    String url;
    public NewsTaskLoader(@NonNull Context context , String url) {
        super(context);
        this.url = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }
    @Nullable
    @Override
    public List<NewsModel> loadInBackground() {
        if(url == null){
            return null;
        }
        return NetworkUtils.featchNewsData(url);
    }
}
