package com.github.demo.rest;


import com.github.demo.data.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String MAIN_URL = "https://api.github.com";

    @GET("/users")
    Call<List<User>> requestUsers();

    @GET("/users")
    Call<List<User>> requestUsersById(@Query("since") int id);

}
