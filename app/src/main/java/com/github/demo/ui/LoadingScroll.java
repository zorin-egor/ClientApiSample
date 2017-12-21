package com.github.demo.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.demo.data.User;
import com.github.demo.App;
import java.util.List;

abstract public class LoadingScroll extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 5;
    private int mPreviousTotalItemCount = 0;
    private boolean mLoadingMode = true;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final int visibleItemCount = layoutManager.getChildCount();
        final int totalItemCount = layoutManager.getItemCount();
        final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            if (mLoadingMode) {
                if (totalItemCount >= mPreviousTotalItemCount) {
                    mLoadingMode = false;
                    mPreviousTotalItemCount = totalItemCount;
                }
            }

            if (!mLoadingMode && visibleItemCount + firstVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD) {
                mLoadingMode = true;
                onListEnd();
            }
        }
    }

    abstract public void onListEnd();

}
