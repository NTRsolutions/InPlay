package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.buggyarts.android.cuotos.gaana.R;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.adapters.AlbumExpandableRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.events.UpdatePlayingQueue;
import com.buggyarts.android.cuotos.gaana.sqliteDB.ArtistDBManager;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.artistEntry;
import com.buggyarts.android.cuotos.gaana.utils.AlbumContent;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;

/**
 * Created by mayank on 12/8/17
 */

public class ArtistAlbumsFragment extends Fragment {

    ArtistDBManager artistDBManager;
    ArrayList<String> albumList;
    ArrayList<AlbumContent> expandableGroupList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AlbumExpandableRecyclerViewAdapter albumExpandableRecyclerViewAdapter;
    ImageView album_bg;
    Context context;
    String artistName;
    ArrayList<Audio> queue;
    int queueIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View artistAlbumsFragment_view = inflater.inflate(R.layout.act1_fragment_albums,container,false);

        recyclerView = artistAlbumsFragment_view.findViewById(R.id.albums_recycler_view);
        album_bg = artistAlbumsFragment_view.findViewById(R.id.albums_bg);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        albumExpandableRecyclerViewAdapter = new AlbumExpandableRecyclerViewAdapter(context,expandableGroupList,album_bg,new AlbumExpandableRecyclerViewAdapter.PassDataOnClick() {
            @Override
            public void PassDataOnClick(ArrayList<Audio> tracks, int index) {
                queue = tracks;
                queueIndex = index;
                Glide.with(context).load(MediaRetriever.getAlbumArt(queue.get(queueIndex).getAlbum_id())).asBitmap().centerCrop().into(album_bg);
                EventBus.getDefault().post(new UpdatePlayingQueue(tracks,index, Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromAlbum));
            }
        });
        recyclerView.setAdapter(albumExpandableRecyclerViewAdapter);

        return artistAlbumsFragment_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expandableGroupList = new ArrayList<>();

        this.context = getContext();
        artistDBManager = new ArtistDBManager(context);
        Bundle bundle = getArguments();
        artistName = bundle.getString("artist_name");
        loadArtistAlbums(artistName);
    }

    public void loadArtistAlbums(String artist_name){

        albumList = new ArrayList<>();

        SQLiteDatabase db = artistDBManager.getReadableDatabase();
        String[] columns = {artistEntry.COLUMN_ALBUM};
        String[] selectArgs = {artist_name};
        Cursor cursor = db.query(artistEntry.TABLE_NAME,columns,artistEntry.COLUMN_ARTIST + "=?",selectArgs,artistEntry.COLUMN_ALBUM,null,artistEntry.COLUMN_ALBUM);
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                String album = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_ALBUM));

                loadAlbumTracks(album,artist_name);
            }
        }cursor.close();
    }

    public void loadAlbumTracks(String album_name,String artist_name){
        ArrayList<Audio> trackList = new ArrayList<>();
        String album_id = null;
        SQLiteDatabase db = artistDBManager.getReadableDatabase();
        String[] columns = {artistEntry.COLUMN_DATA,artistEntry.COLUMN_TRACK,artistEntry.COLUMN_AlBUM_ID};
        String[] selectArgs = {artist_name,album_name};
        String selection = artistEntry.COLUMN_ARTIST + " = ? AND " + artistEntry.COLUMN_ALBUM + " = ? ";
        Cursor cursor = db.query(artistEntry.TABLE_NAME,columns,selection,selectArgs,null,null,artistEntry.COLUMN_ALBUM);
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_DATA));
                String track_title = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_TRACK));
                album_id = cursor.getString(cursor.getColumnIndex(artistEntry.COLUMN_AlBUM_ID));

                trackList.add(new Audio(data,track_title,album_name,album_id,artist_name));
            }
        }cursor.close();
        expandableGroupList.add(new AlbumContent(album_name,trackList,album_id));
    }

}
