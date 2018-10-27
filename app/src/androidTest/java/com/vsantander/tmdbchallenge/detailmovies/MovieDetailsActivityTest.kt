package com.vsantander.tmdbchallenge.detailmovies

import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.runner.AndroidJUnit4
import com.google.gson.Gson
import com.vsantander.tmdbchallenge.R
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.presentation.detail.MovieDetailsActivity
import com.vsantander.tmdbchallenge.utils.activityTestRule
import com.vsantander.tmdbchallenge.utils.extension.fromJson
import okio.Okio
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.nio.charset.StandardCharsets

@RunWith(AndroidJUnit4::class)
class MovieDetailsActivityTest {

    @Rule
    @JvmField
    val activityTestRule = activityTestRule<MovieDetailsActivity>(launchActivity = false)

    @Before
    fun setUp() {
        val fileContent = getFileContentAsString("movie.json")
        val movie = Gson().fromJson<Movie>(fileContent)

        val i = Intent()
        i.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie)
        activityTestRule.launchActivity(i)
    }

    @Test
    fun infoViewIsShowing() {
        //Verify that the movie info is setted in the textViews
        Espresso.onView(ViewMatchers.withId(R.id.yearValueTextView))
                .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.withText(R.string.movie_details_no_info))))
        Espresso.onView(ViewMatchers.withId(R.id.overViewValueTextView))
                .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.withText(R.string.movie_details_no_info))))
    }

    @Test
    fun parallaxToolbarTest() {
        //Verify that appbar is expanded
        Espresso.onView(ViewMatchers.withId(R.id.appbar))
                .check(ViewAssertions.matches(ViewMatchers.withContentDescription(R.string.img_expanded)))

        //perform click and swipe up
        Espresso.onView(ViewMatchers.withId(R.id.appbar)).perform(ViewActions.click(), ViewActions.swipeUp())

        //We can't be really sure if the swiping has finished by the time we come to this point on all devices (slow)
        //so either a collaped or collapsing state passes the test
        Espresso.onView(ViewMatchers.withId(R.id.appbar)).check(ViewAssertions.matches(Matchers.anyOf(
                ViewMatchers.withContentDescription(R.string.img_collapsed),
                ViewMatchers.withContentDescription(R.string.img_collapsing))))
    }

    @Throws(IOException::class)
    private fun getFileContentAsString(fileName: String): String {
        val inputStream =
                javaClass.classLoader.getResourceAsStream("sampledata/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        return source.readString(StandardCharsets.UTF_8)
    }

}