package com.buggyarts.android.cuotos.gaana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.buggyarts.android.cuotos.gaana.R;
import com.bumptech.glide.Glide;
import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter;
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import com.buggyarts.android.cuotos.gaana.events.UpdatePlayingQueue;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;
import com.buggyarts.android.cuotos.gaana.utils.PlaylistExpandableGroup;
import com.buggyarts.android.cuotos.gaana.vHolders.PlaylistGVH;
import com.buggyarts.android.cuotos.gaana.vHolders.PlaylistTracksCCVH;

/**
 * Created by mayank on 12/2/17
 */

public class PlaylistExpandableRecyclerViewAdapter extends CheckableChildRecyclerViewAdapter<PlaylistGVH, PlaylistTracksCCVH> {

    private Context context;
    private RelativeLayout childLayout;

    public interface PassDataOnClick{
        void PassDataOnClick(ArrayList<Audio> tracks,int index);
    }

    public PassDataOnClick passDataOnClick;

    public PlaylistExpandableRecyclerViewAdapter(Context context, List<PlaylistExpandableGroup> groups, PassDataOnClick passDataOnClick) {
        super(groups);
        this.context = context;
        this.passDataOnClick = passDataOnClick;
    }

    @Override
    public PlaylistGVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View playlist_header = LayoutInflater.from(context).inflate(R.layout.playlist_item_header,parent,false);
        return new PlaylistGVH(playlist_header);
    }

    @Override
    public void onBindGroupViewHolder(PlaylistGVH holder, int flatPosition, final ExpandableGroup group) {
        holder.playlist_header_name.setText(group.getTitle().replace('_',' '));
        String details = context.getResources().getString(R.string.no_of_tracks)+ ":" + group.getItemCount();
        holder.playListDetails.setText(details);
        holder.clickToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new UpdatePlayingQueue((ArrayList<Audio>) group.getItems(),0, Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromPlaylist));
            }
        });
    }

    @Override
    public PlaylistTracksCCVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View playlist_tracks_view = LayoutInflater.from(context).inflate(R.layout.model_list_3,parent,false);
//        childLayout = playlist_tracks_view.findViewById(R.id.item_model_1);
        return new PlaylistTracksCCVH(playlist_tracks_view);
    }

    @Override
    public void onBindChildViewHolder(PlaylistTracksCCVH holder, int flatPosition, final ExpandableGroup group, int childIndex) {
        final int index = childIndex;
        final int indexWithOfset = childIndex;
        Audio audio = (Audio) group.getItems().get(childIndex);
        holder.title.setText(audio.title);
        holder.artist.setText(audio.artist);
        Glide.with(context).load(MediaRetriever.getAlbumArt(audio.album_id)).asBitmap().into(holder.thumbnail);
        holder.childLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passDataOnClick.PassDataOnClick((ArrayList<Audio>) group.getItems(),index);
//                if(Constants.isPlaylistPlaying){
//                    EventBus.getDefault().post(new PlayTrackOfIndex(index));
//                }
            }
        });
    }

    @Override
    public PlaylistTracksCCVH onCreateCheckChildViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindCheckChildViewHolder(PlaylistTracksCCVH holder, int flatPosition, CheckedExpandableGroup group, int childIndex) {
    }

}
