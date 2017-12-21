package com.github.demo;

import android.app.Application;

import com.squareup.picasso.Picasso;
import com.github.demo.orm.HelperFactory;
import com.github.demo.rest.Api;
import com.github.demo.rest.RetrofitTool;


public class App extends Application {

    public static final String TAG = App.class.getSimpleName();

    /*
    * App instance
    * */
    private static App sApplication = null;

    /*
    * Instance for request
    * */
    private RetrofitTool mUtils = null;


    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        HelperFactory.releaseHelper();
    }

    /*
    * Load previous data from SqLite
    * */
    private void init(){
        // Db
        HelperFactory.setHelper(getApplicationContext());
        // Rest
        mUtils = new RetrofitTool(Api.MAIN_URL);
        // Enable debug for picasso
        if(BuildConfig.DEBUG){
            Picasso.with(this).setIndicatorsEnabled(true);
        }
    }

    public static App getInstance(){
        return sApplication;
    }

    public RetrofitTool getUtils() {
        return mUtils;
    }

}
