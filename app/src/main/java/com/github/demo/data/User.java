package com.github.demo.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = User.TABLE)
public class User {

    public static final String TABLE = "users";
    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_LOGIN = "login";
    public static final String FIELD_AVATAR_URL = "avatar_url";

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
    private Integer id;

    @SerializedName(FIELD_AVATAR_URL)
    @Expose
    @DatabaseField(columnName = FIELD_AVATAR_URL)
    private String avatarUrl;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}