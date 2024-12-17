package com.example.tiktube.frontend.register;

import android.content.Intent;
import android.os.Bundle;

import com.example.tiktube.MainActivity;
import com.example.tiktube.backend.callbacks.SignUpCallback;
import com.example.tiktube.backend.register.RegisterController;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tiktube.databinding.ActivityRegisterBinding;

import com.example.tiktube.R;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    RegisterController registerController;

    EditText nameInput;

    EditText phoneNumberInput;
    EditText emailInput;
    EditText passwordInput;

    Button finishBtn;

    ImageView passwordToggle;

    boolean pwdVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerController = new RegisterController(this);
        togglePassword();
        onFinishButtonClicked();
    }

    private void togglePassword() {
        passwordToggle = findViewById(R.id.passwordToggle);
        passwordInput = findViewById(R.id.passwordInput);
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdVisible = !pwdVisible;
                if(pwdVisible) {
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });
    }

    private void onFinishButtonClicked() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        finishBtn = findViewById(R.id.finishButton);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String phoneNum = phoneNumberInput.getText().toString().trim();
                String name = nameInput.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty() || phoneNum.isEmpty() || name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    registerController.register(email, password, name, phoneNum, new SignUpCallback() {
                        @Override
                        public void onSuccess(FirebaseUser user) {
                            Toast.makeText(RegisterActivity.this, "User created successfully: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            Log.d("RegisterService", "User: " + user.getEmail());
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Log.d("RegisterActivity", "Navigating to MainActivity and finishing activity");
                            finish();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            if (exception != null) {
                                if (exception instanceof FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(RegisterActivity.this, "Password too weak. Must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(RegisterActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                                } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, "This email is already registered.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Unknown error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}