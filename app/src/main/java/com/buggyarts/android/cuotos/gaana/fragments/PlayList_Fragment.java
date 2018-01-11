package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.buggyarts.android.cuotos.gaana.CreateNewPlaylist;
import com.buggyarts.android.cuotos.gaana.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import com.buggyarts.android.cuotos.gaana.adapters.PlaylistExpandableRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.events.UpdatePlayingQueue;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.PlaylistDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.playlist;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.PlaylistExpandableGroup;

/**
 * Created by mayank on 11/15/17
 */

public class PlayList_Fragment extends Fragment{

    RelativeLayout add_playlist;
    Context context;
    PlaylistDBManager playlistDBManager;
    AudioDBManager audioDBManager;
    RecyclerView playlists_recycler_view;
    RecyclerView.LayoutManager layoutManager;
    PlaylistExpandableRecyclerViewAdapter adapter;
    List<PlaylistExpandableGroup> expandableGroupList;
    ArrayList<Audio> queue;
    int queueIndex;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.act1_fragment_playlists,container,false);

        add_playlist = fragment_view.findViewById(R.id.add_playlist);
        add_playlist.setOnClickListener(add_new_playlist);

        playlists_recycler_view = fragment_view.findViewById(R.id.play_list);
        layoutManager = new LinearLayoutManager(context);
        playlists_recycler_view.setLayoutManager(layoutManager);
        expandableGroupList = new ArrayList<>();
        findAllTablesInPlaylistDB();
        adapter = new PlaylistExpandableRecyclerViewAdapter(context, expandableGroupList, new PlaylistExpandableRecyclerViewAdapter.PassDataOnClick() {
            @Override
            public void PassDataOnClick(ArrayList<Audio> tracks, int index) {
                queue = tracks;
                queueIndex = index;
                EventBus.getDefault().post(new UpdatePlayingQueue(tracks,index, Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromPlaylist));

            }
        });
        playlists_recycler_view.setAdapter(adapter);

        return fragment_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        lbm.registerReceiver(playlist_refresh_receiver,new IntentFilter("refresh playlist"));
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
//        MediaRetriever retriever = new MediaRetriever(context);
//        retriever.storeQueueValues(queue,queueIndex);
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(playlist_refresh_receiver);
    }

    public View.OnClickListener add_new_playlist = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            startActivity(new Intent(context,CreateNewPlaylist.class));
        }
    };

    public BroadcastReceiver playlist_refresh_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Receiver","intent received");
            if(intent!= null){
                if(intent.getBooleanExtra("shouldRefresh",false)){
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void findAllTablesInPlaylistDB(){
        playlistDBManager = new PlaylistDBManager(context);
        SQLiteDatabase db = playlistDBManager.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c != null && c.getCount()>0) {
            while (c.moveToNext()) {
                String table_name = c.getString(0);
                if(!(table_name.equals("android_metadata") || table_name.equals("sqlite_sequence"))){
//                    Toast.makeText(context, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                    extractThisPlaylist(c.getString(0));
                }
            }
        }c.close();
    }

    public void extractThisPlaylist(String playlist_name){

        ArrayList<Audio> tracks = new ArrayList<>();

        playlistDBManager = new PlaylistDBManager(context);
        SQLiteDatabase db = playlistDBManager.getReadableDatabase();
        String[] title = {playlist.COLUMN_TRACK_TITLE};
        Cursor cursor = db.query(playlist_name,title,null,null,null,null,null);
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                String track_title = cursor.getString(cursor.getColumnIndex(playlist.COLUMN_TRACK_TITLE));
                fillPlaylistWithTracks(track_title,tracks);
            }
        }cursor.close();

        expandableGroupList.add(new PlaylistExpandableGroup(playlist_name,tracks));
    }

    public void fillPlaylistWithTracks(String track_title, ArrayList<Audio> songs){
        audioDBManager = new AudioDBManager(context);
        SQLiteDatabase db = audioDBManager.getReadableDatabase();

        String[] columns = {"data","title","artist","album_id"};
        String[] args = {track_title};

        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,audioEntry.COLUMN_TITLE + "=?",args,null,null,null);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));
//                Log.v("TRACK ADDED WITH TITLE",title);
                songs.add(new Audio(data,title,artist,album_id));
            }
        }cursor.close();
    }

}
