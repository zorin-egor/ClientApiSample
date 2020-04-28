package com.sample.client.api;


import com.sample.client.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String MAIN_URL = "https://api.github.com";

    @GET("/users")
    Call<List<User>> requestUsers();

    @GET("/users")
    Call<List<User>> requestUsersById(@Query("since") String id);

}
