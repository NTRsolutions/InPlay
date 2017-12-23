package com.buggyarts.android.cuotos.gaana.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.sqliteDB.ArtistDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.artistEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.keysEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.KeysnValuesDbManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.PlayingQueueDBManager;

/**
 * Created by mayank on 11/15/17
 */

public class MediaRetriever{

    public Context context;
    public AudioDBManager audioDbManager;
    public ArtistDBManager artistDBManager;
    public KeysnValuesDbManager keysnValuesDbManager;
    public PlayingQueueDBManager playingQueueDBManager;
    public Bitmap bitmap;
    final public static Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public MediaRetriever(Context context){
        this.context = context;
    }

    public MediaRetriever(Context context,ArrayList<Audio> tracks){

        if(tracks != null){
            playingQueueDBManager = new PlayingQueueDBManager(context);
            SQLiteDatabase db = playingQueueDBManager.getWritableDatabase();
            playingQueueDBManager.onUpgrade(db,1,1);
            int i = 0;
            while(i< tracks.size()){
                ContentValues values = new ContentValues();
                values.put(audioEntry.COLUMN_DATA,tracks.get(i).data);
                values.put(audioEntry.COLUMN_TITLE,tracks.get(i).title);
                values.put(audioEntry.COLUMN_ARTIST,tracks.get(i).artist);
                values.put(audioEntry.COLUMN_ALBUM_ID,tracks.get(i).album_id);
                db.insert(audioEntry.QUEUE_NAME,null,values);
                i++;
            }
        }
    }

    public MediaRetriever(Context context,int index){
        this.context = context;

        keysnValuesDbManager = new KeysnValuesDbManager(context);
        SQLiteDatabase keysDb = keysnValuesDbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keysEntry.COLUMN_KEY,"queueIndex");
        values.put(keysEntry.COLUMN_INT_VALUE,index);
        keysDb.insert(keysEntry.TABLE_NAME,null,values);


        audioDbManager = new AudioDBManager(context);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";

