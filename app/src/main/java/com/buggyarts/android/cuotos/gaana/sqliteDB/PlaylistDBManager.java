package com.buggyarts.android.cuotos.gaana.sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.playlist;

/**
 * Created by mayank on 11/28/17
 */

public class PlaylistDBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "playlist.db";
    private static final int DATABASE_VERSION = 1;

    public String SQL_DELETE_ENTRY;

    public static final String TEXT_TYPE = " TEXT";
    public static final String NOT_NULL = " NOT NULL";
    public static final String COMMA_SEP = ", ";
    public static final String INT = " INTEGER";
    public static final String KEY = " PRIMARY KEY AUTOINCREMENT";

    private String SQL_CREATE_ENTRY = "CREATE TABLE " + playlist.TABLE_NAME + "(" +
            playlist.COLUMN_ID + INT + KEY + COMMA_SEP +
            playlist.COLUMN_TRACK_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            "CONSTRAINT unique_key UNIQUE (" + playlist.COLUMN_TRACK_TITLE + ") " + ")";

    public PlaylistDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        Log.v("DB MANAGER : Table ",playlist.TABLE_NAME);
//        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SQL_DELETE_ENTRY = "DROP TABLE IF EXIST"+ playlist.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);
        onCreate(sqLiteDatabase);
    }
}
