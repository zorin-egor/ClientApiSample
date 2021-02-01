package com.sample.client.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.client.R;
import com.sample.client.data.User;
import com.sample.client.tools.UsersFacade;

import java.util.List;

public class MainActivity extends AppCompatActivity implements UsersFacade.OnUsersListener {

    private static final String RECYCLER_STATE = "RECYCLER_STATE";

    private RecyclerView mRecyclerView;
    private UsersAdapter mUsersAdapter;
    private ProgressBar mProgressBar;
    private Parcelable mRecyclerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(RECYCLER_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UsersFacade.getInstance().setListener(this);
    }

    @Override
    protected void onStop() {
        UsersFacade.getInstance().removeListener();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) { UsersFacade.getInstance().cancel(); }
        super.onDestroy();
    }

    private void init(final Bundle savedInstanceState) {
        mProgressBar = findViewById(R.id.github_progress);
        mProgressBar.setVisibility(View.VISIBLE);
        mUsersAdapter = new UsersAdapter();
        mRecyclerView = findViewById(R.id.github_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mUsersAdapter);
        mRecyclerView.addOnScrollListener(new OnEndListener() {
            @Override
            public void onListEnd() {
                mProgressBar.setVisibility(View.VISIBLE);
                final User user = mUsersAdapter.getLast();
                UsersFacade.getInstance().getUsers(user != null? user.getId() : null);
            }
        });

        UsersFacade.getInstance().init();

        if (savedInstanceState != null) {
            mRecyclerState = savedInstanceState.getParcelable(RECYCLER_STATE);
        }
    }

    private void restoreRecycler() {
        final Parcelable state = mRecyclerState;
        if (state != null) {
            mRecyclerState = null;
            mRecyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    public void onUsers(@NonNull List<User> items) {
        mUsersAdapter.addUsers(items);
        mProgressBar.setVisibility(View.GONE);
        restoreRecycler();
    }

    @Override
    public void onError(@NonNull final String message) {
        mProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, getString(R.string.api_message) + message, Toast.LENGTH_SHORT).show();
    }

}
