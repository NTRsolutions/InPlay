package com.buggyarts.android.cuotos.gaana.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;

import java.util.ArrayList;


/**
 * Created by mayank on 11/16/17
 */

public class SimpleListRecyclerViewAdapter extends RecyclerView.Adapter<SimpleListRecyclerViewAdapter.MyViewHolder> {

    public interface OnItemClickListener{
        void OnItemClick(int index,String item);
    }

    private ArrayList<String> playlist_list;
    private Context context;
    private OnItemClickListener listener;


    //Default Constructor
    public SimpleListRecyclerViewAdapter(Context context,ArrayList<String> playlists, OnItemClickListener listener){
        this.context = context;
        this.playlist_list = playlists;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumList_view = LayoutInflater.from(context).inflate(R.layout.add_in_playlist_list,parent,false);
        return new MyViewHolder(albumList_view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int index = position;
        final String playlist_name = playlist_list.get(position);
        holder.title.setText(playlist_name);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClick(index,playlist_name);
            }
        });

    }

    @Override
    public int getItemCount() {
        return playlist_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private RelativeLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.play_list_name);
            layout = itemView.findViewById(R.id.playlist_name_container_layout);
        }
    }
}
