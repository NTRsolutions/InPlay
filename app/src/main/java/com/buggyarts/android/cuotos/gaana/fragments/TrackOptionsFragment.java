package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.buggyarts.android.cuotos.gaana.R;

import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 12/13/17
 */

public class TrackOptionsFragment extends Fragment {

    ImageView closeFragment;
    RelativeLayout share,add_to_play_list,set_as_ringtone,rate;
    Context context;

    static Audio audio;
    int index;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View tracksOptionsListView = inflater.inflate(R.layout.track_options_fragment,container,false);

        closeFragment = tracksOptionsListView.findViewById(R.id.close_options);
        closeFragment.setOnClickListener(click_closeOptions);

        share = tracksOptionsListView.findViewById(R.id.option_1);
        add_to_play_list = tracksOptionsListView.findViewById(R.id.option_2);
        set_as_ringtone = tracksOptionsListView.findViewById(R.id.option_3);
        rate = tracksOptionsListView.findViewById(R.id.option_4);

        share.setOnClickListener(onShareOptionClick);
        add_to_play_list.setOnClickListener(onAddToPlaylistClick);
        set_as_ringtone.setOnClickListener(onSetAsRingToneClick);
        rate.setOnClickListener(onRateOptionClick);

        return tracksOptionsListView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        Bundle bundle = getArguments();
        audio = bundle.getParcelable("audio");
        index = bundle.getInt("index");
    }

    private View.OnClickListener click_closeOptions = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            FragmentManager manager = getFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            TrackOptionsFragment trackOptionsFragment = (TrackOptionsFragment) manager.findFragmentByTag("trackOptions");
            fragmentTransaction.hide(trackOptionsFragment);
            fragmentTransaction.commit();
        }
    };

    private View.OnClickListener onShareOptionClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.setData(Uri.parse(audio.data));
        share.putExtra(Intent.EXTRA_STREAM,Uri.parse(audio.data));
        share.setAction("android.intent.action.SEND");
        share.setType("audio/*");
        share.addCategory("android.intent.category.DEFAULT");
        Log.v("TRACK OP HAS",index+ " : " + audio.title);
        if(share.resolveActivity(getContext().getPackageManager())!=null){
            startActivity(share);
        }
        click_closeOptions.onClick(view);

        }
    };

    private View.OnClickListener onAddToPlaylistClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //create new dialog
            FragmentManager fm = getFragmentManager();
            AddInPlaylist addInPlaylist = new AddInPlaylist();
            Bundle bundle = new Bundle();
            bundle.putParcelable("audio",audio);
            addInPlaylist.setArguments(bundle);
            addInPlaylist.show(fm,"addInPlaylistFragment");

            click_closeOptions.onClick(view);
        }
    };

    private View.OnClickListener onSetAsRingToneClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!Settings.System.canWrite(context)) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS));
                }
            }
            else {
                RingtoneManager.setActualDefaultRingtoneUri(context,RingtoneManager.TYPE_RINGTONE,Uri.parse(audio.data));
                Toast.makeText(context,context.getResources().getString(R.string.set_ringtone_msg) ,Toast.LENGTH_SHORT).show();
            }
            click_closeOptions.onClick(view);
        }
    };

    private View.OnClickListener onRateOptionClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            click_closeOptions.onClick(view);
        }
    };


}
