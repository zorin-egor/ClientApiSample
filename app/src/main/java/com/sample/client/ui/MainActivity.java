package com.sample.client.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.client.R;
import com.sample.client.data.User;
import com.sample.client.tools.AsyncTool;
import com.sample.client.tools.OrmLiteTool;
import com.sample.client.tools.RetrofitTool;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RetrofitTool.Callbacks {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static AsyncTool.OnBackground<List<User>> sBackgroundOrmLite = () -> {
        return OrmLiteTool.getInstance().loadData();
    };

    private RecyclerView mRecyclerView;
    private UsersAdapter mUsersAdapter;
    private ProgressBar mProgressBar;
    private AsyncTool mAsyncTool;

    private AsyncTool.OnResult<List<User>> mResultOrmLite = (result) -> {
        if (result == null || result.isEmpty()) {
            RetrofitTool.getInstance().getUsers();
        } else {
            mUsersAdapter.setUserList(result);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitTool.getInstance().cancel();
        mAsyncTool.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        RetrofitTool.getInstance().setCallback(this);
        mAsyncTool.setResultCallback(mResultOrmLite);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RetrofitTool.getInstance().removeCallback();
        mAsyncTool.removeResultCallback();
    }

    @Override
    public void onSuccess(final List<User> userList) {
        mUsersAdapter.addUserList(userList);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(final String message, final int errorCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, getString(R.string.api_message) + message + "; " + getString(R.string.api_error) + errorCode,
                Toast.LENGTH_SHORT).show();
    }

    private void init(final Bundle savedInstanceState) {
        initViews(savedInstanceState);
        initData();
    }

    private void initViews(final Bundle savedInstanceState) {
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
                if (user != null) {
                    RetrofitTool.getInstance().getUsersById(user.getId());
                } else {
                    RetrofitTool.getInstance().getUsers();
                }
            }
        });
    }

    private void initData() {
        mAsyncTool = AsyncTool.run(sBackgroundOrmLite, mResultOrmLite);
    }

}
