package com.example.myapplication.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityEmailSingUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

// EmailSingUpActivity handles user registration functionality using Firebase Authentication.
public class EmailSingUpActivity extends AppCompatActivity {

    private ActivityEmailSingUpBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sing_up);

        // Initialize view binding
        binding = ActivityEmailSingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth if it is not already initialized
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        // Set up the register and login buttons
        registerButton();
        logInButton();
    }

    // Setter method to allow setting the FirebaseAuth instance (useful for testing)
    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // Set up the register button click listener
    private void registerButton(){
        binding.btnRegister.setOnClickListener(view -> {
            createUser();
        });
    }

    // Set up the login button click listener
    private void logInButton(){
        binding.tvLoginHere.setOnClickListener(view -> {
            startActivity(new Intent(EmailSingUpActivity.this, EmailLoginActivity.class));
        });
    }

    // Handle the user registration process
    private void createUser(){

        // Get the email, password, and name input from the user
        String email = binding.etRegEmail.getText().toString();
        String password = binding.etRegPass.getText().toString();
        String name = binding.etName.getText().toString();

        // Validate the email input
        if (TextUtils.isEmpty(email)){
            binding.etRegEmail.setError("Email cannot be empty");
            binding.etRegEmail.requestFocus();

            // Validate the password input
        }else if (TextUtils.isEmpty(password)){
            binding.etRegPass.setError("Password cannot be empty");
            binding.etRegPass.requestFocus();

            // Validate the name input
        }else if (TextUtils.isEmpty(name)){
            binding.etName.setError("Name cannot be empty");
            binding.etName.requestFocus();

            // Proceed with Firebase Authentication if all inputs are valid
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // If registration is successful, show a success message and update the user's profile
                    if (task.isSuccessful()){
                        Toast.makeText(EmailSingUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        user.updateProfile(profileUpdates);
                        startActivity(new Intent(EmailSingUpActivity.this, LoginActivity.class));
                    }else{
                        // If registration fails, show an error message
                        Toast.makeText(EmailSingUpActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
