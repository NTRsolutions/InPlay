package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;

/**
 * Created by mayank on 11/15/17
 */

public class Songs_Fragment extends Fragment {


    TrackOptionsFragment trackOptionsFragment;
    FragmentManager fragmentManager;
    private AudioDBManager dbManager;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private Context context;
    private ArrayList<Audio> songs;
    PlayingQueue playingQueue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.act1_fragment_songs,container,false);

        recyclerView = fragment_view.findViewById(R.id.song_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SongsRecyclerViewAdapter(context,songs,onTrackSelect,onTrackOptionClick);
        recyclerView.setAdapter(adapter);

//        trackOptionsFragment = new TrackOptionsFragment();
//        fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.track_options,trackOptionsFragment,"trackOptions");
//        fragmentTransaction.hide(trackOptionsFragment);
//        fragmentTransaction.commit();

        return fragment_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        dbManager = new AudioDBManager(context);
        songs = new ArrayList<>();
        loadList();
    }

    private void loadList(){

        SQLiteDatabase db = dbManager.getReadableDatabase();

        String[] columns = {"data","title","artist","album_id",audioEntry.COLUMN_DURATION_MIN,audioEntry.COLUMN_DURATION_SEC};
        String order_by = "title";

        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,null,null,null,null,audioEntry.COLUMN_ALBUM);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));
                int min = cursor.getInt(cursor.getColumnIndex(audioEntry.COLUMN_DURATION_MIN));
                int sec = cursor.getInt(cursor.getColumnIndex(audioEntry.COLUMN_DURATION_SEC));

                songs.add(new Audio(data,title,artist,album_id,min,sec));
            }
        }cursor.close();
    }

    public interface PlayingQueue{
         void queue(ArrayList<Audio> list, int index);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        playingQueue = (PlayingQueue) context;
    }

    /**
     * Click Listeners
     */

    SongsRecyclerViewAdapter.OnItemClickListener onTrackSelect = new SongsRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int index,Audio track) {
            Log.v("Clicked",""+index);
            //playingQueue.queue(songs,index);
            EventBus.getDefault().post(new UpdatePlayingQueue(songs,index,Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromSingles));
        }
    };

    SongsRecyclerViewAdapter.OnOptionsClickListener onTrackOptionClick = new SongsRecyclerViewAdapter.OnOptionsClickListener() {
        @Override
        public void onItemClick(int index, Audio track) {
            EventBus.getDefault().post(new OnTrackOptionsClick(index,track));
        }
    };

}
