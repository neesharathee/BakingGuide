package com.example.android.bakersapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class UIActivityBasicTest {

    @Rule
    public final ActivityTestRule<MainListActivity> mActivityTestRule =
            new ActivityTestRule<>(MainListActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
       IdlingRegistry.getInstance().register(mIdlingResource);
    }

    private static final String RECIPE_NAME = "Yellow Cake";
    @Test
    public void checkMainRecyclerText(){
        onView(ViewMatchers.withId(R.id.rv_main_list)).perform(RecyclerViewActions.scrollToPosition(2));
        onView(withText(RECIPE_NAME)).check(matches(isDisplayed()));
    }

    private static final String DETAIL_RECIPE_NAME = "Brownies";
    @Test
    public void checkDetailPaneTitleText(){
        onView(ViewMatchers.withId(R.id.rv_main_list)).
                perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(ViewMatchers.withId(R.id.tv_recipe_name)).
                check(matches(withText(DETAIL_RECIPE_NAME)));
    }

    @Test
    public void checkPlayerVisible() {
        onView(ViewMatchers.withId(R.id.rv_main_list)).
                perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(ViewMatchers.withId(R.id.rv_steps)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.exo_player_view)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
