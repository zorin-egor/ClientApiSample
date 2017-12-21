package com.github.demo.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.github.demo.data.User;
import com.github.demo.R;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private final Context mContext;
    private List<User> mUserList;

    public UsersAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUserList.get(position);
        // Load mAvatar with picasso
        Picasso.with(mContext)
                .load(user.getAvatarUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .transform(new PicassoTransform())
                .error(R.mipmap.ic_launcher)
                .into(holder.mAvatar);

        // Set mLogin
        holder.mLogin.setText(user.getLogin());
    }

    @Override
    public int getItemCount() {
        return mUserList != null? mUserList.size() : 0;
    }

    public User getLast() {
        return mUserList != null? mUserList.get(mUserList.size() - 1) : null;
    }

    public void setUserList(@NonNull final List<User> userList) {
        mUserList = userList;
        notifyDataSetChanged();
    }

    public void addUserList(@NonNull final List<User> userList) {
        if (mUserList == null) {
            mUserList = new ArrayList<>();
        }

        mUserList.addAll(userList);
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mLogin;

        public ViewHolder(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.github_avatar);
            mLogin = itemView.findViewById(R.id.github_login);
        }
    }

}