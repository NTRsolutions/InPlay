package com.buggyarts.android.cuotos.gaana;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.events.NowPlayingTrackInfo;
import com.buggyarts.android.cuotos.gaana.events.OnStartService;
import com.buggyarts.android.cuotos.gaana.events.EventPlaybackStatus;
import com.buggyarts.android.cuotos.gaana.events.OnTrackOptionsClick;
import com.buggyarts.android.cuotos.gaana.events.PreviousPlayPauseNext;
import com.buggyarts.android.cuotos.gaana.events.UpdateInfo;
import com.buggyarts.android.cuotos.gaana.events.UpdatePlayingQueue;
import com.buggyarts.android.cuotos.gaana.fragments.TrackOptionsFragment;
import com.buggyarts.android.cuotos.gaana.services.MusicPlayerService;
import com.buggyarts.android.cuotos.gaana.adapters.MyPagerAdapter;
import com.buggyarts.android.cuotos.gaana.fragments.Player_Fragment;
import com.buggyarts.android.cuotos.gaana.fragments.Songs_Fragment;
import com.buggyarts.android.cuotos.gaana.sqliteDB.AudioContract.keysEntry;
import com.buggyarts.android.cuotos.gaana.sqliteDB.KeysnValuesDbManager;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.utils.Constants;
import com.buggyarts.android.cuotos.gaana.utils.MediaRetriever;
import com.buggyarts.android.cuotos.gaana.utils.PermissionsUtil;

public class MainActivity extends AppCompatActivity implements Songs_Fragment.PlayingQueue {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyPagerAdapter myPagerAdapter;
    private FragmentManager fragmentManager;
    private MediaRetriever retriever;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private KeysnValuesDbManager keysnValuesDbManager;

    private PermissionsUtil permissionsUtil;

    private Player_Fragment player_fragment;
    private TrackOptionsFragment trackOptionsFragment;
    private RelativeLayout open_player_view;

    private ImageView player_thumbnail, player_thumbnail_background, play_next,play_pause,play_previous;
    private TextView player_title, player_artist;

    private MusicPlayerService musicPlayerService;
    private ServiceConnection serviceConnection;
    private Intent musicServiceIntent;

    private boolean serviceBound;
    public Audio nowPlayingTrack;
    public ArrayList<Audio> playingQueue;
    public int nowPlayingIndex = 0;

