package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buggyarts.android.cuotos.gaana.R;

import java.util.ArrayList;

//import com.buggyarts.android.inplay.adapters.AlbumRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 11/16/17.
 */

public class AlbumFragment extends Fragment {

    private ArrayList<Audio> album_tracks;
    private AudioDBManager dbManager;
    private Context context;
    private String[] arguments;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album,container,false);

        recyclerView = view.findViewById(R.id.album_song_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);;
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        album_tracks = new ArrayList<>();
        dbManager = new AudioDBManager(context);

        Bundle bundle = getArguments();
        arguments = bundle.getStringArray("arguments");

        load_album_tracks(arguments);
    }

    public void load_album_tracks(String[] argument){

        SQLiteDatabase db = dbManager.getReadableDatabase();
        String[] columns = {"data","title","album","album_id","artist"};

        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,"album = ?",argument,null,null,"title");
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String album = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));

                Log.v("album tracks",""+title);
                album_tracks.add(new Audio(data,title,album,album_id,artist));
            }
        }cursor.close();
    }
}
