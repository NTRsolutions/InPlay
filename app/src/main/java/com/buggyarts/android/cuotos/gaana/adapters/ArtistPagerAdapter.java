package com.buggyarts.android.cuotos.gaana.adapters;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.buggyarts.android.cuotos.gaana.R;

import com.buggyarts.android.cuotos.gaana.fragments.ArtistAlbumsFragment;
import com.buggyarts.android.cuotos.gaana.fragments.ArtistTracksFragment;

/**
 * Created by mayank on 12/8/17
 */

public class ArtistPagerAdapter extends FragmentPagerAdapter {

    private int pageCount = 2;
    private Context context;
    public Bundle bundle;
    private String artist_name;

    public ArtistPagerAdapter(FragmentManager fm, Context context,String artist_name) {
        super(fm);
        this.context = context;
        this.artist_name = artist_name;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        bundle = new Bundle();
        bundle.putString("artist_name",artist_name);
        switch(position){
            case 0 :
                fragment = new ArtistTracksFragment();
                fragment.setArguments(bundle);
                break;
            case 1 :
                fragment = new ArtistAlbumsFragment();
                fragment.setArguments(bundle);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch(position){
            case 0: title = context.getResources().getString(R.string.songs);
                break;
            case 1: title = context.getResources().getString(R.string.albums);
                break;
        }
        return title;
    }
}
