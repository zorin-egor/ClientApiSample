package com.sample.client.tools;

import android.util.Log;

import androidx.annotation.NonNull;
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

    public interface OnRequestListener {
        void onSuccess(@NonNull final List<User> userList);
        void onError(@Nullable final String error);
    }

    private static final int READ_TIMEOUT = 5;
    private static final int CONNECTION_TIMEOUT = 5;

    private final OkHttpClient mOkHttpClient;
    private final Api mApi;

    private WeakReference<OnRequestListener> mWeakReference;

    private RetrofitTool(String url) {
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MINUTES)
                .build();

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .setPrettyPrinting()
                .create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mApi = mRetrofit.create(Api.class);
    }

    @NonNull
    private Callback<List<User>> getCallback() {
        return new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d(TAG, " - onResponse()");

                if (response.isSuccessful()) {
                    final List<User> userList = response.body();
                    if (userList != null) {
                        final WeakReference<OnRequestListener> weakReference = mWeakReference;
                        if (weakReference != null) {
                            final OnRequestListener listener = weakReference.get();
                            if (listener != null) {
                                listener.onSuccess(userList);
                            }
                        }
                    }
                } else {
                    onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, " - onFailure()");
                onError(t.getMessage());
            }
        };
    }

    private void onError(final String error) {
        final WeakReference<OnRequestListener> reference = mWeakReference;
        if (reference != null) {
            final OnRequestListener listener = reference.get();
            if (listener != null) {
                listener.onError(error);
            }
        }
    }

    public void getUsers() {
        mApi.requestUsers().enqueue(getCallback());
    }

    public void getUsersById(String id) {
        mApi.requestUsersById(id).enqueue(getCallback());
    }

    public void setListener(@Nullable final OnRequestListener listener) {
        mWeakReference = new WeakReference<>(listener);
    }

    public void removeListener() {
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
