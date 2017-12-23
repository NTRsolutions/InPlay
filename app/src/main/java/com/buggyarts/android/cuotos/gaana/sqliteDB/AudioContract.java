package com.buggyarts.android.cuotos.gaana.sqliteDB;

import android.provider.BaseColumns;

/**
 * Created by mayank on 9/28/17
 */

public final class AudioContract {

    public AudioContract(){}

    public static final class audioEntry implements BaseColumns {

        public static final String TABLE_NAME = "audioData";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST = "artist";
        public static final String COLUMN_ALBUM =  "album";
        public static final String COLUMN_ALBUM_ID = "album_id";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_DURATION_MIN = "min";
        public static final String COLUMN_DURATION_SEC = "sec";
        public static final String COLUMN_YEAR = "year";

        public static final String QUEUE_NAME = "playingQueue";

    }

    public static final class keysEntry implements BaseColumns{

        public static final String TABLE_NAME = "keyValues";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_INT_VALUE = "int_value";
        public static final String COLUMN_BOOL_VALUE = "bool_value";
        public static final String COLUMN_STRING_VALUE = "string_value";
    }

    public static final class playlist implements BaseColumns{

        public static String TABLE_NAME ;
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TRACK_TITLE = "title";

    }

    public static final class artistEntry implements BaseColumns{
        public static String TABLE_NAME = "artistData";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_ARTIST = "artist_name";
        public static final String COLUMN_ALBUM = "artist_albums";
        public static final String COLUMN_AlBUM_ID = "album_id";
        public static final String COLUMN_DURATION_MIN = "min";
        public static final String COLUMN_DURATION_SEC = "sec";
        public static final String COLUMN_TRACK = "tracks";
    }
}
