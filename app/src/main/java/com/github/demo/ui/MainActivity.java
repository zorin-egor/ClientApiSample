package com.github.demo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.demo.App;
import com.github.demo.data.User;
import com.github.demo.orm.HelperFactory;
import com.github.demo.R;
import com.github.demo.rest.RetrofitTool;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RetrofitTool.Callbacks {

    private RecyclerView mRecyclerView;
    private UsersAdapter mUsersAdapter;
    private RetrofitTool mRetrofitTool;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRetrofitTool.setCallback(null);
    }

    @Override
    public void onSuccess(final List<User> userList) {
        mUsersAdapter.addUserList(userList);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onError(final String message, final int errorCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this, 
                getString(R.string.api_message) + message + "; " + getString(R.string.api_error) + errorCode, 
                Toast.LENGTH_SHORT);
    }

    private void init(final Bundle savedInstanceState) {
        final List<User> userList = HelperFactory.getHelper().loadData();

        // Set handler for rest utils
        mRetrofitTool = App.getInstance().getUtils();
        mRetrofitTool.setCallback(this);

        // If first load
        if(userList == null || userList.isEmpty()) {
            mRetrofitTool.getUsers();
        }

        // Views init
        mProgressBar = findViewById(R.id.github_progress);
        mProgressBar.setVisibility(mRetrofitTool.isRequesting()? View.VISIBLE : View.INVISIBLE);
        mRecyclerView = findViewById(R.id.github_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsersAdapter = new UsersAdapter(this);
        mRecyclerView.setAdapter(mUsersAdapter);
        mUsersAdapter.setUserList(userList);
        mRecyclerView.addOnScrollListener(new LoadingScroll() {
            @Override
            public void onListEnd() {
                mProgressBar.setVisibility(View.VISIBLE);
                final User user = mUsersAdapter.getLast();
                if (user != null) {
                    mRetrofitTool.getUsersById(user.getId());
                } else {
                    mRetrofitTool.getUsers();
                }
            }
        });
    }

}
