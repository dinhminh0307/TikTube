package com.example.tiktube.frontend.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.tiktube.R;
import com.example.tiktube.api.SignUpAPI;
import com.example.tiktube.backend.callbacks.LoginResultCallback;
import com.example.tiktube.backend.exceptions.InvalidCredentialException;
import com.example.tiktube.frontend.register.RegisterActivity;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailInput;
    EditText passwordInput;

    Button loginButton;

    SignUpAPI signUpAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        signUpAPI = new SignUpAPI(this);

        onLoginButtonClicked();
    }

    private void onLoginButtonClicked() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                try {
                    signUpAPI.login(email, password, new LoginResultCallback() {
                        @Override
                        public void onLoginSuccess(FirebaseUser user) {
                            Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onLoginFailure(Exception exception) {
                            Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}