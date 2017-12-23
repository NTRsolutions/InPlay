package com.buggyarts.android.cuotos.gaana.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.buggyarts.android.cuotos.gaana.R;

/**
 * Created by mayank on 12/18/17
 */

public class PermissionsUtil {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PermissionsUtil(Context context){

        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preferences),context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void updatePermissions(String permission){

        switch(permission){
            case "read storage" :
                editor.putBoolean(context.getString(R.string.permission_read_storage),true);
                editor.commit();
                break;
            case "write storage" :
                editor.putBoolean(context.getString(R.string.permission_write_storage),true);
                editor.commit();
                break;
            case "phone state" :
                editor.putBoolean(context.getString(R.string.permission_phone_state),true);
                editor.commit();
        }
    }

    public boolean checkPermissionsPreferences(String permission){
        boolean isShown = false;
        switch (permission){
            case "read storage":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_read_storage),false);
                break;
            case "write storage":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_write_storage),false);
                break;
            case "phone state":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_phone_state),false);
                break;
        }
        return isShown;
    }

}
