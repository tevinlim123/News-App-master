package com.ahmed.newsapp;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class NetworkUtils {
    public static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    public NetworkUtils() {
    }

    public static ArrayList<NewsModel> featchNewsData (String requestUrl){
        Log.w(LOG_TAG , "fetchNewsData");
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<NewsModel> newsModelList = extractNewsFromJson(jsonResponse);
        return newsModelList;
    }


    private static URL createUrl (String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    private static String makeHttpRequest (URL url)throws IOException{
        String jsonResponse="";
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream , Charset.forName("UTF-8") );
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public static ArrayList<NewsModel> extractNewsFromJson(String newsJSON){
        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }
        ArrayList<NewsModel> newsModelList = new ArrayList<>();

        try{
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject parentJson = baseJsonResponse.optJSONObject("response");
            JSONArray newsArray = parentJson.optJSONArray("results");
            for (int i=0; i<newsArray.length(); i++){
                JSONObject currentNews = newsArray.optJSONObject(i);

                String author = "";
                JSONObject  fieldAuthor = currentNews.optJSONObject("fields");
                if(fieldAuthor != null){
                    author = fieldAuthor.optString("byline");
                }

                NewsModel newsModel = new NewsModel(
                        currentNews.optString (URL_Query_Fields.Query_News_Web_Title) ,
                        currentNews.optString (URL_Query_Fields.Query_News_Type) ,
                        currentNews.optString (URL_Query_Fields.Query_News_Section_ID) ,
                        currentNews.optString (URL_Query_Fields.Query_News_Web_Publication_Date) ,
                        currentNews.optString (URL_Query_Fields.Query_News_Web_URL) ,
                        author) ;
                newsModelList.add(newsModel);
                Log.e("00000000" , author);
            }
            Log.e("00000000" , "   " + newsModelList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Log.e(LOG_TAG, "Finish.........." + newsModelList.size());
        }
        return newsModelList;
    }
}
