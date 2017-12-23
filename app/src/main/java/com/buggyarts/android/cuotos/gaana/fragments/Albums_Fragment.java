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
import android.widget.RelativeLayout;

import com.buggyarts.android.cuotos.gaana.R;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import com.buggyarts.android.cuotos.gaana.adapters.AlbumExpandableRecyclerViewAdapter;
import com.buggyarts.android.cuotos.gaana.events.UpdatePlayingQueue;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.utils.AlbumContent;
import com.buggyarts.android.cuotos.gaana.utils.Albums;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;
import com.buggyarts.android.cuotos.gaana.utils.Tracks;

/**
 * Created by mayank on 11/15/17
 */
public class Albums_Fragment extends Fragment {

//    private ArrayList<Audio> albums;
    private Context context;
    private AudioDBManager dbManager;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
//    private AlbumRecyclerViewAdapter adapter;
    private AlbumExpandableRecyclerViewAdapter adapter;
    public RelativeLayout layout;
    public ImageView image_background;
    private List<AlbumContent> albums_list;

    //Test set
    List<Albums> albums;
    List<Tracks> album1_tracks,album2_tracks,album3_tracks;

    ArrayList<Audio> queue;
    int queueIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.act1_fragment_albums,container,false);

        recyclerView = fragment_view.findViewById(R.id.albums_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        image_background = fragment_view.findViewById(R.id.albums_bg);
        layout = fragment_view.findViewById(R.id.albums_layout);

//        adapter = new AlbumRecyclerViewAdapter(context, albums, clickListener);
        album1_tracks = new ArrayList<>();
        album2_tracks = new ArrayList<>();
        album3_tracks = new ArrayList<>();
        albums = new ArrayList<>();
        createList();

        albums_list = new ArrayList<>();
        load_albums();
        adapter = new AlbumExpandableRecyclerViewAdapter(context, albums_list,image_background,new AlbumExpandableRecyclerViewAdapter.PassDataOnClick() {
            @Override
            public void PassDataOnClick(ArrayList<Audio> tracks, int index) {
                queue = tracks;
                queueIndex = index;
                Glide.with(context).load(MediaRetriever.getAlbumArt(queue.get(queueIndex).getAlbum_id())).asBitmap().centerCrop().into(image_background);
                EventBus.getDefault().post(new UpdatePlayingQueue(tracks,index, Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromAlbum));
            }
        });
        recyclerView.setAdapter(adapter);

        return fragment_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
//        albums = new ArrayList<>();

        dbManager = new AudioDBManager(context);

    }

//    AlbumRecyclerViewAdapter.OnItemClickListener clickListener = new AlbumRecyclerViewAdapter.OnItemClickListener() {
//        @Override
//        public void OnItemClick(Audio item) {
//            Toast.makeText(getContext(),"You just clicked "+ item.album,Toast.LENGTH_SHORT).show();
//        }
//    };

    public void createList(){
        album1_tracks.add(new Tracks("track1"));
        album1_tracks.add(new Tracks("track2"));
        album1_tracks.add(new Tracks("track3"));
        album1_tracks.add(new Tracks("track4"));
        album2_tracks.add(new Tracks("track5"));
        album2_tracks.add(new Tracks("track6"));
        album2_tracks.add(new Tracks("track7"));
        album2_tracks.add(new Tracks("track8"));
        album3_tracks.add(new Tracks("track9"));
        album3_tracks.add(new Tracks("track10"));
        album3_tracks.add(new Tracks("track11"));
        album3_tracks.add(new Tracks("track12"));
        albums.add(new Albums("Album1",album1_tracks));
        albums.add(new Albums("Album2",album2_tracks));
        albums.add(new Albums("Album3",album3_tracks));
        albums.add(new Albums("Album4",album1_tracks));
        albums.add(new Albums("Album5",album3_tracks));
        albums.add(new Albums("Album6",album3_tracks));
    }

    public void load_albums(){
        SQLiteDatabase db = dbManager.getReadableDatabase();

        String[] columns = {"data","album","album_id",};
        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,null,null,"album",null,"album");
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                ArrayList<Audio> track_list = new ArrayList<>();
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String album = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));

//                albums.add(new Audio(data,album,album_id));
                String album_name = album;
                load_album_tracks(album_name, track_list);
//                int i = 0;
//                while(i < track_list.size()){
//                    Log.v("track_list","["+ i +"]  :" + track_list.get(i).title);
//                    i++;
//                }
                albums_list.add(new AlbumContent(album,track_list,album_id));
            }
        }cursor.close();
    }

    public void load_album_tracks(String album_title, ArrayList<Audio> list_of_tracks){

        String[] argument = {album_title};
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String[] columns = {"data","title","album","album_id","artist"};

        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,"album = ?",argument,null,null,"title");
        if(cursor!=null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String album = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));

//                Log.v("album tracks", "" + title);
                list_of_tracks.add(new Audio(data, title, album, album_id, artist));
            }
            cursor.close();
        }
    }

}
