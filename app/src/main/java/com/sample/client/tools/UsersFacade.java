package com.sample.client.tools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sample.client.data.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UsersFacade implements RetrofitTool.OnRequestListener {

    public interface OnUsersListener {
        void onUsers(@NonNull final List<User> items);
        void onError(@NonNull final String message);
    }

    private static volatile UsersFacade sUsersFacade;

    public static UsersFacade getInstance() {
        if (sUsersFacade == null) {
            synchronized (UsersFacade.class) {
                if (sUsersFacade == null) {
                    sUsersFacade = new UsersFacade();
                }
            }
        }
        return sUsersFacade;
    }

    private WeakReference<OnUsersListener> mWeakReference;
    private AsyncTool mInitDatabase;
    private List<AsyncTool> mDatabaseTasks;
    private List<User> mUsersCache;

    private AsyncTool.OnBackground<List<User>> mBackgroundDatabase = () -> {
        return OrmLiteTool.getInstance().loadData();
    };

    private AsyncTool.OnResult<List<User>> mResultDatabase = (result) -> {
        if (result == null || result.isEmpty()) {
            getUsers(null);
        } else {
            final WeakReference<OnUsersListener> reference = mWeakReference;
            if (reference != null) {
                final OnUsersListener listener = reference.get();
                if (listener != null) {
                    listener.onUsers(result);
                    return;
                }
            }
            mUsersCache.addAll(result);
        }
    };

    private UsersFacade() {
        mWeakReference = null;
        mInitDatabase = null;
        mDatabaseTasks = new ArrayList<>();
        mUsersCache = new ArrayList<>();
        RetrofitTool.getInstance().setListener(this);
    }

    public void setListener(OnUsersListener listener) {
        mWeakReference = new WeakReference(listener);
        if (!mUsersCache.isEmpty()) {
            listener.onUsers(mUsersCache);
            mUsersCache.clear();
        }
    }

    public void removeListener() {
        mWeakReference = null;
    }

    public void init() {
        if (mInitDatabase == null || mInitDatabase.isFinished()) {
            mInitDatabase = AsyncTool.run(mBackgroundDatabase, mResultDatabase);
        }
    }

    public void getUsers(@Nullable final String sinceId) {
        if (sinceId != null) {
            RetrofitTool.getInstance().getUsersById(sinceId);
        } else {
            RetrofitTool.getInstance().getUsers();
        }
    }

    public void cancel() {
        RetrofitTool.getInstance().cancel();
        cancelSaveTasks();
        mInitDatabase.cancel();
    }

    private void removeFinishedSaveTasks() {
        final Iterator<AsyncTool> it = mDatabaseTasks.iterator();
        while (it.hasNext()) {
            final AsyncTool item = it.next();
            if (item.isFinished()) {
                it.remove();
            }
        }
    }

    private void cancelSaveTasks() {
        for (AsyncTool item : mDatabaseTasks) {
            item.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull List<User> items) {
        mDatabaseTasks.add(AsyncTool.run(() -> {
            OrmLiteTool.getInstance().saveData(items);
            removeFinishedSaveTasks();
            return null;
        }));

        final WeakReference<OnUsersListener> reference = mWeakReference;
        if (reference != null) {
            final OnUsersListener listener = reference.get();
            if (listener != null) {
                listener.onUsers(items);
                return;
            }
        }
        mUsersCache.addAll(items);
    }

    @Override
    public void onError(@Nullable String error) {
        if (error == null) {
            return;
        }

        final WeakReference<OnUsersListener> reference = mWeakReference;
        if (reference != null) {
            final OnUsersListener listener = reference.get();
            if (listener != null) {
                listener.onError(error);
            }
        }
    }
}
