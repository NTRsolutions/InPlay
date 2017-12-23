package com.buggyarts.android.cuotos.gaana;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.buggyarts.android.cuotos.gaana.adapters.ArtistPagerAdapter;
import com.buggyarts.android.cuotos.gaana.events.NowPlayingTrackInfo;
import com.buggyarts.android.cuotos.gaana.events.EventPlaybackStatus;
import com.buggyarts.android.cuotos.gaana.events.OnTrackOptionsClick;
import com.buggyarts.android.cuotos.gaana.events.PreviousPlayPauseNext;
import com.buggyarts.android.cuotos.gaana.events.UpdateInfo;
import com.buggyarts.android.cuotos.gaana.fragments.Player_Fragment;
import com.buggyarts.android.cuotos.gaana.fragments.TrackOptionsFragment;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;

public class ArtistActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    TabLayout tabLayout;

    Player_Fragment player_fragment;
    FragmentManager fragmentManager;
    TrackOptionsFragment trackOptionsFragment;
    ViewPager viewPager;
    ArtistPagerAdapter artistPagerAdapter;

    RelativeLayout open_player_view;

    ImageView player_thumbnail, player_thumbnail_background, play_next,play_pause,play_previous;
    TextView player_title, player_artist;

    String artist_name;
    private Audio nowPlayingTrack;
    private int nowPlayingIndex;

    /**
     * Handle Activity Life Cycle Here
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Intent intent = getIntent();
        artist_name = intent.getStringExtra("artist");

        play_pause = (ImageView) findViewById(R.id.now_playing_play_pause);
        play_pause.setOnClickListener(play_pause_listener);
        play_previous = (ImageView) findViewById(R.id.now_playing_previous);
        play_previous.setOnClickListener(play_previous_listener);
        play_next = (ImageView) findViewById(R.id.now_playing_next);
        play_next.setOnClickListener(play_next_listener);

        appBarSetup();
        viewPagerFragmentsSetup();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().post(new UpdateInfo());
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().post(new NowPlayingTrackInfo(nowPlayingTrack,nowPlayingIndex));
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Handle Activity Methods Here
     */

    private void appBarSetup(){
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.artist_collapsing_ToolBar);
        collapsingToolbarLayout.setTitle(artist_name);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.viewBg));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.viewBg));

        toolbar = (Toolbar) findViewById(R.id.artist_tool_bar);
        toolbar.setTitle("ToolBar");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white, null));
        }

        viewPager = (ViewPager) findViewById(R.id.artist_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.artist_tab_layout);
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void viewPagerFragmentsSetup(){


        player_fragment = new Player_Fragment();
        fragmentManager = getSupportFragmentManager();


        player_thumbnail = (ImageView) findViewById(R.id.now_playing_art);
        player_thumbnail_background = (ImageView) findViewById(R.id.now_playing_art_background);
        player_title = (TextView) findViewById(R.id.now_playing_title);
        player_artist = (TextView) findViewById(R.id.now_playing_artist);

        open_player_view = (RelativeLayout) findViewById(R.id.open_player);
        open_player_view.setOnClickListener(click_openPlayer);

        artistPagerAdapter = new ArtistPagerAdapter(fragmentManager,this,artist_name);
        viewPager.setAdapter(artistPagerAdapter);

    }

    /**
     * Handle EventBus onEvent Method Calls
     */

    @Subscribe
    public void updateInfo(NowPlayingTrackInfo track){
        nowPlayingTrack = track.getTrack();
        nowPlayingIndex = track.getIndex();
        Glide.with(this).load(MediaRetriever.getAlbumArt(nowPlayingTrack.album_id)).asBitmap().into(player_thumbnail);
        Glide.with(this).load(MediaRetriever.getAlbumArt(nowPlayingTrack.album_id)).asBitmap().centerCrop().into(player_thumbnail_background);
        player_title.setText(nowPlayingTrack.title);
        player_artist.setText(nowPlayingTrack.artist);
    }

    @Subscribe
    public void onTrackOptionsOpen(OnTrackOptionsClick optionsClick){

        Audio audio = optionsClick.getAudio();
        int trackIndex = optionsClick.getTrackIndex();
        Bundle bundle = new Bundle();
        bundle.putParcelable("audio",audio);
        bundle.putInt("index",trackIndex);

        trackOptionsFragment = new TrackOptionsFragment();
        trackOptionsFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        fragmentTransaction.add(R.id.options_fragment,trackOptionsFragment,"trackOptions");
        fragmentTransaction.commit();

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

    @Subscribe
    public void onPlaybackStatusChange(EventPlaybackStatus status){
        switch(status.getStatus()){
            case Constants.STATUS_PLAYING:
                play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                break;
            case Constants.STATUS_PAUSE:
                play_pause.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                break;
        }
    }

    /**
     * Handle Click Listeners Here
     */

    private View.OnClickListener click_openPlayer = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ArtistActivity.this,PlayerUI.class);
            startActivity(intent, ActivityOptions.makeCustomAnimation(getBaseContext(),R.anim.slide_in,R.anim.slide_out).toBundle());
//            FragmentManager manager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = manager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
//            fragmentTransaction.show(player_fragment);
//            fragmentTransaction.commit();
        }
    };

    public View.OnClickListener play_pause_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Constants.isTrackPlaying){
                EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PAUSE));
                Constants.isTrackPlaying = false;
            }else {
                EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PLAY));
                Constants.isTrackPlaying = true;
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

}
