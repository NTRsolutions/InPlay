package com.buggyarts.android.cuotos.gaana.utils;

/**
 * Created by mayank on 11/29/17
 */

public class Constants {

    public static final String TEXT_TYPE = " TEXT";
    public static final String NOT_NULL = " NOT NULL";
    public static final String COMMA_SEP = ", ";
    public static final String INT = " INTEGER";
    public static final String KEY = " PRIMARY KEY AUTOINCREMENT";

    public static int DISPLAY_WIDTH;
    public static int DISPLAY_HEIGHT;

    public static final int PREVIOUS = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int NEXT = 3;

    public static final int STATUS_PLAYING = 0;
    public static final int STATUS_PAUSE = 1;

    public static boolean PLAY_SHUFFLE = false;
    public static boolean PLAY_INLOOP = false;

    public static final int ADD_IN_QUEUE = 0;
    public static final int CLEAR_N_ADD_IN_QUEUE = 1;

    public static final int playingFromPlaylist = 1;
    public static final int playingFromAlbum = 2;
    public static final int playingFromSingles = 0;
    public static final int playingFromArtists = 3;
    public static final int playingFromBoot = 4;

    public static final String INDEX = "trackIndex";
    public static final String UPDATE_QUEUE = "updateQueue";
    public static final String TRACKS = "tracks";

    public static boolean isTrackPlaying = false;
    public static boolean isPlaylistPlaying = false;

    public static final int REQUEST_READ_EXTERNAL_STORAGE = 215;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 225;
    public static final int REQUEST_PHONE_STATE = 30;

    public static final int TXT_READ_EXTERNAL_STORAGE = 1;
    public static final int TXT_WRITE_EXTERNAL_STORAGE = 2;
    public static final int TXT_PHONE_STATE = 3;
}
