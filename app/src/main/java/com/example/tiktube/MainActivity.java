package com.example.tiktube;

import android.content.Intent;
import android.os.Bundle;

import com.example.tiktube.api.AuthAPI;
import com.example.tiktube.frontend.login.LoginActivity;
import com.example.tiktube.frontend.profile.ProfileActivity;
import com.example.tiktube.frontend.register.RegisterActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tiktube.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    Button signUpBtn;

    AuthAPI authAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        authAPI = new AuthAPI();

        onSignUpButtonClicked();
        onLoginButtonClicked();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = authAPI.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        }
    }

    private void onSignUpButtonClicked() {
        signUpBtn = findViewById(R.id.signUpButton);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onLoginButtonClicked() {
        loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}