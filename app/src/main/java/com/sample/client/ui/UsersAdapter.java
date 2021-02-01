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

    private List<User> mUsersList;

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUsersList.get(position);
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
        return mUsersList != null? mUsersList.size() : 0;
    }

    @Nullable
    public User getLast() {
        return mUsersList != null? mUsersList.get(mUsersList.size() - 1) : null;
    }

    public void setUsers(@NonNull final List<User> userList) {
        mUsersList = userList;
        notifyDataSetChanged();
    }

    public void addUsers(@NonNull final List<User> userList) {
        if (mUsersList == null) {
            mUsersList = new ArrayList<>();
        }
        mUsersList.addAll(userList);
        notifyItemRangeChanged(mUsersList.size(), userList.size());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

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