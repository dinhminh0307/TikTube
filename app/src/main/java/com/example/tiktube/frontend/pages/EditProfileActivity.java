package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.User;

import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private UserController userController;
    private LoginController loginController;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        editUserProfile();

    }

    public void editUserProfile() {
        // References to UI components
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText phoneNumEditText = findViewById(R.id.phoneNumEditText);
        EditText bioEditText = findViewById(R.id.bioEditText);
        EditText instagramEditText = findViewById(R.id.instagramEditText);
        EditText facebookEditText = findViewById(R.id.facebookEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button returnButton = findViewById(R.id.returnButton);

        // Set save button click listener
        saveButton.setOnClickListener(v -> {
            // Get inputs from UI
            String name = nameEditText.getText().toString().trim();
            String phoneNum = phoneNumEditText.getText().toString().trim();
            String bio = bioEditText.getText().toString().trim();
            String instagram = instagramEditText.getText().toString().trim();
            String facebook = facebookEditText.getText().toString().trim();

            // Input validation (optional)
            if (name.isEmpty() && phoneNum.isEmpty() && bio.isEmpty() && instagram.isEmpty() && facebook.isEmpty()) {
                Toast.makeText(this, "At least one field must be filled to update profile", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the existing User object
            if (!name.isEmpty()) user.setName(name);
            if (!phoneNum.isEmpty()) user.setPhoneNumber(phoneNum);
            if (!bio.isEmpty()) user.setBio(bio);
            if (!instagram.isEmpty()) user.setInstagram(instagram);
            if (!facebook.isEmpty()) user.setFacebook(facebook); // Assuming YouTube maps to Facebook field

            // Call backend service to update the profile
            userController.userEditProfile(user, new DataFetchCallback<User>() {
                @Override
                public void onSuccess(List<User> updatedUsers) {
                    // Ensure the list is not empty
                    if (updatedUsers != null && !updatedUsers.isEmpty()) {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, return to the previous screen
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Profile update failed: No user data returned.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Set return button click listener
        returnButton.setOnClickListener(v -> {
            // Return to the previous activity
            finish();
        });
    }
}