        Cursor cursor = contentResolver.query(uri,null,selection,null,sortOrder,null);
        if (cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title_key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String track_duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                int year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));

                Long microseconds = Long.valueOf(track_duration);
                Log.v("duration",""+microseconds);
                int mili = (int) (microseconds / 1000);
                int sec = (mili) % 60;
                int min = (mili) / 60;
                Log.v("time " , min + ":" + sec);
                String duration = min + ":" + sec;
                //add to DB
                insertAudio(data,title_key,title,album,album_id,artist,min,sec,year);
                insertArtist(artist,title,data,album,album_id,min,sec);
            }
        }cursor.close();

        update_keys();
    }

    public void insertAudio(String data,String key, String title, String album,String album_id, String artist,int min,int sec, int year){

        SQLiteDatabase db = audioDbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(audioEntry.COLUMN_DATA,data);
        values.put(audioEntry.COLUMN_KEY,key);
        values.put(audioEntry.COLUMN_TITLE,title);
        values.put(audioEntry.COLUMN_ALBUM,album);
        values.put(audioEntry.COLUMN_ALBUM_ID,album_id);
        values.put(audioEntry.COLUMN_ARTIST,artist);
        values.put(audioEntry.COLUMN_DURATION_MIN,min);
        values.put(audioEntry.COLUMN_DURATION_SEC,sec);
        values.put(audioEntry.COLUMN_YEAR,year);

        long row_id = db.insert(audioEntry.TABLE_NAME,null,values);
    }

    public static Uri getAlbumArt(String album_id){
        long id = Long.parseLong(album_id);
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri,id);

        return albumArtUri;
    }

    public void update_keys(){
        keysnValuesDbManager = new KeysnValuesDbManager(context);
        SQLiteDatabase keysDB = keysnValuesDbManager.getWritableDatabase();

        ContentValues key_values = new ContentValues();
        key_values.put(keysEntry.COLUMN_BOOL_VALUE,1);

        String[] arg = {"is_audioDB_created"};
        keysDB.update(keysEntry.TABLE_NAME,key_values,keysEntry.COLUMN_KEY + " = ?",arg);
    }

    public void insertArtist(String artists,String track_title,String data,String album,String album_id,int min,int sec){

        StringManipulator manipulator = new StringManipulator(artists);
        ArrayList<String> artist= manipulator.refineString();
        for(int i = 0; i<artist.size();i++){
            // For every artist add title, album to the DB
            createArtistEntry(artist.get(i),track_title,data,album,album_id,min,sec);
        }
    }

    public void createArtistEntry(String artist,String track_title,String data, String album,String album_id,int min,int sec){

        artistDBManager = new ArtistDBManager(context);
        SQLiteDatabase db = artistDBManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(artistEntry.COLUMN_ARTIST,artist);
        values.put(artistEntry.COLUMN_TRACK,track_title);
        values.put(artistEntry.COLUMN_DATA,data);
        values.put(artistEntry.COLUMN_ALBUM,album);
        values.put(artistEntry.COLUMN_AlBUM_ID,album_id);
        values.put(artistEntry.COLUMN_DURATION_MIN,min);
        values.put(artistEntry.COLUMN_DURATION_SEC,sec);

        long rw_id = db.insert(artistEntry.TABLE_NAME,null,values);
    }

    public ArrayList<Audio> recreateQueue(){

        ArrayList<Audio> tracks = new ArrayList<>();

        playingQueueDBManager = new PlayingQueueDBManager(context);
        SQLiteDatabase db = playingQueueDBManager.getReadableDatabase();
        String[] columns = {"data","title","artist","album_id"};
        String order_by = "title";

        Cursor cursor = db.query(audioEntry.QUEUE_NAME,columns,null,null,null,null,null);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));

                tracks.add(new Audio(data,title,artist,album_id));
            }
        }cursor.close();
        return tracks;
    }

    public int extractIndex(){
        int index = 0;
        keysnValuesDbManager = new KeysnValuesDbManager(context);
        SQLiteDatabase db = keysnValuesDbManager.getReadableDatabase();
        String[] columns ={keysEntry.COLUMN_INT_VALUE};
        String[] args = {"queueIndex"};
        Cursor cursor = db.query(keysEntry.TABLE_NAME,columns,keysEntry.COLUMN_KEY+ "=?",args,null,null,null);
        if(cursor != null && cursor.getCount()>0){
            while(cursor.moveToNext()){
               index  = cursor.getInt(cursor.getColumnIndex(keysEntry.COLUMN_INT_VALUE));
            }
        }cursor.close();
        return index;
    }

    public void storeQueueValues(ArrayList<Audio> tracks,int index){

        playingQueueDBManager = new PlayingQueueDBManager(context);
        SQLiteDatabase db = playingQueueDBManager.getWritableDatabase();
        playingQueueDBManager.onUpgrade(db,1,1);
        int i = 0;
        while(i< tracks.size()){
            ContentValues values = new ContentValues();
            values.put(audioEntry.COLUMN_DATA,tracks.get(i).data);
            values.put(audioEntry.COLUMN_TITLE,tracks.get(i).title);
            values.put(audioEntry.COLUMN_ARTIST,tracks.get(i).artist);
            values.put(audioEntry.COLUMN_ALBUM_ID,tracks.get(i).album_id);
            db.insert(audioEntry.QUEUE_NAME,null,values);
            i++;
        }

        keysnValuesDbManager = new KeysnValuesDbManager(context);
        SQLiteDatabase keysDB = keysnValuesDbManager.getWritableDatabase();
        ContentValues key_values = new ContentValues();
        key_values.put(keysEntry.COLUMN_INT_VALUE,index);
        String[] arg = {"queueIndex"};
        keysDB.update(keysEntry.TABLE_NAME,key_values,keysEntry.COLUMN_KEY + " = ?",arg);
        Log.v("Queue KEY",index + " saved");

    }

    public Audio makeAudioObject(Uri uri){
        Audio audio = null;

        String path = uri.getPath();

        ContentResolver contentResolver = context.getContentResolver();
        Uri from = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA + "=?";
        String[] arg = {path};

        Cursor cursor = contentResolver.query(from,null,selection,arg,null,null);
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String track_duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                Long microseconds = Long.valueOf(track_duration);
                int mili = (int) (microseconds / 1000);
                int sec = (mili) % 60;
                int min = (mili) / 60;

                audio = new Audio(data,title,artist,album_id,min,sec);
            }

        }cursor.close();

        return audio;
    }

}
