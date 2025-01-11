package com.example.tiktube.frontend.pages;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.tiktube.MainActivity;
import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.LoginController;

public class SettingActivity extends AppCompatActivity {
    LinearLayout logout;
    LoginController loginController;
    LinearLayout backToPrevious, customerServiceBtn, displayBtn;
    boolean isDarkMode;
    ImageView displayIcon;
    Dialog displayDialog;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        setUpComponent();
        onLogoutClicked();
        setUpBackNavigation();
        setUpServiceNavigation();
        setUpDisplayNavigation();
    }

    private void setUpComponent() {
//        Save display mode status
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        displayIcon = findViewById(R.id.displayIcon);
//        Apply display mode on launch
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            displayIcon.setImageResource(R.drawable.ic_nightlight_foreground);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            displayIcon.setImageResource(R.drawable.ic_wb_sunny_foreground);
        }

        logout = findViewById(R.id.logoutId);
        //controller
        loginController = new LoginController();
        backToPrevious = findViewById(R.id.backToPrevious);
        customerServiceBtn = findViewById(R.id.customerServiceBtn);
        displayBtn = findViewById(R.id.displayBtn);
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

    private void setUpServiceNavigation() {
        customerServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate to ServiceActivity
                Intent intent = new Intent(SettingActivity.this, ServiceActivity.class);
                startActivity(intent);
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

    private void setUpDisplayNavigation() {
        displayBtn.setOnClickListener(v -> {
            displayDialog = new Dialog(this);
            displayDialog.setContentView(R.layout.dialog_display);
            ImageView btnDisplayClose = displayDialog.findViewById(R.id.btnDisplayClose);
            RadioButton radioLight = displayDialog.findViewById(R.id.radioLight);
            RadioButton radioDark = displayDialog.findViewById(R.id.radioDark);

            radioDark.setChecked(isDarkMode);
            radioLight.setChecked(!isDarkMode);

            radioLight.setOnCheckedChangeListener(listenerRadio);
            radioDark.setOnCheckedChangeListener(listenerRadio);
            btnDisplayClose.setOnClickListener(v2 -> displayDialog.dismiss());

            displayDialog.show();
        });
    }

    CompoundButton.OnCheckedChangeListener listenerRadio = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                Toast.makeText(SettingActivity.this, compoundButton.getText(), Toast.LENGTH_SHORT).show();
                if (compoundButton.getText().equals("Light")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkMode", false);
                    editor.apply();
                    displayIcon.setImageResource(R.drawable.ic_wb_sunny_foreground);
                    isDarkMode = false;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkMode", true);
                    editor.apply();
                    displayIcon.setImageResource(R.drawable.ic_nightlight_foreground);
                    isDarkMode = true;
                }
            }
        }
    };
}