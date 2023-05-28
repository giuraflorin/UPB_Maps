package com.example.upbmaps;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HeatMapActivityTest {

    @Rule
    public ActivityTestRule<HeatMapActivity> activityRule =
            new ActivityTestRule<>(HeatMapActivity.class);

    @Test
    public void testHeatmapViewDisplayed() {
        onView(withId(R.id.heatmapView)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationHeatmapSelected() {
        onView(withId(R.id.bottom_navigation))
                .check(matches(isDisplayed()))
                .perform(click());

    }


}
