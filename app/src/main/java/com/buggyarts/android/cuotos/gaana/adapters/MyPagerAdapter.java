package com.buggyarts.android.cuotos.gaana.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.buggyarts.android.cuotos.gaana.R;

import com.buggyarts.android.cuotos.gaana.fragments.Albums_Fragment;
import com.buggyarts.android.cuotos.gaana.fragments.Aritsts_Fragment;
import com.buggyarts.android.cuotos.gaana.fragments.PlayList_Fragment;
import com.buggyarts.android.cuotos.gaana.fragments.Songs_Fragment;


/**
 * Created by mayank on 11/15/17
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    private final static int PAGE_COUNT = 4;
    private String page_title = "";
    private Context context ;

    public MyPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0: fragment = new Songs_Fragment();
                break;
            case 1: fragment = new PlayList_Fragment();
                break;
            case 2: fragment = new Albums_Fragment();
                break;
            case 3: fragment = new Aritsts_Fragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                page_title = context.getResources().getString(R.string.songs);
                break;
            case 1:
                page_title = context.getResources().getString(R.string.playlists);
                break;
            case 2:
                page_title = context.getResources().getString(R.string.albums);
                break;
            case 3:
                page_title = context.getResources().getString(R.string.artists);
                break;
        }
        return page_title;
    }
}
