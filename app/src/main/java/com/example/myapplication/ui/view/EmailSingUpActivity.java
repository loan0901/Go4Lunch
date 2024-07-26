package com.example.myapplication.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityEmailSingUpBinding;
import com.example.myapplication.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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

        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        registerButton();
        logInButton();
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    private void registerButton(){
        binding.btnRegister.setOnClickListener(view -> {
            createUser();
        });
    }

    private void logInButton(){
        binding.tvLoginHere.setOnClickListener(view -> {
            startActivity(new Intent(EmailSingUpActivity.this, EmailLoginActivity.class));
        });
    }

    private void createUser(){
        String email = binding.etRegEmail.getText().toString();
        String password = binding.etRegPass.getText().toString();
        String name = binding.etName.getText().toString();

        if (TextUtils.isEmpty(email)){
            binding.etRegEmail.setError("Email cannot be empty");
            binding.etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            binding.etRegPass.setError("Password cannot be empty");
            binding.etRegPass.requestFocus();
        }else if (TextUtils.isEmpty(name)){
            binding.etName.setError("Name cannot be empty");
            binding.etName.requestFocus();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EmailSingUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        user.updateProfile(profileUpdates);
                        startActivity(new Intent(EmailSingUpActivity.this, LoginActivity.class));

                    }else{
                        Toast.makeText(EmailSingUpActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
