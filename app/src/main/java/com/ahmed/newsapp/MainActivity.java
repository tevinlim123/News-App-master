package com.ahmed.newsapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<NewsModel>> {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    TextView emptyView;
    ProgressBar progressbar;
    NewsOnItemClickListener newsOnItemClickListener;
    private static final String NEWS_URL = "https://content.guardianapis.com/search";
    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getResources().getString(R.string.loading));

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    emptyView.setText(getResources().getString(R.string.no_news_available));
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
                } else {
                    emptyView.setText(getResources().getString(R.string.no_connection));
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        newsOnItemClickListener = new NewsOnItemClickListener() {
            @Override
            public void onItemClick(NewsModel newsModel) {
                Toast.makeText(MainActivity.this, "" + newsModel.getTitle(), Toast.LENGTH_SHORT).show();
            }
        };

        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.INVISIBLE);
        emptyView = findViewById(R.id.emptyView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        newsAdapter = new NewsAdapter(this, new ArrayList<NewsModel>(), newsOnItemClickListener);
        recyclerView.setAdapter(newsAdapter);
        RecyclerViewEmptyObserver observer = new RecyclerViewEmptyObserver(recyclerView, emptyView);
        newsAdapter.registerAdapterDataObserver(observer);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            emptyView.setText(getResources().getString(R.string.no_news_available));
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            emptyView.setText(getResources().getString(R.string.no_connection));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_menu) {
            startActivity(new Intent(this, SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<NewsModel>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(NEWS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(URL_Query_Fields.Query_Q, "android");
        uriBuilder.appendQueryParameter(URL_Query_Fields.Query_Show_fileds , "byline");
        uriBuilder.appendQueryParameter(URL_Query_Fields.Order_By , orderBy);
        uriBuilder.appendQueryParameter(URL_Query_Fields.Query_Api_Key, "test");
        Log.e("The whole link ", uriBuilder.toString());
        return new NewsTaskLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsModel>> loader, List<NewsModel> newsModels) {
        progressbar.setVisibility(View.GONE);
        if (newsModels != null && !newsModels.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            newsAdapter = new NewsAdapter(MainActivity.this, (ArrayList<NewsModel>) newsModels, newsOnItemClickListener);
            recyclerView.setAdapter(newsAdapter);
        } else {
            newsAdapter = new NewsAdapter(MainActivity.this, new ArrayList<NewsModel>(), newsOnItemClickListener);
            recyclerView.setAdapter(newsAdapter);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                emptyView.setText(getResources().getString(R.string.no_news_available));
            } else {
                emptyView.setText(getResources().getString(R.string.no_connection));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsModel>> loader) {
        newsAdapter = new NewsAdapter(getBaseContext(), new ArrayList<NewsModel>(), newsOnItemClickListener);
        newsAdapter.notifyDataSetChanged();
    }


    // this is another implementation with a AsyncTask to get data from internet
    public class NewsAsyncTask extends AsyncTask<String, Void, ArrayList<NewsModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.GONE);
            progressbar.setVisibility(View.VISIBLE);
        }
        @Override
        protected ArrayList<NewsModel> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            ArrayList<NewsModel> result = NetworkUtils.featchNewsData(urls[0]);
            return result;
        }
        @Override
        protected void onPostExecute(ArrayList<NewsModel> data) {
            progressbar.setVisibility(View.GONE);
            if (data != null && !data.isEmpty()) {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                newsAdapter = new NewsAdapter(MainActivity.this, data, newsOnItemClickListener);
                recyclerView.setAdapter(newsAdapter);
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                emptyView.setText(getResources().getString(R.string.no_connection));
            }
        }
    }

}
