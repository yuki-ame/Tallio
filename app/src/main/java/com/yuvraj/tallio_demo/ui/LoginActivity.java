package com.yuvraj.tallio_demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yuvraj.tallio_demo.R;

/**
 * LoginActivity.java - STATIC LOGIN SCREEN
 *
 * Simple login that compares the entered username and password
 * against hardcoded credentials using an if-else statement.
 *
 * Username: ADMIN
 * Password: Pass@123
 */
public class LoginActivity extends AppCompatActivity {

    // Hardcoded credentials
    private static final String CORRECT_USERNAME = "ADMIN";
    private static final String CORRECT_PASSWORD = "Pass@123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvError = findViewById(R.id.tv_error);

        btnLogin.setOnClickListener(v -> {
            String enteredUsername = etUsername.getText().toString().trim();
            String enteredPassword = etPassword.getText().toString().trim();

            // Check if fields are empty
            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                tvError.setText("Please enter both username and password.");
                return;
            }

            // Compare entered credentials with the correct ones
            if (enteredUsername.equals(CORRECT_USERNAME) && enteredPassword.equals(CORRECT_PASSWORD)) {
                // Correct! Go to the main app
                Toast.makeText(this, "Welcome to Tallio!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish(); // Close LoginActivity so user can't go back to it
            } else {
                // Wrong credentials
                tvError.setText("Invalid username or password. Please try again.");
                etPassword.setText(""); // Clear password field
            }
        });
    }
}