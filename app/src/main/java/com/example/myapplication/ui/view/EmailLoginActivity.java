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

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton();
        registerButton();
    }

    private void loginButton(){
        binding.btnLogin.setOnClickListener(view -> {
            loginUser();
        });
    }

    private void registerButton(){
        binding.tvRegisterHere.setOnClickListener(view -> {
            startActivity(new Intent(EmailLoginActivity.this, EmailSingUpActivity.class));
        });
    }

    private void loginUser(){
        String email = binding.etLoginEmail.getText().toString();
        String password = binding.etLoginPass.getText().toString();

        if (TextUtils.isEmpty(email)){
            binding.etLoginEmail.setError("Email cannot be empty");
            binding.etLoginEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            binding.etLoginPass.setError("Password cannot be empty");
            binding.etLoginPass.requestFocus();
        }else{
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EmailLoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(EmailLoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}