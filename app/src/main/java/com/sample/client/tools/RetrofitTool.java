package com.sample.client.tools;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sample.client.api.Api;
import com.sample.client.data.User;

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

    private static volatile RetrofitTool sRetrofitTool;

    public static RetrofitTool getInstance() {
        if (sRetrofitTool == null) {
            synchronized (RetrofitTool.class) {
                if (sRetrofitTool == null) {
                    sRetrofitTool = new RetrofitTool(Api.MAIN_URL);
                }
            }
        }
        return sRetrofitTool;
    }

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

    private WeakReference<Callbacks> mWeakReference;

    public RetrofitTool(String url) {
        mUrl = url;

        mGson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .setPrettyPrinting()
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
    }

    private Callback getCallback() {
        return new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d(TAG, " - onResponse()");
                if (response.code() >= 200 && response.code() < 300) {

                    // Add new to common list
                    final List<User> userList = response.body();
                    if(userList != null) {

                        // For data restore
                        AsyncTool.run(() -> {
                            OrmLiteTool.getInstance().saveData(userList);
                            return null;
                        });

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
                errorCall(t.getMessage(), Callbacks.ERROR_FATAL);
            }
        };
    }

    private void errorCall(final String error, final int code) {
        final Callbacks callbacks = mWeakReference.get();
        if (callbacks != null) {
            callbacks.onError(error, code);
        }
    }

    public void getUsers() {
        mApi.requestUsers().enqueue(getCallback());
    }

    public void getUsersById(String id) {
        mApi.requestUsersById(id).enqueue(getCallback());
    }

    public void setCallback(@Nullable final Callbacks callback) {
        mWeakReference = new WeakReference<>(callback);
    }

    public void removeCallback() {
        mWeakReference = null;
    }

    public void cancel() {
        mOkHttpClient.dispatcher().cancelAll();
    }

    public boolean isRequesting() {
        return mOkHttpClient.dispatcher().queuedCallsCount() > 0 ||
                mOkHttpClient.dispatcher().runningCallsCount() > 0;
    }

}
