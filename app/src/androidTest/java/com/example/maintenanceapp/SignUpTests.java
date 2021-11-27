package com.example.maintenanceapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpTests {

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule
            = new ActivityScenarioRule<>(SignUpActivity.class);

    @Test
    public void changeUsertype_Landlord_NoLandlordKey() {
        // Switch user type to landlord
        onView(withId(R.id.radioLandlord)).perform(click());

        // The landlord key option should not be visible for entry
        onView(withId(R.id.landlordKey)).check((view, noViewFoundException) -> {assert view.getVisibility() == View.INVISIBLE;});
    }
}
