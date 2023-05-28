package com.example.upbmaps;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> activityRule =
            new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void testGreetingDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.textViewGreeting))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEmailDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.textViewEmail))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testChangePasswordButtonDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonChangePassword))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    // Add more test methods to cover other UI elements and specific functionality
}
