package com.buggyarts.android.cuotos.gaana.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.buggyarts.android.cuotos.gaana.sqliteDB.ArtistDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mayank on 1/5/18
 */

public class MetadataAPI {

    private String API_KEY = "14330664847300717d3706629701ecdc";
    private String base_url = "http://ws.audioscrobbler.com/2.0/?method=";
    private String method = "artist.getinfo";
    private String format = "json";

    private String artist;
    private String api_link;
    private String image_url;
    private JSONObject jsonObject;
    private String jsonResponse;

    private Context context;

    public MetadataAPI(Context context){
        this.context = context;
    }

    public void getArtistImage(String artist){
        this.artist = artist;

        String artist_name = artist.replace(" ","+");
        artist_name = artist_name.replace("<","");
        artist_name = artist_name.replace(">","");

        api_link = base_url + method + "&artist=" + artist_name + "&api_key=" + API_KEY + "&format=" + format;

//        Log.v("API_link", api_link);
        new GetMetadata().execute(api_link);
    }

    public void getJson(String src){
        jsonResponse = null;
        try {
            URL url = new URL(src);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if(httpURLConnection.getResponseCode()!= 200){
//                throw new IOException(httpURLConnection.getResponseMessage());
                Log.v("Response",httpURLConnection.getResponseMessage());
            }else {
                Log.v("Connection Status",httpURLConnection.getResponseMessage());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line + "\n");
                }bufferedReader.close();
                httpURLConnection.disconnect();
                jsonResponse = stringBuilder.toString();
            }
            Thread.sleep(200);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void find_image_url(){
        if(jsonResponse != null){
            try {
                jsonObject = new JSONObject(jsonResponse);
                JSONObject artist = jsonObject.getJSONObject("artist");
                JSONArray images = artist.getJSONArray("image");
                image_url = images.getJSONObject(3).getString("#text");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertImage(){
        ArtistDBManager artistDBManager = new ArtistDBManager(context);
        SQLiteDatabase db = artistDBManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AudioContract.artistEntry.COLUMN_IMAGE,image_url);
        String[] args = {artist};
        db.update(AudioContract.artistEntry.TABLE_NAME,values, AudioContract.artistEntry.COLUMN_ARTIST+ "=?",args);
    }

    public class GetMetadata extends AsyncTask<Object,Void,Void>{


        @Override
        protected Void doInBackground(Object... objects) {
            getJson((String) objects[0]);
            find_image_url();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            insertImage();
            super.onPostExecute(v);
        }
    }

}