    /**
     * Handle Activity LifeCycle Here
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Constants.DISPLAY_WIDTH = dm.widthPixels;
        Constants.DISPLAY_HEIGHT = dm.heightPixels;

//        permissionsUtil = new PermissionsUtil(this);
//        getPermissions();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUEST_READ_EXTERNAL_STORAGE);

        } else {
            initViews();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().post(new UpdateInfo());
//        Log.v("MAIN ACTIVITY","RESUMED");
        super.onResume();
    }

    @Override
    protected void onPause() {
//        Log.v("MAIN ACTIVITY","PAUSED");
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

//        retriever.storeQueueValues(playingQueue,nowPlayingIndex);
        storeQueueValues(nowPlayingIndex);
        stopService(musicServiceIntent);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Constants.REQUEST_READ_EXTERNAL_STORAGE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                otherPermissions();
                initViews();
            } else {

                otherPermissions();

                Toast.makeText(this,"Please allow Storage permissions in your app Settings",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",this.getPackageName(),null);
                intent.setData(uri);
                this.startActivity(intent);
            }

        }else if(requestCode == Constants.REQUEST_PHONE_STATE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){}
            else {}
        }
    }

    private void otherPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    Constants.REQUEST_PHONE_STATE);
        }
    }

    private void getPermissions(){


        if(checkPermission(Constants.TXT_READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                showPermissionExplanation(Constants.TXT_READ_EXTERNAL_STORAGE);
            }
            else if(!permissionsUtil.checkPermissionsPreferences("read storage")){

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Constants.REQUEST_READ_EXTERNAL_STORAGE);

                permissionsUtil.updatePermissions("read storage");
            }
            else {

                Toast.makeText(this,"Please allow Storage permissions in your app Settings",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",this.getPackageName(),null);
                intent.setData(uri);
                this.startActivity(intent);
            }
        }

        if(checkPermission(Constants.TXT_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)){
                showPermissionExplanation(Constants.TXT_PHONE_STATE);
            }else if(!permissionsUtil.checkPermissionsPreferences("phone state")){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},Constants.REQUEST_PHONE_STATE);
                permissionsUtil.updatePermissions("phone state");
            }else {
                Toast.makeText(this,"Please allow Storage permissions in your app Settings",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",this.getPackageName(),null);
                intent.setData(uri);
                this.startActivity(intent);
            }
        }
    }

    public int checkPermission(int permission){

        switch (permission){
            case Constants.TXT_READ_EXTERNAL_STORAGE :
                return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            case Constants.TXT_WRITE_EXTERNAL_STORAGE:
                return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            case Constants.TXT_PHONE_STATE:
                return ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE);

        }
        return -1;
    }

    public void requestPermission(int permission){

        switch (permission){
            case Constants.TXT_READ_EXTERNAL_STORAGE:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Constants.REQUEST_READ_EXTERNAL_STORAGE);
                break;
            case Constants.TXT_WRITE_EXTERNAL_STORAGE:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
                break;
            case Constants.TXT_PHONE_STATE:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},Constants.REQUEST_PHONE_STATE);
                break;
        }
    }

    public void showPermissionExplanation(final int permission){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(permission == Constants.TXT_READ_EXTERNAL_STORAGE){
            builder.setMessage("This app needs to READ your device Storage.. Please allow");
            builder.setTitle(R.string.permission_read_storage);

        }else if(permission == Constants.TXT_WRITE_EXTERNAL_STORAGE){
            builder.setMessage("This app needs to WRITE your device Storage.. Please allow");
            builder.setTitle(R.string.permission_write_storage);

        } else if(permission == Constants.TXT_PHONE_STATE){
            builder.setMessage("This app needs to access your device Phone State.. Please allow");
            builder.setTitle(R.string.permission_phone_state);

        }

        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(permission == Constants.TXT_READ_EXTERNAL_STORAGE){
                    requestPermission(Constants.TXT_READ_EXTERNAL_STORAGE);

                }else if(permission == Constants.TXT_WRITE_EXTERNAL_STORAGE){
                    requestPermission(Constants.TXT_WRITE_EXTERNAL_STORAGE);

                }else if(permission == Constants.TXT_PHONE_STATE){
                    requestPermission(Constants.TXT_PHONE_STATE);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initViews(){

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(!Settings.System.canWrite(this)) {
//                startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS));
//            }
//        }

        keysnValuesDbManager = new KeysnValuesDbManager(this);

        play_pause = (ImageView) findViewById(R.id.now_playing_play_pause);
        play_pause.setOnClickListener(play_pause_listener);
        play_previous = (ImageView) findViewById(R.id.now_playing_previous);
        play_previous.setOnClickListener(play_previous_listener);
        play_next = (ImageView) findViewById(R.id.now_playing_next);
        play_next.setOnClickListener(play_next_listener);

        musicServiceIntent = new Intent(MainActivity.this,MusicPlayerService.class);
        startService(musicServiceIntent);

        add_keys();
        if (is_Audio_Table_Created()) {
            retriever = new MediaRetriever(this,0);
        }
        else {
            retriever = new MediaRetriever(this);
            playingQueue = retriever.recreateQueue();
            nowPlayingIndex = retriever.extractIndex();
            handleImplicitIntent();
//            nowPlayingTrack = playingQueue.get(nowPlayingIndex);
        }

        viewPagerFragmentsSetup();
        appBarSetup();
    }

    private void appBarSetup(){
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_ToolBar);
        collapsingToolbarLayout.setTitle("INPlay");
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.viewBg));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.viewBg));

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white, null));
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void viewPagerFragmentsSetup(){
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        fragmentManager = getSupportFragmentManager();

//        player_fragment = new Player_Fragment();
//        final FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.player_frame_layout, player_fragment, "playerFragment");
//        transaction.hide(player_fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
//        fragmentTransaction.add(R.id.options_fragment,trackOptionsFragment,"trackOptions");
//        fragmentTransaction.hide(trackOptionsFragment);
//        fragmentTransaction.commit();

        player_thumbnail = (ImageView) findViewById(R.id.now_playing_art);
        player_thumbnail_background = (ImageView) findViewById(R.id.now_playing_art_background);
        player_title = (TextView) findViewById(R.id.now_playing_title);
        player_artist = (TextView) findViewById(R.id.now_playing_artist);

        open_player_view = (RelativeLayout) findViewById(R.id.open_player);
//        open_player_view.setVisibility(View.GONE);
        open_player_view.setOnClickListener(click_openPlayer);

        myPagerAdapter = new MyPagerAdapter(fragmentManager, this);
        viewPager.setAdapter(myPagerAdapter);
    }

    private void add_keys() {
        SQLiteDatabase keysDb = keysnValuesDbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(keysEntry.COLUMN_KEY, "is_audioDB_created");
        contentValues.put(keysEntry.COLUMN_BOOL_VALUE, 0);
        keysDb.insert(keysEntry.TABLE_NAME, null, contentValues);
    }

    private boolean is_Audio_Table_Created() {
        boolean create_db = true;
        int value;
        SQLiteDatabase keysDB = keysnValuesDbManager.getReadableDatabase();
        String[] columns = {keysEntry.COLUMN_KEY, keysEntry.COLUMN_BOOL_VALUE};
        String[] args = {"is_audioDB_created"};
        Cursor cursor = keysDB.query(keysEntry.TABLE_NAME, columns, "key = ?", args, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String key = cursor.getString(cursor.getColumnIndex(keysEntry.COLUMN_KEY));
                value = cursor.getInt(cursor.getColumnIndex(keysEntry.COLUMN_BOOL_VALUE));
                Log.v("Value", "" + value);

                if (value == 0) {
                    create_db = true;
                } else {
                    create_db = false;
                }
            }
        }
        cursor.close();
        Log.v("RESULT", "" + create_db);
        return create_db;
    }

    private void handleImplicitIntent(){

        Intent intent = getIntent();

        if(intent != null){
            String receivedAction = intent.getAction();
            if(receivedAction.equals(Intent.ACTION_VIEW)){
                {
                    Uri data = intent.getData();
                    if(data != null) {
//                        String intent_media_path = data.getPath();
//                        Log.v("IMPLICIT INTENT",intent_media_path);
                        MediaRetriever retriever = new MediaRetriever(this);
                        playingQueue.add(nowPlayingIndex,retriever.makeAudioObject(data));
                        EventBus.getDefault().post(new UpdatePlayingQueue(playingQueue,nowPlayingIndex,Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromSingles));
                        EventBus.getDefault().post(new PreviousPlayPauseNext(Constants.PLAY));
                    }
                }
            }
        }
    }

    /**
     * EventBus onEvent Method Calls
     */

