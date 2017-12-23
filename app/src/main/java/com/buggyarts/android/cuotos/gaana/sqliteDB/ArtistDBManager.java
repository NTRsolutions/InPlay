package com.buggyarts.android.cuotos.gaana.sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.artistEntry;
import com.buggyarts.android.cuotos.gaana.utils.Constants;

/**
 * Created by mayank on 12/8/17
 */

public class ArtistDBManager extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "artist.db";
    private static int DATABASE_VERSION = 1;

    private String SQL_CREATE_ENTRY = "CREATE TABLE " + artistEntry.TABLE_NAME + " ( "+
            artistEntry.COLUMN_ID + Constants.INT + Constants.NOT_NULL + Constants.KEY + Constants.COMMA_SEP +
            artistEntry.COLUMN_DATA + Constants.TEXT_TYPE + Constants.NOT_NULL + Constants.COMMA_SEP +
            artistEntry.COLUMN_ARTIST + Constants.TEXT_TYPE + Constants.NOT_NULL + Constants.COMMA_SEP +
            artistEntry.COLUMN_ALBUM + Constants.TEXT_TYPE + Constants.NOT_NULL + Constants.COMMA_SEP +
            artistEntry.COLUMN_AlBUM_ID + Constants.TEXT_TYPE + Constants.NOT_NULL + Constants.COMMA_SEP +
            artistEntry.COLUMN_DURATION_MIN + Constants.INT + Constants.COMMA_SEP +
            artistEntry.COLUMN_DURATION_SEC + Constants.INT + Constants.COMMA_SEP +
            artistEntry.COLUMN_TRACK + Constants.TEXT_TYPE + Constants.NOT_NULL +  ")";
//          Constants.COMMA_SEP + "CONSTRAINT unique_key UNIQUE (" + artistEntry.COLUMN_TRACK + ") " + ")";

    public ArtistDBManager(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String SQL_DELETE_ENTRY = "DROP TABLE IF EXIST"+ artistEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);

        onCreate(sqLiteDatabase);
    }
}
