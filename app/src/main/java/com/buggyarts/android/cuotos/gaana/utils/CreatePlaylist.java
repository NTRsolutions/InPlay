package com.buggyarts.android.cuotos.gaana.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.playlist;
import com.buggyarts.android.cuotos.gaana.sqliteDB.PlaylistDBManager;


/**
 * Created by mayank on 11/28/17
 */

public class CreatePlaylist {

    private PlaylistDBManager dbManager;
    SQLiteDatabase db;

    String playlist_name;
    ArrayList<String> playlist_tracks;

    public CreatePlaylist(Context context, String playlist_name,ArrayList<String> playlist_tracks){
        this.playlist_name = playlist_name;
        this.playlist_tracks = playlist_tracks;

        playlist.TABLE_NAME = playlist_name;
        Log.v("Table Name",playlist.TABLE_NAME);
        dbManager = new PlaylistDBManager(context);
        db = dbManager.getWritableDatabase();

        int i = 0;
        while(i < playlist_tracks.size()){
            add_to_list(playlist_tracks.get(i));
            i++;
        }
        db.close();
    }

    private void add_to_list(String track_title){
        ContentValues values = new ContentValues();
        values.put(playlist.COLUMN_TRACK_TITLE,track_title);

        db.insert(playlist.TABLE_NAME,null,values);
    }

}
