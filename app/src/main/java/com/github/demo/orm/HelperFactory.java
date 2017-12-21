package com.github.demo.orm;

import android.content.Context;
import android.support.annotation.NonNull;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class HelperFactory {

    private static DatabaseHelper sDatabaseHelper = null;

    public static DatabaseHelper getHelper(){
        return sDatabaseHelper;
    }
    public static void setHelper(@NonNull Context context){
        sDatabaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        sDatabaseHelper = null;
    }

}