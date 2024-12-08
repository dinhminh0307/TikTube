package com.example.tiktube.frontend.register;

import android.os.Bundle;

import com.example.tiktube.api.SignUpAPI;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
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

public class RegisterActivity extends AppCompatActivity {

    SignUpAPI signUpAPI;

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
        signUpAPI = new SignUpAPI(this);
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
                    signUpAPI.register(email, password, name, phoneNum);
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}