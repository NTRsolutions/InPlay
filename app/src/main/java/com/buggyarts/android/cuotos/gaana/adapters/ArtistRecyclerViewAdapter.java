package com.buggyarts.android.cuotos.gaana.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.android.cuotos.gaana.R;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.MetadataAPI;
import com.bumptech.glide.Glide;

/**
 * Created by mayank on 11/16/17
 */

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.MyViewHolder> {

    public interface OnItemClickListener{
        void OnItemClick(Audio audio);
    }

    private ArrayList<Audio> artist_list,artist_tracks;
    private Context context;
    private OnItemClickListener listener;
    private AudioDBManager dbManager;

    public ArtistRecyclerViewAdapter(Context context, ArrayList<Audio> list, OnItemClickListener listener){
        this.context = context;
        this.artist_list = list;
        this.listener = listener;
    }

    @Override
    public ArtistRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View artist_view = LayoutInflater.from(context).inflate(R.layout.model_grid_2,parent,false);
        return new MyViewHolder(artist_view);
    }

    @Override
    public void onBindViewHolder(ArtistRecyclerViewAdapter.MyViewHolder holder, int position) {
        final Audio artist_card = artist_list.get(position);
        String artist_name = artist_card.artist;
        holder.artist_name.setText(artist_name);
        Glide.with(context).load(artist_card.artist_image).asBitmap().into(holder.artist_thumbnail);
        holder.artist_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClick(artist_card);
//                Toast.makeText(context,"You just Clicked "+artist_card.artist,Toast.LENGTH_SHORT).show();

//                artist_tracks = new ArrayList<Audio>();
//                String[] arguments = {artist_card.artist};
//                load_artist_tracks(arguments);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artist_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView artist_name;
        private TextView track_count;
        private ImageView artist_thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);

            artist_name = itemView.findViewById(R.id.album_view_title);
            track_count = itemView.findViewById(R.id.album_view_count);
            artist_thumbnail = itemView.findViewById(R.id.album_view_thumbnail);
        }

    }

    public void load_artist_tracks(String[] argument){
        dbManager = new AudioDBManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String[] columns = {"data","title","album","album_id","artist"};

        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,"artist = ?",argument,null,null,"title");
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String album = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));

                Log.v("album tracks",""+title);
                artist_tracks.add(new Audio(data,title,album,album_id,artist));
            }
        }cursor.close();
    }
}
