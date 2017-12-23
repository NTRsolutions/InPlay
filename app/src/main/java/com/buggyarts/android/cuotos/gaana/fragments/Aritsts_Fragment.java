package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buggyarts.android.cuotos.gaana.ArtistActivity;
import com.buggyarts.android.cuotos.gaana.R;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.adapters.ArtistRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.sqliteDB.ArtistDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.artistEntry;
import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 11/15/17
 */

public class Aritsts_Fragment extends Fragment {

    private ArrayList<Audio> artist_list;
    private Context context;
    private SQLiteDatabase db;
    private ArtistDBManager dbManager;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.act1_fragment_artists,container,false);

        recyclerView = fragment_view.findViewById(R.id.artist_recycler_view);
        layoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ArtistRecyclerViewAdapter(context, artist_list, listener);
        recyclerView.setAdapter(adapter);
        return fragment_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        artist_list = new ArrayList<>();
        dbManager = new ArtistDBManager(context);
//        load_artist();
        loadArtist();
    }

    ArtistRecyclerViewAdapter.OnItemClickListener listener = new ArtistRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(Audio audio) {
            Intent artistActivity = new Intent(context, ArtistActivity.class);
            artistActivity.putExtra("artist",audio.artist);
            startActivity(artistActivity);
        }
    };

    public void loadArtist(){
        db = dbManager.getReadableDatabase();
        String[] columns = {artistEntry.COLUMN_ARTIST};

        Cursor cursor = db.query(artistEntry.TABLE_NAME,columns,null,null, artistEntry.COLUMN_ARTIST,null,artistEntry.COLUMN_ARTIST);
        if(cursor != null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                String artist = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_ARTIST));

                artist_list.add(new Audio(artist));
            }
        }cursor.close();
    }

    //    public void load_artist(){
//        db = dbManager.getReadableDatabase();
//        String[] columns = {"artist"};
//        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,null,null,"artist",null,"artist");
//        if(cursor != null && cursor.getCount()>0){
//            while (cursor.moveToNext()){
//                String artists = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));
//                StringManipulator manipulator = new StringManipulator(artists);
//                ArrayList<String> artist= manipulator.refineString();
//                for(int i = 0; i<artist.size();i++){
//                    artist_list.add(new Audio(artist.get(i)));
//                }
//            }
//        }cursor.close();
//    }
}
