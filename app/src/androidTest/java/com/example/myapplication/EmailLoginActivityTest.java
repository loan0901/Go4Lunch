package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.myapplication.ui.view.EmailLoginActivity;
import com.example.myapplication.ui.view.EmailSingUpActivity;
import com.example.myapplication.ui.view.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class EmailLoginActivityTest {

    @Rule
    public ActivityScenarioRule<EmailLoginActivity> activityScenarioRule = new ActivityScenarioRule<>(EmailLoginActivity.class);

    @Mock
    FirebaseAuth mockFirebaseAuth;

    @Mock
    FirebaseUser mockFirebaseUser;

    @Mock
    AuthResult mockAuthResult;

    @Before
    public void setUp() {
        // Initialize Mockito and Espresso Intents
        MockitoAnnotations.openMocks(this);
        Intents.init();

        // Prevent Espresso Intents from blocking real intents for internal activities
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        // Mock the FirebaseAuth instance
        ActivityScenario<EmailLoginActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> activity.setFirebaseAuth(mockFirebaseAuth));

        // Mock the FirebaseUser
        when(mockFirebaseUser.getUid()).thenReturn("mockUid");
        when(mockFirebaseUser.getDisplayName()).thenReturn("Mock User");
        when(mockFirebaseUser.getPhotoUrl()).thenReturn(null);

        // Mock the FirebaseAuth to return the mock user
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);

        // Mock the sign-in behavior with a successful result
        Task<AuthResult> successfulAuthResultTask = Tasks.forResult(mockAuthResult);
        when(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(successfulAuthResultTask);
    }

    @After
    public void tearDown() {
        // Release Espresso Intents
        Intents.release();
    }

    @Test
    public void testLoginUser_Success() {
        // Type email and password
        onView(withId(R.id.etLoginEmail)).perform(typeText("louiseloan357@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.etLoginPass)).perform(typeText("password"), closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.btnLogin)).perform(click());

        // Check if the intent to start MainActivity was sent
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testLoginUser_Failure() {
        // Mock a failed login
        Task<AuthResult> failedAuthResultTask = Tasks.forException(new Exception("Login failed"));
        when(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(failedAuthResultTask);

        // Type email and password
        onView(withId(R.id.etLoginEmail)).perform(typeText("louiseloan357@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.etLoginPass)).perform(typeText("password"), closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.btnLogin)).perform(click());

        // check that EmailLoginActivity is still displayed
        onView(withId(R.id.etLoginEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToRegister() {
        // Click on the register link
        onView(withId(R.id.tvRegisterHere)).perform(click());

        // Check if the intent to start EmailSingUpActivity was sent
        intended(hasComponent(EmailSingUpActivity.class.getName()));
    }
}