package com.sample.client.tools;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncTool<Result> {

    @FunctionalInterface
    public interface OnBackground<T> {
        @Nullable T run() throws InterruptedException;
    }

    @FunctionalInterface
    public interface OnResult<T> {
        void run(T t);
    }

    public static <Result> AsyncTool run(@NonNull OnBackground<Result> onBackground) {
        return new AsyncTool(onBackground).run();
    }

    public static <Result> AsyncTool run(@NonNull OnBackground<Result> onBackground, @NonNull OnResult<Result> onResult) {
        return new AsyncTool(onBackground, onResult).run();
    }

    private final ExecutorService mExecutorService;
    private final OnBackground<Result> mOnBackground;
    private final OnResult<Result> mOnResult;
    private final Handler mResultHandler;
    private Future<?> mFuture;

    private AsyncTool(OnBackground<Result> onBackground) {
        this(onBackground, null);
    }

    private AsyncTool(OnBackground<Result> onBackground, OnResult<Result> onResult) {
        mExecutorService = Executors.newCachedThreadPool();
        mResultHandler = new Handler(Looper.getMainLooper());
        mOnBackground = onBackground;
        mOnResult = onResult;
    }

    private AsyncTool run() {
        mFuture = mExecutorService.submit(() -> {
            try {
                final Result result = mOnBackground.run();
                mResultHandler.post(() -> {
                    final OnResult<Result> onResult = mOnResult;
                    if (onResult != null) {
                        onResult.run(result);
                    }
                });
            } catch (Exception e) {
                // No need handle
            }
        });
        return this;
    }

    public boolean isFinished() {
        return mFuture == null || mFuture.isDone();
    }

    public void cancel() {
        final Handler handler = mResultHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        final Future<?> future = mFuture;
        if (future != null) {
            future.cancel(true);
        }
    }

}