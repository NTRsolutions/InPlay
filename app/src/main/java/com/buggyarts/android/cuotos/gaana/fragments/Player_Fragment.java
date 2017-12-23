package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.buggyarts.android.cuotos.gaana.events.NowPlayingTrackInfo;
import com.buggyarts.android.cuotos.gaana.events.PreviousPlayPauseNext;
import com.buggyarts.android.cuotos.gaana.events.UpdateProgress;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;

/**
 * Created by mayank on 11/25/17
 */

public class Player_Fragment extends Fragment implements SeekBar.OnSeekBarChangeListener{
    private Context context;
    private ImageView close_view;

    private ImageView thumbnail,play_previous,play_pause,play_next;
    private TextView track_title;
    private TextView track_artist;
    private Audio nowPlayingTrack;
    private SeekBar seekBar;
    private boolean isPlaying = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View player_view = inflater.inflate(R.layout.fragment_player,container,false);

        thumbnail = player_view.findViewById(R.id.player_thumbnail);
        track_title = player_view.findViewById(R.id.player_title);
        track_artist = player_view.findViewById(R.id.player_artist);
        seekBar = player_view.findViewById(R.id.seek_bar);

        play_pause = player_view.findViewById(R.id.play_pause);
        play_pause.setOnClickListener(play_pause_listener);
        play_previous = player_view.findViewById(R.id.play_previous);
        play_previous.setOnClickListener(play_previous_listener);
        play_next = player_view.findViewById(R.id.play_next);
        play_next.setOnClickListener(play_next_listener);

        close_view = player_view.findViewById(R.id.player_close);
        close_view.setOnClickListener(click_closePlayer);
        Log.v("Called","onCreateView");
        return player_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private View.OnClickListener click_closePlayer = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
            Player_Fragment player_fragment = (Player_Fragment) manager.findFragmentByTag("playerFragment");
            fragmentTransaction.hide(player_fragment);
            fragmentTransaction.commit();
        }
    };

    public View.OnClickListener play_pause_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isPlaying){
                EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PAUSE));
                play_pause.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                isPlaying = false;
            }else {
                EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PLAY));
                play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                isPlaying = true;
            }
        }
    };

    public View.OnClickListener play_previous_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PREVIOUS));
        }
    };

    public View.OnClickListener play_next_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.NEXT));
        }
    };

    @Subscribe
    public void updateInfo(NowPlayingTrackInfo track){
        nowPlayingTrack = track.getTrack();

        Glide.with(context).load(MediaRetriever.getAlbumArt(nowPlayingTrack.album_id)).asBitmap().into(thumbnail);
        track_title.setText(nowPlayingTrack.title);
        track_artist.setText(nowPlayingTrack.artist);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void updateSeekBar(UpdateProgress updateProgress){
        seekBar.setMax(updateProgress.getMaxDuration());
        seekBar.setProgress(updateProgress.getProgress());
    }

    @Subscribe
    public void onPreviousPlayPauseNext(PreviousPlayPauseNext option){
        switch(option.getControlCondition()){
            case Constants.PREVIOUS:
                break;
            case Constants.PLAY:
                play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                break;
            case Constants.PAUSE:
                play_pause.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                break;
            case Constants.NEXT:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
