package com.example.android.bakersapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakersapp.models.Recipe;

import java.util.ArrayList;

public class StepDetailActivity extends AppCompatActivity
        implements StepDetailFragment.ButtonClickListener {

    private Bundle currentRecipeBundle;
    private ArrayList<Recipe> recipe;
    private Integer clickedItemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            currentRecipeBundle = getIntent().getExtras();
            if (currentRecipeBundle != null) {
                if (currentRecipeBundle.containsKey("Step_Index")) {
                    clickedItemIndex = currentRecipeBundle.getInt("Step_Index");
                }
                if (currentRecipeBundle.containsKey("Current_Recipe")) {
                    recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");
                }
            }

            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(currentRecipeBundle);
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.step_detail_container, stepDetailFragment, "stag").
                    commit();
        } else {
            clickedItemIndex = savedInstanceState.getInt("Step_Index");
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            currentRecipeBundle = new Bundle();
            currentRecipeBundle.putParcelableArrayList("Current_Recipe", recipe);
            currentRecipeBundle.putInt("Step_Index", clickedItemIndex);
            StepDetailFragment stepDetailFragment =
                    (StepDetailFragment) getSupportFragmentManager().findFragmentByTag("stag");
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
    public void onButtonClick(int targetStepIndex) {

        clickedItemIndex = targetStepIndex;
        currentRecipeBundle.putInt("Step_Index", targetStepIndex);

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(currentRecipeBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment, "stag")
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
        currentState.putInt("Step_Index", clickedItemIndex);
    }

}
