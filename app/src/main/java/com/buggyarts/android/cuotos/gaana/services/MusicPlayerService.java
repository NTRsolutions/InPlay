package com.buggyarts.android.cuotos.gaana.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;


import com.buggyarts.android.cuotos.gaana.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.events.EventPlaybackStatus;
import com.buggyarts.android.cuotos.gaana.events.NowPlayingTrackInfo;
import com.buggyarts.android.cuotos.gaana.events.OnStartService;
import com.buggyarts.android.cuotos.gaana.events.PlayTrackOfIndex;
import com.buggyarts.android.cuotos.gaana.events.PreviousPlayPauseNext;
import com.buggyarts.android.cuotos.gaana.events.SeekBarChange;
import com.buggyarts.android.cuotos.gaana.events.UpdateInfo;
import com.buggyarts.android.cuotos.gaana.events.UpdatePlayingQueue;
import com.buggyarts.android.cuotos.gaana.events.UpdateProgress;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.audioEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioDBManager;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;


/**
 * Created by mayank on 12/5/17
 */

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener,
        AudioManager.OnAudioFocusChangeListener ,Runnable{

    public MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioDBManager audioDBManager;
    private int resumePosition = 0;
    private Thread playerThread;
    public ArrayList<String> tracks;
    public ArrayList<Audio> songs;
    public int trackIndex;
    public int updateQueue, isPlayingFrom;
    public boolean shouldPlay = true;

    //Incoming Calls Global Methods

    public boolean ongoingCall = false;
    public PhoneStateListener phoneStateListener;
    public TelephonyManager telephonyManager;

    /**
     * Media Sessions Playback Control
     */
    public static final String ACTION_PLAY = "com.buggyarts.android.inplay.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.buggyarts.android.inplay.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.buggyarts.android.inplay.ACTION_STOP";
    public static final String ACTION_NEXT = "com.buggyarts.android.inplay.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.buggyarts.android.inplay.ACTION_PREVIOUS";

    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;

    public enum PlaybackStatus{
        PLAYING,PAUSED
    }
//    public class MusicPlayerBinder extends Binder {
//        public MusicPlayerService getService() {
//            return MusicPlayerService.this;
//        }
//    }
//
//    public IBinder mBinder = new MusicPlayerBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new OnStartService(true));
        Log.v("SERVICE","Started");

//        if (mediaSessionManager == null) {
//            initMediaSession();
//            buildNotification(PlaybackStatus.PAUSED);
//        }

        //Handle Intent action from MediaSession.TransportControls
//        handleIncomingActions(intent);

//        trackIndex = intent.getExtras().getInt(Constants.INDEX);
//        updateQueue = intent.getExtras().getInt(Constants.UPDATE_QUEUE);
//        tracks = intent.getExtras().getStringArrayList(Constants.TRACKS);
//        createQueue();
//        clearMedia();
//        initMediaPlayer();

        return super.onStartCommand(intent, flags, startId);
    }

    public void fillPlaylistWithTracks(String track_title){
        audioDBManager = new AudioDBManager(this);
        SQLiteDatabase db = audioDBManager.getReadableDatabase();

        String[] columns = {"data","title","artist","album_id"};
        String[] args = {track_title};

        Cursor cursor = db.query(audioEntry.TABLE_NAME,columns,audioEntry.COLUMN_TITLE + "=?",args,null,null,null);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_DATA));
                String title = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ARTIST));
                String album_id = cursor.getString(cursor.getColumnIndex(audioEntry.COLUMN_ALBUM_ID));
