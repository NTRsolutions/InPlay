package com.buggyarts.android.cuotos.gaana.sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.keysEntry;

/**
 * Created by mayank on 11/25/17
 */

public class KeysnValuesDbManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "keys.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TEXT_TYPE = " TEXT";
    public static final String NOT_NULL = " NOT NULL";
    public static final String COMMA_SEP = ", ";
    public static final String INT = " INTEGER";
    public static final String KEY = " PRIMARY KEY AUTOINCREMENT";

    public String SQL_CREATE_ENTRY = "CREATE TABLE " + keysEntry.TABLE_NAME + "( "+
            keysEntry.COLUMN_ID + INT + KEY + COMMA_SEP +
            keysEntry.COLUMN_KEY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            keysEntry.COLUMN_INT_VALUE + INT + COMMA_SEP +
            keysEntry.COLUMN_BOOL_VALUE + INT + COMMA_SEP +
            keysEntry.COLUMN_STRING_VALUE + TEXT_TYPE + COMMA_SEP +
            "CONSTRAINT unique_key UNIQUE (" + keysEntry.COLUMN_KEY + ") " + ")";

    String SQL_DELETE_ENTRY;

    public KeysnValuesDbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SQL_DELETE_ENTRY = "DROP TABLE IF EXIST"+ keysEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);
        onCreate(sqLiteDatabase);
    }
}
