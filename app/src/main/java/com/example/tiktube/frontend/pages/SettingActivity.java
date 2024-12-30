package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.MainActivity;
import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.LoginController;

public class SettingActivity extends AppCompatActivity {
    LinearLayout logout;
    LoginController loginController;
    LinearLayout backToPrevious;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        setUpComponent();
        onLogoutClicked();
        setUpBackNavigation();
    }

    private void setUpComponent() {
        logout = findViewById(R.id.logoutId);
        //controller
        loginController = new LoginController();
        backToPrevious = findViewById(R.id.backToPrevious);
    }

    private void onLogoutClicked() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginController.userSignOut();
                // Clear the activity stack and navigate to MainActivity
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack
                startActivity(intent);
                finish(); // Finish current activity
                Toast.makeText(SettingActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpBackNavigation() {
        // Set up the "Back" button functionality
        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and return to the previous activity
                finish();
            }
        });
    }
}