package com.buggyarts.android.cuotos.gaana.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;

/**
 * Created by mayank on 12/1/17
 */

public class  PlayListRecyclerViewAdapter extends RecyclerView.Adapter<PlayListRecyclerViewAdapter.MyViewHolder> {

    public interface AddToPlaylist{
        void addToPlaylist(Audio audio);
    }

    Context context;
    ArrayList<Audio> track_list;
    AddToPlaylist add_In_Playlist;


    public PlayListRecyclerViewAdapter(Context context,ArrayList<Audio> tracks,AddToPlaylist list){
        this.context = context;
        this.track_list = tracks;
        this.add_In_Playlist = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View playlist_view = LayoutInflater.from(context).inflate(R.layout.model_list_2,parent,false);
        return new MyViewHolder(playlist_view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Audio audio = track_list.get(position);
        final int index = position;
        holder.title.setText(audio.title);
        holder.artist.setText(audio.artist);
        Glide.with(context).load(MediaRetriever.getAlbumArt(audio.album_id)).asBitmap().into(holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                add_In_Playlist.addToPlaylist(audio);
                Log.v("PLAYLIST ADD TRACK", index +" : "+audio.title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return track_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;
        TextView title;
        TextView artist;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.thumbnail = itemView.findViewById(R.id.item_art);
            this.title = itemView.findViewById(R.id.item_title);
            this.artist = itemView.findViewById(R.id.item_artist);
        }
    }

    public void dismissItem(int pos){


        Log.v("SWIPE FEED", pos +" : "+track_list.get(pos).title);
        add_In_Playlist.addToPlaylist(track_list.get(pos));
        track_list.remove(pos);
        this.notifyItemRemoved(pos);
    }

}
