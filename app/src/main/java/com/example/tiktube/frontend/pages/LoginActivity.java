package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.MainActivity;
import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.LoginResultCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.services.GoogleOAuth2Service;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    EditText emailInput;
    EditText passwordInput;

    Button loginButton, googleSignInButton;
    ImageView passwordToggle;
    boolean showPassword = false;

    LoginController loginController;
    GoogleOAuth2Service googleOAuth2Service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginController = new LoginController();
        googleOAuth2Service = new GoogleOAuth2Service(this);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        onLoginButtonClicked();
        onGoogleSignInClicked();
    }

    private void onLoginButtonClicked() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        passwordToggle = findViewById(R.id.passwordToggle);

        passwordToggle.setOnClickListener(v -> {
            if (showPassword) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility_foreground);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility_off_foreground);
            }
            showPassword = !showPassword;
            passwordInput.setSelection(passwordInput.getText().length());
        });

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(LoginActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            loginController.adminLogin(email, password).thenAccept(isAdmin -> {
                if (isAdmin) {
                    // Navigate to AdminActivity
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Attempt user login
                    try {
                        loginController.login(email, password, new LoginResultCallback() {
                            @Override
                            public void onLoginSuccess(FirebaseUser user) {
                                Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onLoginFailure(Exception exception) {
                                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).exceptionally(e -> {
                // Handle errors in admin login
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Admin login error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                return null;
            });
        });
    }

    private void onGoogleSignInClicked() {
        googleSignInButton.setOnClickListener(v -> {
            googleOAuth2Service.startGoogleSignIn(LoginActivity.this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) { // Request code for Google Sign-In
            googleOAuth2Service.handleSignInResult(data)
                    .thenAccept(firebaseUser -> {
                        Toast.makeText(this, "Welcome, " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .exceptionally(e -> {
                        Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return null;
                    });
        }
    }

}
