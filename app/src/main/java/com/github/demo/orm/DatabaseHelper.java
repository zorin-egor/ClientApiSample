package com.github.demo.orm;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.github.demo.data.User;
import com.github.demo.App;

import java.sql.SQLException;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "GithubTest";
    private static final int DATABASE_VERSION = 1;
    private RuntimeExceptionDao<User, Integer> mUserRuntimeExceptionDao = null;
    private Dao<User, Integer> mUserDao = null;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try {
            TableUtils.createTable(connectionSource, User.class);
        } catch (SQLException e){
            Log.d(App.TAG, "Error creating DB: " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer){
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e){
            Log.e(App.TAG, "Error upgrading DB: " + DATABASE_NAME + " from ver. " + oldVer);
            throw new RuntimeException(e);
        }
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        if (mUserDao == null) {
            mUserDao = getDao(User.class);
        }

        return mUserDao;
    }

    public RuntimeExceptionDao<User, Integer> getUserRuntimeExceptionDao() throws SQLException {
        if (mUserRuntimeExceptionDao == null) {
            mUserRuntimeExceptionDao = getRuntimeExceptionDao(User.class);
        }

        return mUserRuntimeExceptionDao;
    }

    @Override
    public void close(){
        super.close();
        mUserDao = null;
        mUserRuntimeExceptionDao = null;
    }

    public void saveData(@NonNull List<User> data){
        if(data != null){
            try {
                for(User user : data) {
                    getUserDao().createOrUpdate(user);
                }
            } catch (SQLException e) {
                Log.d(App.TAG, "Can't add data to DB");
                throw new RuntimeException(e);
            }
        }
    }

    @Nullable
    public List<User> loadData(){
        List<User> userList = null;
        try {
            userList = getUserDao().queryForAll();
        } catch (SQLException e) {
            Log.d(App.TAG, "Can't add data to DB");
        }

        return userList;
    }
}