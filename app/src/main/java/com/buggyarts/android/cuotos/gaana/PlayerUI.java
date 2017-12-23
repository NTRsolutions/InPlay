package com.buggyarts.android.cuotos.gaana;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.buggyarts.android.cuotos.gaana.events.NowPlayingTrackInfo;
import com.buggyarts.android.cuotos.gaana.events.PreviousPlayPauseNext;
import com.buggyarts.android.cuotos.gaana.events.SeekBarChange;
import com.buggyarts.android.cuotos.gaana.events.UpdateInfo;
import com.buggyarts.android.cuotos.gaana.events.UpdateProgress;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;

public class PlayerUI extends AppCompatActivity{

    ImageView close_view;

    ImageView thumbnail,play_previous,play_pause,play_next,play_loop,play_shuffle;
    TextView track_title;
    TextView track_artist;
    Audio nowPlayingTrack;
    SeekBar seekBar;
    boolean isPlaying = true;
    int previousValue = 0;
    int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_ui);

        thumbnail = (ImageView) findViewById(R.id.player_thumbnail);
        track_title = (TextView) findViewById(R.id.player_title);
        track_artist = (TextView) findViewById(R.id.player_artist);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);

        play_loop = (ImageView) findViewById(R.id.play_loop);
        play_loop.setOnClickListener(play_loop_listener);
        play_shuffle = (ImageView) findViewById(R.id.play_shuffle);
        play_shuffle.setOnClickListener(play_shuffle_listener);

        play_pause = (ImageView) findViewById(R.id.play_pause);
        play_pause.setOnClickListener(play_pause_listener);
        play_previous = (ImageView) findViewById(R.id.play_previous);
        play_previous.setOnClickListener(play_previous_listener);
        play_next = (ImageView) findViewById(R.id.play_next);
        play_next.setOnClickListener(play_next_listener);

        close_view = (ImageView) findViewById(R.id.player_close);
        close_view.setOnClickListener(click_closePlayer);
        Log.v("Called","onCreateView");

        if(Constants.isTrackPlaying){
            play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    EventBus.getDefault().post(new SeekBarChange(progress));
                    previousValue = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        EventBus.getDefault().post(new UpdateInfo());
        Log.v("PLAYER ACTIVITY","RESUMED");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.v("PLAYER ACTIVITY","PAUSED");
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * EventBus Subscribe methods
     */

    @Subscribe
    public void updateInfo(NowPlayingTrackInfo track){
        nowPlayingTrack = track.getTrack();

        Glide.with(this).load(MediaRetriever.getAlbumArt(nowPlayingTrack.album_id)).asBitmap().into(thumbnail);
        track_title.setText(nowPlayingTrack.title);
        track_artist.setText(nowPlayingTrack.artist);
        previousValue = 0;
    }

    @Subscribe
    public void updateSeekBar(UpdateProgress updateProgress){

        int currentValue = updateProgress.getProgress();
        duration = updateProgress.getMaxDuration();
        seekBar.setMax(duration);

        if((currentValue - previousValue) > 500){
            Log.v("SEEKBAR","PRE: " + previousValue + " CUR: " +currentValue);
            seekBar.setProgress(currentValue);
            previousValue = currentValue;
        }
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

    /**
     * Implementing OnClick Listeners
     */

    private View.OnClickListener click_closePlayer = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {

            //Translate the activity down
            finish();
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

    public View.OnClickListener play_loop_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Constants.PLAY_INLOOP == false){
                Constants.PLAY_INLOOP = true;
                Constants.PLAY_SHUFFLE = false;
                play_loop.setImageResource(R.drawable.amber_loop_icon);
                play_shuffle.setImageResource(R.drawable.ic_shuffle_white_36dp);
            }else {
                Constants.PLAY_INLOOP = false;
                play_loop.setImageResource(R.drawable.ic_loop_white_48dp);
            }
        }
    };

    public View.OnClickListener play_shuffle_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Constants.PLAY_SHUFFLE == false){
                Constants.PLAY_SHUFFLE = true;
                Constants.PLAY_INLOOP = false;
                play_shuffle.setImageResource(R.drawable.amber_shuffle_icon);
                play_loop.setImageResource(R.drawable.ic_loop_white_48dp);
            }else {
                Constants.PLAY_SHUFFLE = false;
                play_shuffle.setImageResource(R.drawable.ic_shuffle_white_36dp);
            }
        }
    };
}
