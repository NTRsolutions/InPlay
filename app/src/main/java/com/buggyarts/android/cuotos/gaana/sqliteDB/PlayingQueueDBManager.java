package com.buggyarts.android.cuotos.gaana.sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buggyarts.android.cuotos.gaana.utils.Constants;

/**
 * Created by mayank on 12/9/17
 */

public class PlayingQueueDBManager extends SQLiteOpenHelper{


    private static final String DATABASE_NAME = "playingQueueDB";
    private static final int DATABASE_VERSION = 1;

    String SQL_CREATE_ENTRY = "CREATE TABLE " + AudioContract.audioEntry.QUEUE_NAME + " ( " +
            AudioContract.audioEntry.COLUMN_ID + Constants.INT + Constants.NOT_NULL + Constants.KEY + Constants.COMMA_SEP +
            AudioContract.audioEntry.COLUMN_DATA + Constants.TEXT_TYPE + Constants.NOT_NULL + Constants.COMMA_SEP +
            AudioContract.audioEntry.COLUMN_TITLE + Constants.TEXT_TYPE + Constants.NOT_NULL + Constants.COMMA_SEP +
            AudioContract.audioEntry.COLUMN_ARTIST + Constants.TEXT_TYPE + Constants.NOT_NULL + Constants.COMMA_SEP +
            AudioContract.audioEntry.COLUMN_ALBUM_ID + Constants.TEXT_TYPE + Constants.NOT_NULL + " )";


    public PlayingQueueDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String SQL_DELETE_ENTRY = "DROP TABLE IF EXISTS "+ AudioContract.audioEntry.QUEUE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);

        onCreate(sqLiteDatabase);
    }
}
