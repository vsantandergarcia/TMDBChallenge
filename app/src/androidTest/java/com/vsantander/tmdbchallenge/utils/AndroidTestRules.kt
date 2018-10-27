package com.vsantander.tmdbchallenge.utils

import android.app.Activity
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.rule.ActivityTestRule

inline fun <reified T : Activity> activityTestRule(initialTouchMode: Boolean = false, launchActivity: Boolean = true) =
        ActivityTestRule(T::class.java, initialTouchMode, launchActivity)

inline fun <reified T : Activity> intentsTestRule(initialTouchMode: Boolean = false, launchActivity: Boolean = true) =
        IntentsTestRule(T::class.java, initialTouchMode, launchActivity)