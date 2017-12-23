package com.buggyarts.android.cuotos.gaana.sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;


/**
 * Created by mayank on 11/15/17
 */

public class AudioDBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "audio.db";
    private static final int DATABASE_VERSION = 1;

    String SQL_DELETE_ENTRY;

    public static final String TEXT_TYPE = " TEXT";
    public static final String NOT_NULL = " NOT NULL";
    public static final String COMMA_SEP = ", ";
    public static final String INT = " INTEGER";
    public static final String KEY = " PRIMARY KEY AUTOINCREMENT";

    public String SQL_CREATE_ENTRY = "CREATE TABLE "+ audioEntry.TABLE_NAME + "(" +
            audioEntry.COLUMN_ID + INT + KEY + COMMA_SEP +
            audioEntry.COLUMN_KEY + TEXT_TYPE + NOT_NULL + COMMA_SEP+
            audioEntry.COLUMN_DATA + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            audioEntry.COLUMN_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            audioEntry.COLUMN_ALBUM + TEXT_TYPE + COMMA_SEP +
            audioEntry.COLUMN_ALBUM_ID + TEXT_TYPE + COMMA_SEP +
            audioEntry.COLUMN_ARTIST + TEXT_TYPE + COMMA_SEP +
            audioEntry.COLUMN_DURATION_MIN + INT + COMMA_SEP +
            audioEntry.COLUMN_DURATION_SEC + INT + COMMA_SEP +
            audioEntry.COLUMN_YEAR + INT + COMMA_SEP +
            "CONSTRAINT unique_key UNIQUE (" + audioEntry.COLUMN_KEY + ") " + ")";



    public AudioDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        SQL_DELETE_ENTRY = "DROP TABLE IF EXIST"+ audioEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);

        onCreate(sqLiteDatabase);
    }

    public void dropTable(SQLiteDatabase db){
        SQL_DELETE_ENTRY = "DROP TABLE IF EXIST " + AudioContract.audioEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRY);
    }
}
