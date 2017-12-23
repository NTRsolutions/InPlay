package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.adapters.PlayListRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.SwipeHelper;


/**
 * Created by mayank on 11/29/17
 */

public class PlaylistAddTracksFragment extends Fragment {

    public interface NewPlaylist {
        void createNewPlaylistWithTracks(ArrayList<String> tracks);
    }

    AudioDBManager dbManager;
    Context context;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PlayListRecyclerViewAdapter adapter;
    ArrayList<Audio> songs;
    NewPlaylist newPlaylist;
    ArrayList<String> audio_tracks;

    TextView finish;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist_add_tracks,container,false);

        recyclerView = v.findViewById(R.id.add_playlist_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        audio_tracks = new ArrayList<>();

        adapter = new PlayListRecyclerViewAdapter(context, songs, new PlayListRecyclerViewAdapter.AddToPlaylist() {
            @Override
            public void addToPlaylist(Audio audio) {
//                Log.v("PLAYLIST ADD TRACK",audio.title);
                audio_tracks.add(audio.title);
            }
        });

        recyclerView.setAdapter(adapter);

        finish = v.findViewById(R.id.finish_new_playlist);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPlaylist.createNewPlaylistWithTracks(audio_tracks);
            }
        });

        ItemTouchHelper.Callback callback = new SwipeHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        dbManager = new AudioDBManager(context);
        songs = new ArrayList<>();
        loadList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        newPlaylist = (NewPlaylist) context;
    }

    private void loadList(){

        SQLiteDatabase db = dbManager.getReadableDatabase();

        String[] columns = {"data","title","artist","album_id"};
        String order_by = "title";

        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,null,null,null,null,audioEntry.COLUMN_ALBUM);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));

                songs.add(new Audio(data,title,artist,album_id));
            }
        }cursor.close();
    }

}