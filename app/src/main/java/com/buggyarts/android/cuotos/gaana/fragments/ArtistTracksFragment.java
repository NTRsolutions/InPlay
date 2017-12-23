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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.adapters.SongsRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.events.OnTrackOptionsClick;
import com.buggyarts.android.cuotos.gaana.events.UpdatePlayingQueue;
import com.buggyarts.android.cuotos.gaana.sqliteDB.ArtistDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.artistEntry;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;

/**
 * Created by mayank on 12/8/17
 */

public class ArtistTracksFragment extends Fragment {

    Context context;
    ArtistDBManager artistDBManager;
    ArrayList<Audio> trackList;
    String artistName;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SongsRecyclerViewAdapter songsRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View artistTracksFragment_view = inflater.inflate(R.layout.act1_fragment_songs,container,false);

        recyclerView = artistTracksFragment_view.findViewById(R.id.song_recycler_view);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        songsRecyclerViewAdapter = new SongsRecyclerViewAdapter(context,trackList,onTrackSelect,onTrackOptionClick);

        recyclerView.setAdapter(songsRecyclerViewAdapter);

        return artistTracksFragment_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getContext();
        artistDBManager = new ArtistDBManager(context);
        Bundle bundle = getArguments();
        artistName = bundle.getString("artist_name");
        loadTracks(artistName);

    }

    public void loadTracks(String artist_name){

        trackList = new ArrayList<>();

        SQLiteDatabase db = artistDBManager.getReadableDatabase();

        String[] columns = {artistEntry.COLUMN_DATA,artistEntry.COLUMN_TRACK,artistEntry.COLUMN_ALBUM,artistEntry.COLUMN_AlBUM_ID,artistEntry.COLUMN_DURATION_MIN,artistEntry.COLUMN_DURATION_SEC};
        String[] selectArgs = {artist_name};
        Cursor cursor = db.query(artistEntry.TABLE_NAME,columns,artistEntry.COLUMN_ARTIST + "=?",selectArgs,null,null,artistEntry.COLUMN_ALBUM);
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_DATA));
                String track_title = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_TRACK));
                String album = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_ALBUM));
                String album_id = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_AlBUM_ID));
                int min = cursor.getInt(cursor.getColumnIndex(artistEntry.COLUMN_DURATION_MIN));
                int sec = cursor.getInt(cursor.getColumnIndex(artistEntry.COLUMN_DURATION_SEC));


                trackList.add(new Audio(data,track_title,album,album_id,artist_name,min,sec));
            }
        }cursor.close();
    }

    /**
     * Click Listeners
     */

    SongsRecyclerViewAdapter.OnOptionsClickListener onTrackOptionClick = new SongsRecyclerViewAdapter.OnOptionsClickListener() {
        @Override
        public void onItemClick(int index, Audio track) {
            EventBus.getDefault().post(new OnTrackOptionsClick(index,track));
        }
    };

    SongsRecyclerViewAdapter.OnItemClickListener onTrackSelect = new SongsRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int index,Audio track) {
            Log.v("Clicked",""+index);
            //playingQueue.queue(songs,index);
            EventBus.getDefault().post(new UpdatePlayingQueue(trackList,index,Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromSingles));
        }
    };

}
