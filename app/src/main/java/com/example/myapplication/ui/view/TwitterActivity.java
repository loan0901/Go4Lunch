package com.example.myapplication.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

// TwitterActivity handles user login functionality using Twitter OAuth provider.
public class TwitterActivity extends LoginActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        provider.addCustomParameter("lang", "fr");

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask
                    .addOnSuccessListener(
                            authResult -> {
                                Toast.makeText(TwitterActivity.this, R.string.connexion_success, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TwitterActivity.this, MainActivity.class));
                            })
                    .addOnFailureListener(
                            e -> Toast.makeText(TwitterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            firebaseAuth
                    .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(
                            authResult -> {
                                Toast.makeText(TwitterActivity.this, R.string.connexion_success, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TwitterActivity.this, MainActivity.class));

                            })
                    .addOnFailureListener(
                            e -> Toast.makeText(TwitterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
