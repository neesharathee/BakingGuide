package com.example.android.bakersapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bakersapp.adapters.MainListRecyclerAdapter;
import com.example.android.bakersapp.test.SimpleIdlingResource;
import com.example.android.bakersapp.models.Recipe;
import com.example.android.bakersapp.network.GetDataService;
import com.example.android.bakersapp.network.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainListActivity
        extends AppCompatActivity
        implements MainListRecyclerAdapter.ListItemClickListener {

    private RecyclerView recyclerView;
    private LinearLayout mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ArrayList<Recipe> recipes;

    @Nullable
    private SimpleIdlingResource mIdlingResource;


    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    private void setIdlingResource(Boolean in) {
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(in);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        getIdlingResource();

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        mErrorMessageDisplay = (LinearLayout) findViewById(R.id.ll_error_message_display);
        Button mRetryButton = (Button) findViewById(R.id.retry_button);
        mRetryButton.setOnClickListener(
                new OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }
        );

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        float dens = resources.getDisplayMetrics().density;

        int screenWidthDp = config.screenWidthDp;
        int screenWidthPx = (int) (screenWidthDp * dens);
        int itemWidthPx = (int) (resources.getDimension(R.dimen.recipe_card_width));
        int numberOfColumns = (screenWidthPx / itemWidthPx);

        recyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        final MainListRecyclerAdapter adapter = new MainListRecyclerAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState == null) {
            GetDataService service = RetrofitClientInstance.getRecipejson();
            Call<ArrayList<Recipe>> call = service.getAllRecipes();

            setIdlingResource(false);

            call.enqueue(new Callback<ArrayList<Recipe>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Recipe>> call,
                                       @NonNull Response<ArrayList<Recipe>> response) {
                    recipes = response.body();
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setRecipeData(recipes);
                    setIdlingResource(true);
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.VISIBLE);
                    Toast.makeText(MainListActivity.this,
                            R.string.network_call_error_toast_message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            recipes = savedInstanceState.getParcelableArrayList("All_recipes");
            if (recipes == null) {
                savedInstanceState = null;
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            } else {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setRecipeData(recipes);
            }
        }

    }

    @Override
    public void onListItemClick(Recipe clickedRecipeCard) {
        ArrayList<Recipe> selectedRecipe = new ArrayList<>();
        selectedRecipe.add(clickedRecipeCard);
        Bundle specificRecipeBundle = new Bundle();
        specificRecipeBundle.putParcelableArrayList("Current_Recipe", selectedRecipe);

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(specificRecipeBundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("All_recipes", recipes);
    }

}