//                Log.v("TRACK ADDED WITH TITLE",title);
                songs.add(new Audio(data,title,artist,album_id));
            }
        }cursor.close();
    }

    private void createQueue() {
        if (updateQueue == Constants.CLEAR_N_ADD_IN_QUEUE) {
            if(songs!=null){
                songs.clear();
            }
        }
        trackIndex = trackIndex + songs.size();
        int i = 0;
        while(i < tracks.size()){
            fillPlaylistWithTracks(tracks.get(i));
            i++;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize playing queue array
        tracks = new ArrayList<>();
        songs = new ArrayList<>();

        //Get audio Focus
        requestAudioFocus();

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
//        callStateListener();

        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        registerMediaActionButton();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Release media player
        clearMedia();

        //Remove audio focus
        removeAudioFocus();

        //Unregister Broadcast Receivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(mediaActionButtonClick);
        //Remove notification
//        removeNotification();

        //Disable the PhoneStateListener
        if(phoneStateListener != null){
            telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_NONE);
        }

        //Unregister EventBus
        EventBus.getDefault().unregister(this);
    }

    //Handle All Implemented Methods Here, That is..
    // -- OnPreparedListener
    // -- OnErrorListener
    // -- OnInfoListener
    // -- OnCompletionListener
    // -- OnSeekCompleteListener
    // -- OnAudioFocusChangeListener

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if(shouldPlay){
            playMedia();
            EventBus.getDefault().post(new EventPlaybackStatus(Constants.STATUS_PLAYING));
            Constants.isTrackPlaying = true;
        }
        playerThread = new Thread(this);
        playerThread.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {

        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //Invoked when playback of a media source has completed.
        if(Constants.PLAY_SHUFFLE){
            int max = songs.size()-1;
            int min = 0;
            trackIndex = min + (int) (Math.random()* max);

        }else if(Constants.PLAY_INLOOP){
            if(trackIndex == 0){
                trackIndex = songs.size()-1;
            } else {
                trackIndex = trackIndex - 1;
            }
        }
        playNextMedia();
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onAudioFocusChange(int focusState) {

        //Invoked when the audio focus of the system is updated
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    mediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                stopMedia();
                mediaPlayer.release();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a small amount of time: stop playback and release media player
                pauseMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) {mediaPlayer.setVolume(0.1f, 0.1f);}
                break;
        }
    }


    //EventBus  Event calls

    @Subscribe
    public void onUpdateQueue(UpdatePlayingQueue updatePlayingQueue){
        shouldPlay = true;
        updateQueue = updatePlayingQueue.getUpdateType();
        trackIndex = updatePlayingQueue.getIndex();

        if(updateQueue == Constants.ADD_IN_QUEUE){
            //add tracks in list
            ArrayList<Audio> list_to_add;
            list_to_add = updatePlayingQueue.getTrack_list();
            int i = 0;
            while(i < list_to_add.size()){
                songs.add(list_to_add.get(i));
            }
            trackIndex = trackIndex + songs.size();
        } else if(updateQueue == Constants.CLEAR_N_ADD_IN_QUEUE){
            songs = updatePlayingQueue.getTrack_list();
            trackIndex = updatePlayingQueue.getIndex();
//            Log.v("UPDATE QUEUE",trackIndex+" : "+songs.size());
        }

        isPlayingFrom = updatePlayingQueue.getIsPlayingFrom();
        switch(isPlayingFrom){
            case Constants.playingFromSingles: break;
            case Constants.playingFromPlaylist: Constants.isPlaylistPlaying = true; break;
            case Constants.playingFromAlbum: break;
            case Constants.playingFromArtists: break;
            case Constants.playingFromBoot: shouldPlay = false; break;
        }

        clearMedia();
        initMediaPlayer();
    }

    @Subscribe
    public void onEvent(PlayTrackOfIndex track){
        shouldPlay = true;
        trackIndex = track.getIndex();
        clearMedia();
        initMediaPlayer();
    }

    @Subscribe
    public void onPreviousPlayPauseNext(PreviousPlayPauseNext option){
        shouldPlay = true;
        switch(option.getControlCondition()){
            case Constants.PREVIOUS:
                playPreviousMedia();
                break;
            case Constants.PLAY:
                resumeMedia();
                break;
            case Constants.PAUSE:
                pauseMedia();
                break;
            case Constants.NEXT:
                playNextMedia();
                break;
        }
    }

    @Subscribe
    public void onUpdateInfo(UpdateInfo info){
        EventBus.getDefault().post(new NowPlayingTrackInfo(songs.get(trackIndex),trackIndex));
    }

    @Subscribe
    public void onSeekBarChange(SeekBarChange seekBarChange){
        mediaPlayer.seekTo(seekBarChange.getPosition());
    }

    //Initialize MediaPlayer and It's Controls Here

    public void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);

        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        setTrack(songs.get(trackIndex).data);

        EventBus.getDefault().post(new NowPlayingTrackInfo(songs.get(trackIndex),trackIndex));
        mediaPlayer.prepareAsync();

    }

    private void setTrack(String path){
        Log.v("path: ", path);
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
        Constants.isTrackPlaying = false;
        EventBus.getDefault().post(new EventPlaybackStatus(Constants.STATUS_PAUSE));
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            Constants.isTrackPlaying = true;
            EventBus.getDefault().post(new EventPlaybackStatus(Constants.STATUS_PLAYING));
        }
    }

    public void playNextMedia(){
        if(trackIndex == songs.size()-1){
            trackIndex = 0;
        }else {
            trackIndex = trackIndex + 1;
        }
        clearMedia();
        initMediaPlayer();
    }

    public void playPreviousMedia(){
        if(trackIndex == 0){
            trackIndex = songs.size()-1;
        } else {
            trackIndex = trackIndex - 1;
        }
        clearMedia();
        initMediaPlayer();
    }

    public void clearMedia() {
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
    }


    //Handle Audio Focus Here

    public boolean requestAudioFocus() {

        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(MusicPlayerService.this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    public boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    //Implementing seekBar

    @Override
    public void run() {
        int currentPosition = 0;
        int duration = mediaPlayer.getDuration();
//        Log.v("Values","DURATION: " + duration);
//        Map the value to a standard range
//        slope = (output_end - output_start) / (input_end - input_start)
//        output = output_start + slope * (input - input_start)

//        final int max = 10000;
//        int slope = (max)/(duration);
//        Log.v("Values","SLOPE: "+slope+ " DURATION: " + duration);

        while(mediaPlayer != null && currentPosition<duration){
            try {

                Thread.sleep(500);
                currentPosition = mediaPlayer.getCurrentPosition();
//                int currentValue = slope * (currentPosition);
//                Log.v("Progress",""+currentPosition);
                EventBus.getDefault().post(new UpdateProgress(duration,currentPosition));

            }catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }catch (Exception OtherException){
                return;
            }
        }
    }

    /**
     * Handle incoming phone calls here
     */

//    public void callStateListener(){
//        //Get telephone manager
//        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//
//        phoneStateListener = new PhoneStateListener(){
//
//            @Override
//            public void onCallStateChanged(int state, String incomingNumber) {
//                super.onCallStateChanged(state, incomingNumber);
//
//                switch(state){
//
//                    //if at least one call exists or the phone is ringing
//                    //pause the MediaPlayer
//
//                    case TelephonyManager.CALL_STATE_OFFHOOK:
//                    case TelephonyManager.CALL_STATE_RINGING:
//                        if(mediaPlayer!=null){
//                            pauseMedia();
//                            ongoingCall = true;
//                        }
//                        break;
//                    case TelephonyManager.CALL_STATE_IDLE:
//                        if(mediaPlayer!=null){
//                            if(ongoingCall){
//                                ongoingCall = false;
//                                if(Constants.isTrackPlaying){
//                                    resumeMedia();
//                                }
//                            }
//                        }
//                        break;
//                }
//            }
//        };
//
//        // Register the listener with the telephony manager
//        // Listen for changes to the device call state.
//        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
//    }


    /**
     * Implementing BroadReceiver to read device state changes like
     *  - AUDIO_BECOMING_NOISY
     */

    // Whenever the user removes the headphones pause media to be played on loud speaker
    BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // pause Audio on AUDIO_BECOMING_NOISY
            pauseMedia();
        }
    };

    private void registerBecomingNoisyReceiver(){
        IntentFilter noisyReceiver = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver,noisyReceiver);
    }

    BroadcastReceiver mediaActionButtonClick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())){

                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (event == null) {
                    return;
                }

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(mediaPlayer!=null){
                        if(Constants.isTrackPlaying == true){
                            pauseMedia();
                        }else {
                            resumeMedia();
                        }
                    }
                }
            }

        }
    };

    private void registerMediaActionButton(){
        IntentFilter mediaButton = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        registerReceiver(mediaActionButtonClick,mediaButton);
    }

    /**
     * Handle Media sessions Here
     */

    private void initMediaSession(){

        // if mediaSessionManager exists exit this method
        if(mediaSessionManager != null) return;

        mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);

        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(),"AudioPlayer");

        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();

        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);

        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //set media sessions metadata
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                playNextMedia();
                updateMetaData();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                playPreviousMedia();
                updateMetaData();
            }

            @Override
            public void onStop() {
                super.onStop();

                //remove notification
                //stop service
            }

        });

    }

    public void updateMetaData(){

        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,getBitmap(songs.get(trackIndex).data))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,songs.get(trackIndex).title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,songs.get(trackIndex).artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,songs.get(trackIndex).album)
                .build());
    }

    public static Bitmap getBitmap(String audioData){


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioData);
        Bitmap album_art;
        try {
            byte[] art = retriever.getEmbeddedPicture();
            album_art = BitmapFactory.decodeByteArray(art,0,art.length);
        }catch (Exception e){
            album_art = null;
        }
        return album_art;
    }

    /**
     * Notification Builder
     */

    private void buildNotification(PlaybackStatus playbackStatus){

        int notificationAction = R.drawable.ic_pause_circle_outline_white_48dp;
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if(playbackStatus == PlaybackStatus.PLAYING){
            notificationAction = R.drawable.ic_pause_circle_outline_white_48dp;
            play_pauseAction = playbackAction(1);
        }else if(playbackStatus == PlaybackStatus.PAUSED){
            notificationAction = R.drawable.ic_play_circle_outline_white_48dp;
            play_pauseAction = playbackAction(0);
        }


        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                // Attach our MediaSession token
                .setMediaSession(mediaSession.getSessionToken())
                // Show our playback controls in the compact notification view.
                .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorPrimary))
                // Set the large and small icons
                .setLargeIcon(getBitmap(songs.get(trackIndex).data))
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentTitle(songs.get(trackIndex).title)
                .setContentInfo(songs.get(trackIndex).artist)
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next,"next",playbackAction(2));


        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }


    public PendingIntent playbackAction(int action){

        Intent playbackAction = new Intent(this,MusicPlayerService.class);

        switch(action){
            case 0:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this,action,playbackAction,0);
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this,action,playbackAction,0);
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this,action,playbackAction,0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this,action,playbackAction,0);
        }

        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
            EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PLAY));
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
            EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PAUSE));
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
            EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.NEXT));
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
            EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PREVIOUS));
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }


}