    @Subscribe
    public void updateInfo(NowPlayingTrackInfo track){
        nowPlayingTrack = track.getTrack();
        Glide.with(this).load(MediaRetriever.getAlbumArt(nowPlayingTrack.album_id)).asBitmap().into(player_thumbnail);
        Glide.with(this).load(MediaRetriever.getAlbumArt(nowPlayingTrack.album_id)).asBitmap().centerCrop().into(player_thumbnail_background);
        player_title.setText(nowPlayingTrack.title);
        player_artist.setText(nowPlayingTrack.artist);
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

    @Subscribe
    public void onUpdateQueue(UpdatePlayingQueue updatePlayingQueue){
        playingQueue = updatePlayingQueue.getTrack_list();
        nowPlayingIndex = updatePlayingQueue.getIndex();
    }

    @Subscribe
    public void onServiceStart(OnStartService onStartService){
        EventBus.getDefault().post(new UpdatePlayingQueue(playingQueue,nowPlayingIndex,Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromBoot));
    }

    @Subscribe
    public void onTrackOptionsOpen(OnTrackOptionsClick optionsClick){

        Audio audio = optionsClick.getAudio();
        int trackIndex = optionsClick.getTrackIndex();
        Bundle bundle = new Bundle();
        bundle.putParcelable("audio",audio);
        bundle.putInt("index",trackIndex);

        if(trackOptionsFragment != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(trackOptionsFragment);
            fragmentTransaction.commit();
        }

        trackOptionsFragment = new TrackOptionsFragment();
        trackOptionsFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragmentTransaction.add(R.id.options_fragment,trackOptionsFragment,"trackOptions");
        fragmentTransaction.commit();

    }

    /**
     * Handle CLick Events here
     */

    private View.OnClickListener click_openPlayer = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        Intent intent = new Intent(MainActivity.this,PlayerUI.class);
        startActivity(intent,ActivityOptions.makeCustomAnimation(getBaseContext(),R.anim.slide_in,R.anim.slide_out).toBundle());

//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = manager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
//        fragmentTransaction.show(player_fragment);
//        fragmentTransaction.commit();
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

    /**
     * Methods No Longer In Use
     */

    @Override
    public void queue(ArrayList<Audio> list, int index) {
        open_player_view.setVisibility(View.VISIBLE);
        startMusicService(list,index);
//        EventBus.getDefault().post(new PlayTrackOfIndex(index));

    }

    private void playMusic(ArrayList<String> tracks,int trackIndex,int updateQueue){
        musicServiceIntent = new Intent(MainActivity.this,MusicPlayerService.class);
        musicServiceIntent.putExtra(Constants.INDEX,trackIndex);
        musicServiceIntent.putExtra(Constants.UPDATE_QUEUE,updateQueue);
        musicServiceIntent.putStringArrayListExtra(Constants.TRACKS,tracks);

        startService(musicServiceIntent);
//        bindService();
    }

    public void startMusicService(ArrayList<Audio> list,int index){
        ArrayList<String> tracks = new ArrayList<>();
        if(!serviceBound){
            int i = 0;
            while(i < list.size()){
                tracks.add(list.get(i).title);
                i++;
            }
            playMusic(tracks,index,Constants.CLEAR_N_ADD_IN_QUEUE);
        }
    }

    public void bindService(){
        if(serviceConnection == null){
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//                    MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) iBinder;
                    //A Service class instance
//                    musicPlayerService = binder.getService();
                    serviceBound = true;
                }
                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    serviceBound = false;
                }
            };
            bindService(musicServiceIntent,serviceConnection,BIND_AUTO_CREATE);
        }
    }

    public void unbindService(){
        if(serviceBound){
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    private void storeQueueValues(int index){

        retriever = new MediaRetriever(this,playingQueue);

        keysnValuesDbManager = new KeysnValuesDbManager(this);
        SQLiteDatabase keysDB = keysnValuesDbManager.getWritableDatabase();
        ContentValues key_values = new ContentValues();
        key_values.put(keysEntry.COLUMN_INT_VALUE,index);
        String[] arg = {"queueIndex"};
        keysDB.update(keysEntry.TABLE_NAME,key_values,keysEntry.COLUMN_KEY + " = ?",arg);
        Log.v("Queue KEY",nowPlayingIndex + " saved");

    }

}
