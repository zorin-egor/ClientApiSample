package com.sample.client.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = User.TABLE)
public class User implements Parcelable {

    public static final String TABLE = "users";
    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_LOGIN = "login";
    public static final String FIELD_AVATAR_URL = "avatar_url";

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /*
    * Needed for ORMLite.
    * */
    public User() {}

    @SerializedName(FIELD_NAME_LOGIN)
    @Expose
    @DatabaseField(columnName = FIELD_NAME_LOGIN)
    private String login;

    @SerializedName(FIELD_NAME_ID)
    @Expose
    @DatabaseField(id = true, columnName = FIELD_NAME_ID)
    private String id;

    @SerializedName(FIELD_AVATAR_URL)
    @Expose
    @DatabaseField(columnName = FIELD_AVATAR_URL)
    private String avatarUrl;

    protected User(Parcel in) {
        login = in.readString();
        id = in.readString();
        avatarUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeString(id);
        dest.writeString(avatarUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}