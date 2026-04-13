package com.yuvraj.tallio_demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Added for debugging
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yuvraj.tallio_demo.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onStart() {
        super.onStart();
        // Initialize mAuth here as well to ensure it's ready
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Mapping UI elements
        EditText etEmail = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvError = findViewById(R.id.tv_error);
        TextView tvRegister = findViewById(R.id.tv_register);

        // 3. Handle Login
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                tvError.setText("Enter both email and password");
                return;
            }

            btnLogin.setEnabled(false);
            tvError.setText("Authenticating...");

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Welcome to Tallio!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            btnLogin.setEnabled(true);
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                            tvError.setText("Login Failed: " + errorMsg);
                        }
                    });
        });

        // 4. Handle Registration (Only if view exists)
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    tvError.setText("Enter email and password to register");
                    return;
                }

                if (password.length() < 6) {
                    tvError.setText("Password must be at least 6 characters");
                    return;
                }

                tvError.setText("Creating account...");
                btnLogin.setEnabled(false);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                goToMainActivity();
                            } else {
                                btnLogin.setEnabled(true);
                                tvError.setText("Registration Failed: " + task.getException().getMessage());
                            }
                        });
            });
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}