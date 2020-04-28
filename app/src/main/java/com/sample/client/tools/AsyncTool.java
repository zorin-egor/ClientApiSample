package com.sample.client.tools;

import android.os.AsyncTask;

public class AsyncTool<R> {

    @FunctionalInterface
    public interface OnBackground<T> {
        T run() throws InterruptedException;
    }

    @FunctionalInterface
    public interface OnResult<T> {
        void run(T t);
    }

    public static <R> AsyncTool run(OnBackground<R> onBackground) {
        return new AsyncTool(onBackground).run();
    }

    public static <R> AsyncTool run(OnBackground<R> onBackground, OnResult<R> onResult) {
        return new AsyncTool(onBackground, onResult).run();
    }

    private OnBackground<R> mOnBackground;
    private OnResult<R> mOnResult;
    private AsyncTask<Void, Void, R> mTask;

    private AsyncTool() {}

    private AsyncTool(OnBackground<R> onBackground) {
        this(onBackground, null);
    }

    private AsyncTool(OnBackground<R> onBackground, OnResult<R> onResult) {
        mOnBackground = onBackground;
        mOnResult = onResult;
    }

    private AsyncTool run() {
        mTask = new AsyncTask<Void, Void, R>() {
            @Override
            protected R doInBackground(Void... voids) {
                try {
                    return mOnBackground.run();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(R result) {
                super.onPostExecute(result);
                try {
                    mOnResult.run(result);
                } catch (Exception e) {
                    // Stub
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return this;
    }

    public void setResultCallback(OnResult<R> onResult) {
        mOnResult = onResult;
        if (mTask.getStatus() == AsyncTask.Status.FINISHED) {
            try {
                mOnResult.run(mTask.get());
            } catch (Exception e) {
                // Stub
            }
        }
    }

    public void removeResultCallback() {
        mOnResult = null;
    }

    public boolean cancel() {
        return mTask != null && mTask.cancel(true);
    }

}