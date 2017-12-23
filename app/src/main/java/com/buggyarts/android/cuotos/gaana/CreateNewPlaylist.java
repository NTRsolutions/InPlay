package com.buggyarts.android.cuotos.gaana;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.fragments.PlaylistAddTracksFragment;
import com.buggyarts.android.cuotos.gaana.fragments.PlaylistNameFragment;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract;
import com.buggyarts.android.cuotos.gaana.sqliteDB.PlaylistDBManager;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.CreatePlaylist;


/**
 * Created by mayank on 11/29/17
 */

public class CreateNewPlaylist extends AppCompatActivity implements PlaylistNameFragment.GetPlaylistName,
        PlaylistAddTracksFragment.NewPlaylist{


    String playlist_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_playlist);
        getWindow().setLayout((int) (Constants.DISPLAY_WIDTH * 0.82), (int) (Constants.DISPLAY_HEIGHT * 0.8));

        //two step form
        //Enter the name of playlist

        final PlaylistAddTracksFragment tracksFragment = new PlaylistAddTracksFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        transaction.add(R.id.add_playlist_form,tracksFragment,"playlist_add_tracks");
        transaction.hide(tracksFragment);
        transaction.add(R.id.add_playlist_form,new PlaylistNameFragment(),"playlist_name");
        transaction.commit();

    }

    @Override
    public void getNewPlaylistName(String name) {


        playlist_name = name.replace(' ','_');
        Log.v("Playlist Name",playlist_name);

        openSelectPlaylistTracksFragment();
    }

    @Override
    public void createNewPlaylistWithTracks(ArrayList<String> tracks) {
        //Create new playlist with playlist_name and track_keys
        PlaylistDBManager dbManager;
        dbManager = new PlaylistDBManager(this);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.execSQL("CREATE TABLE " + playlist_name + "(" +
                AudioContract.playlist.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AudioContract.playlist.COLUMN_TRACK_TITLE + " TEXT NOT NULL, " +
                "CONSTRAINT unique_key UNIQUE (" + AudioContract.playlist.COLUMN_TRACK_TITLE + ") " + ")");
        db.close();

        new CreatePlaylist(this,playlist_name, tracks);
        finish();
    }

    public void openSelectPlaylistTracksFragment(){
        PlaylistAddTracksFragment fragment2 = (PlaylistAddTracksFragment) getSupportFragmentManager().findFragmentByTag("playlist_add_tracks");
        PlaylistNameFragment fragment1 = (PlaylistNameFragment) getSupportFragmentManager().findFragmentByTag("playlist_name");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
        transaction.hide(fragment1);
        transaction.show(fragment2);
        transaction.commit();
    }

}
