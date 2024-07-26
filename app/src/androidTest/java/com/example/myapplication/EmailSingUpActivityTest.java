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
import com.example.myapplication.ui.view.LoginActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class EmailSingUpActivityTest {

    @Rule
    public ActivityScenarioRule<EmailSingUpActivity> activityScenarioRule = new ActivityScenarioRule<>(EmailSingUpActivity.class);

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
        ActivityScenario<EmailSingUpActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> activity.setFirebaseAuth(mockFirebaseAuth));

        // Mock the FirebaseUser
        when(mockFirebaseUser.getUid()).thenReturn("mockUid");
        when(mockFirebaseUser.getDisplayName()).thenReturn("Mock User");
        when(mockFirebaseUser.getPhotoUrl()).thenReturn(null);

        // Mock the FirebaseAuth to return the mock user
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
    }

    @After
    public void tearDown() {
        // Release Espresso Intents
        Intents.release();
    }

    @Test
    public void testRegisterUser_Success() {
        // Mock the sign-up behavior with a successful result
        Task<AuthResult> successfulAuthResultTask = Tasks.forResult(mockAuthResult);
        when(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successfulAuthResultTask);

        // Type email, password, and name
        onView(withId(R.id.etRegEmail)).perform(typeText("louiseloan357@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.etRegPass)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.etName)).perform(typeText("Louis"), closeSoftKeyboard());

        // Click on the register button
        onView(withId(R.id.btnRegister)).perform(click());

        // Check if the intent to start LoginActivity was sent
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testRegisterUser_Failure() {
        // Mock a failed sign-up
        Task<AuthResult> failedAuthResultTask = Tasks.forException(new Exception("Registration failed"));
        when(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(failedAuthResultTask);

        // Type email, password, and name
        onView(withId(R.id.etRegEmail)).perform(typeText("louiseloan357@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.etRegPass)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.etName)).perform(typeText("Louis"), closeSoftKeyboard());

        // Click on the register button
        onView(withId(R.id.btnRegister)).perform(click());

        // check that EmailLoginActivity is still displayed
        onView(withId(R.id.etRegEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToLogin() {
        // Click on the login link
        onView(withId(R.id.tvLoginHere)).perform(click());

        // Check if the intent to start EmailLoginActivity was sent
        intended(hasComponent(EmailLoginActivity.class.getName()));
    }
}