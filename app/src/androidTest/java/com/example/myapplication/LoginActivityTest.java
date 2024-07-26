package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.ui.view.EmailLoginActivity;
import com.example.myapplication.ui.view.LoginActivity;
import com.example.myapplication.ui.view.TwitterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Before
    public void setUp() {
        // Initialize Intents before each test
        Intents.init();

        // Launch the LoginActivity
        ActivityScenario.launch(LoginActivity.class);
    }

    @After
    public void tearDown() {
        // Release Intents after each test
        Intents.release();
    }

    @Test
    public void testGoogleSignInButtonDisplayed() {
        // Check if the Google Sign-In button is displayed
        onView(withId(R.id.googleSingInButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testFacebookSignInButtonDisplayed() {
        // Check if the Facebook Sign-In button is displayed
        onView(withId(R.id.facebookSingInButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmailLoginButtonDisplayed() {
        // Check if the Email Login button is displayed
        onView(withId(R.id.eMailButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testTwitterLoginButtonDisplayed() {
        // Check if the Twitter Login button is displayed
        onView(withId(R.id.twitterLoginButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmailLoginButtonClick() {
        // Click the Email Login button and verify the email login intent is started
        onView(withId(R.id.eMailButton)).perform(click());
        intended(hasComponent(EmailLoginActivity.class.getName()));
    }

    @Test
    public void testTwitterLoginButtonClick() {
        // Click the Twitter Login button and verify the twitter login intent is started
        onView(withId(R.id.twitterLoginButton)).perform(click());
        intended(hasComponent(TwitterActivity.class.getName()));
    }
}
