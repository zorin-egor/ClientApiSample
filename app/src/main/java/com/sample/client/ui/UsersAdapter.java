package com.sample.client.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sample.client.R;
import com.sample.client.data.User;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> mUserList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUserList.get(position);
        if (user != null) {
            Glide.with(holder.mAvatar)
                    .load(user.getAvatarUrl())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(
                            holder.mAvatar.getContext().getResources().getDimensionPixelSize(R.dimen.corners_radius))))
                    .into(holder.mAvatar);

            holder.mLogin.setText(user.getLogin() + ": ");
            holder.mId.setText(user.getId());
        }
    }

    @Override
    public int getItemCount() {
        return mUserList != null? mUserList.size() : 0;
    }

    @Nullable
    public List<User> getUsers() {
        return mUserList;
    }

    @Nullable
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
        notifyItemRangeChanged(mUserList.size(), userList.size());
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mLogin;
        TextView mId;

        public ViewHolder(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.github_avatar);
            mLogin = itemView.findViewById(R.id.github_login);
            mId = itemView.findViewById(R.id.github_id);
        }
    }

}