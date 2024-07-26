package com.example.myapplication.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityEmailLoginBinding;
import com.example.myapplication.databinding.ActivityEmailSingUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// EmailLoginActivity handles user login functionality using Firebase Authentication.
public class EmailLoginActivity extends AppCompatActivity {

    private ActivityEmailLoginBinding binding;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        // Initialize view binding
        binding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth if it is not already initialized
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        // Set up the login and register buttons
        loginButton();
        registerButton();
    }

    // Setter method to allow setting the FirebaseAuth instance (useful for testing)
    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // Set up the login button click listener
    private void loginButton(){
        binding.btnLogin.setOnClickListener(view -> {
            loginUser();
        });
    }

    // Set up the register button click listener
    private void registerButton(){
        binding.tvRegisterHere.setOnClickListener(view -> {
            startActivity(new Intent(EmailLoginActivity.this, EmailSingUpActivity.class));
        });
    }

    // Handle the user login process
    private void loginUser(){

        // Get the email and password input from the user
        String email = binding.etLoginEmail.getText().toString();
        String password = binding.etLoginPass.getText().toString();

        // Validate the email input
        if (TextUtils.isEmpty(email)){
            binding.etLoginEmail.setError("Email cannot be empty");
            binding.etLoginEmail.requestFocus();

            // Validate the password input
        }else if (TextUtils.isEmpty(password)){
            binding.etLoginPass.setError("Password cannot be empty");
            binding.etLoginPass.requestFocus();

            // Proceed with Firebase Authentication if both inputs are valid
        }else{
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // If sign-in is successful, show a success message and navigate to MainActivity
                    if (task.isSuccessful()){
                        Toast.makeText(EmailLoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));
                    }else{
                        // If sign-in fails, show an error message
                        Toast.makeText(EmailLoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
