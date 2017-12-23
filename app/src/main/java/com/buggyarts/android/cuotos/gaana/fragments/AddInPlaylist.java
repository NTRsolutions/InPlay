package com.buggyarts.android.cuotos.gaana.fragments;

import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.android.cuotos.gaana.R;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.adapters.SimpleListRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract;
import com.buggyarts.android.cuotos.gaana.sqliteDB.PlaylistDBManager;
import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 12/16/17
 */

public class AddInPlaylist extends DialogFragment {

    ArrayList<String> list;
    PlaylistDBManager playlistDBManager;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SimpleListRecyclerViewAdapter simpleListRecyclerViewAdapter;

    TextView close;
    Context context;
    Audio audio;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_linear_list,null,false);

        close = view.findViewById(R.id.add_playlist_close);
        close.setOnClickListener(onCloseClickListener);

        findAllPlaylist();
        recyclerView = view.findViewById(R.id.simple_list_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        simpleListRecyclerViewAdapter = new SimpleListRecyclerViewAdapter(context, list, new SimpleListRecyclerViewAdapter.OnItemClickListener(){
            @Override
            public void OnItemClick(int item,String playlist_name) {
                //add audio to this --> list.get(item) playlist
                addThisToPlaylist(playlist_name);
            }
        });
        recyclerView.setAdapter(simpleListRecyclerViewAdapter);



        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audio = getArguments().getParcelable("audio");
        this.context = getContext();
    }

    View.OnClickListener onCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    public void findAllPlaylist(){
        list = new ArrayList<>();
        playlistDBManager = new PlaylistDBManager(context);
        SQLiteDatabase db = playlistDBManager.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c != null && c.getCount()>0) {
            while (c.moveToNext()) {
                String table_name = c.getString(0);
                if(!(table_name.equals("android_metadata") || table_name.equals("sqlite_sequence"))){
                    list.add(c.getString(0));
                }
            }
        }c.close();
    }

    public void addThisToPlaylist(String playlist_name){

        playlistDBManager = new PlaylistDBManager(context);
        SQLiteDatabase db = playlistDBManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AudioContract.playlist.COLUMN_TRACK_TITLE,audio.title);
        db.insert(playlist_name,null,values);
        db.close();

        Toast.makeText(context, context.getResources().getString(R.string.track_added) + playlist_name, Toast.LENGTH_SHORT).show();
    }

}
