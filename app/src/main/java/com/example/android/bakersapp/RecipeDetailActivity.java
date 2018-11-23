package com.example.android.bakersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakersapp.adapters.StepListRecyclerAdapter;
import com.example.android.bakersapp.models.Recipe;

import java.util.ArrayList;

public class RecipeDetailActivity
        extends AppCompatActivity
        implements StepListRecyclerAdapter.ListItemClickListener,
        StepDetailFragment.ButtonClickListener {

    private Bundle currentRecipeBundle;
    private ArrayList<Recipe> recipe;
    private int clickedItemIndex = 0;
    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            currentRecipeBundle = getIntent().getExtras();
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();

            if (currentRecipeBundle != null && currentRecipeBundle.containsKey("Current_Recipe")) {
                recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");
            }

            recipeDetailFragment.setArguments(currentRecipeBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, recipeDetailFragment, "rtag")
                    .commit();

            currentRecipeBundle.putInt("Step_Index", clickedItemIndex);

            if (findViewById(R.id.divider1) != null) {
                mTwoPane = true;

                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                stepDetailFragment.setArguments(currentRecipeBundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, stepDetailFragment, "stag")
                        .commit();
            }
        } else {
            currentRecipeBundle = savedInstanceState.getBundle("Recipe_Bundle");
            clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            RecipeDetailFragment recipeDetailFragment =
                    (RecipeDetailFragment) getSupportFragmentManager().findFragmentByTag("rtag");
            if (findViewById(R.id.divider1) != null) {
                mTwoPane = true;
                StepDetailFragment stepDetailFragment =
                        (StepDetailFragment) getSupportFragmentManager().findFragmentByTag("stag");
            }
        }

        String recipeName = recipe.get(0).getName();
        getSupportActionBar().setTitle(recipeName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        currentRecipeBundle.putInt("Step_Index", clickedItemIndex);

        if (mTwoPane) {
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(currentRecipeBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtras(currentRecipeBundle);
            startActivity(intent);
        }
    }


    @Override
    public void onButtonClick(int targetStepIndex) {

        clickedItemIndex = targetStepIndex;
        currentRecipeBundle.putInt("Step_Index", targetStepIndex);

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        FragmentManager sFragmentManager = getSupportFragmentManager();
        stepDetailFragment.setArguments(currentRecipeBundle);
        sFragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
        currentState.putBundle("Recipe_Bundle", currentRecipeBundle);
        currentState.putInt("Step_Index", clickedItemIndex);
    }

}
