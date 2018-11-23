package com.example.android.bakersapp.network;

import com.example.android.bakersapp.models.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

// retrofit interface
public interface GetDataService {

    @GET("/android-baking-app-json")
    Call<ArrayList<Recipe>> getAllRecipes();

}
