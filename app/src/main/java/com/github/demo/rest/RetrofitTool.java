package com.github.demo.rest;

import android.util.Log;

import com.github.demo.orm.HelperFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.demo.data.User;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitTool {

    public static final String TAG = Retrofit.class.getSimpleName();

    public interface Callbacks {
        int ERROR_FATAL = -1;
        void onSuccess(final List<User> userList);
        void onError(final String error, final int code);
    }

    private static final int READ_TIMEOUT = 5;
    private static final int CONNECTION_TIMEOUT = 5;

    private final OkHttpClient mOkHttpClient;
    private final Retrofit mRetrofit;
    private final Gson mGson;
    private final String mUrl;
    private final Api mApi;
    private final Callback mCallback;
    private boolean isRequesting;

    private WeakReference<Callbacks> mWeakReference = null;

    public RetrofitTool(String url){
        mUrl = url;
        isRequesting = false;

        mGson = new GsonBuilder()
                .setLenient()
                .create();
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MINUTES)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mUrl)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();
        mApi = mRetrofit.create(Api.class);
        mCallback = createCallback();
    }

    private Callback createCallback(){
        Callback callback = new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d(TAG, " - onResponse()");
                isRequesting = false;
                if (response.code() >= 200 && response.code() < 300) {
                    // Add new to common list
                    final List<User> userList = response.body();
                    if(userList != null) {
                        // For data restore
                        HelperFactory.getHelper().saveData(userList);
                        // For views update
                        final Callbacks callbacks = mWeakReference.get();
                        if (callbacks != null) {
                            callbacks.onSuccess(userList);
                        }
                    }
                } else {
                    errorCall(response.message(), response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, " - onFailure()");
                isRequesting = false;

                // For views update
                errorCall(t.getMessage(), Callbacks.ERROR_FATAL);
            }
        };

        return callback;
    }

    private void errorCall(final String error, final int code) {
        final Callbacks callbacks = mWeakReference.get();
        if (callbacks != null) {
            callbacks.onError(error, code);
        }
    }

    public void getUsers(){
        isRequesting = true;
        mApi.requestUsers().enqueue(mCallback);
    }

    public void getUsersById(int id){
        isRequesting = true;
        mApi.requestUsersById(id).enqueue(mCallback);
    }

    public Api getApi() {
        return mApi;
    }

    public Callback getCallback() {
        return mCallback;
    }

    public void setCallback(final Callbacks callback) {
        mWeakReference = new WeakReference<>(callback);
    }

    public boolean isRequesting() {
        return isRequesting;
    }

}
