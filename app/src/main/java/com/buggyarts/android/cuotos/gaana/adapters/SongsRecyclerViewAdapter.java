package com.buggyarts.android.cuotos.gaana.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;
import com.buggyarts.android.cuotos.gaana.utils.MetadataAPI;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by mayank on 11/15/17
 */

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.MyViewHolder> {

    public ArrayList<Audio> list;
    public Context context;

    public interface OnItemClickListener{
        void onItemClick(int index,Audio track);
    }

    public interface OnOptionsClickListener{
        void onItemClick(int index,Audio track);
    }


    OnItemClickListener onItemClickListener;
    OnOptionsClickListener onOptionsClickListener;


    public SongsRecyclerViewAdapter(Context context, ArrayList<Audio> audioList, OnItemClickListener listener,OnOptionsClickListener optionsClickListener){
        this.context = context;
        this.list = audioList;
        this.onItemClickListener = listener;
        this.onOptionsClickListener = optionsClickListener;
    }


    @Override
    public SongsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View songList_view = LayoutInflater.from(context).inflate(R.layout.model_list_1,parent,false);
        return new MyViewHolder(songList_view);
    }

    @Override
    public void onBindViewHolder(final SongsRecyclerViewAdapter.MyViewHolder holder, int position) {
        final Audio audio = list.get(position);
        final int index = position;
        holder.title.setText(audio.title);
        holder.artist.setText(audio.artist);
        String sec = String.format("%02d", audio.sec);
        String duration = audio.min + ":" + sec;
        holder.duration.setText(duration);

        Glide.with(context).load(MediaRetriever.getAlbumArt(audio.album_id)).asBitmap().into(holder.thumbnail);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(index,audio);
//                Log.v("Track",""+audio.title);
            }
        });
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsClickListener.onItemClick(index,audio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView artist;
        public ImageView thumbnail;
        public TextView duration;
        public RelativeLayout layout;
        public ImageView options;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);
            artist = itemView.findViewById(R.id.item_artist);
            thumbnail = itemView.findViewById(R.id.item_art);
            duration = itemView.findViewById(R.id.duration);
            layout = itemView.findViewById(R.id.item_model_1);
            options = itemView.findViewById(R.id.more_options);
        }

    }

}
