package com.project.testProject.network;

import com.project.testProject.model.Response;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {

    // Fetch all users
    @GET("users/")
    Single<List<Response>> getUsers();

}
