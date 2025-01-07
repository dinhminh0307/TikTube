package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.User;

import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private UserController userController;
    private LoginController loginController;
    private User user;

    private ImageView profileImageView;

    EditText nameEditText ;
    EditText phoneNumEditText ;
    EditText bioEditText ;
    EditText instagramEditText ;
    EditText facebookEditText ;

    Button saveButton;
    Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Initialize controllers
        userController = new UserController();
        loginController = new LoginController();

        initComponent();

        // Fetch current user
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User currentUser) {
                user = currentUser; // Set the current user
                populateUserFields(user); // Populate UI fields
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failed to fetch user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        editUserProfile();
    }

    private void initComponent() {
        nameEditText = findViewById(R.id.nameEditText);
         phoneNumEditText = findViewById(R.id.phoneNumEditText);
         bioEditText = findViewById(R.id.bioEditText);
         instagramEditText = findViewById(R.id.instagramEditText);
         facebookEditText = findViewById(R.id.facebookEditText);
         saveButton = findViewById(R.id.saveButton);
         returnButton = findViewById(R.id.returnButton);

    }

    private void populateUserFields(User user) {

        nameEditText.setText(user.getName());
        phoneNumEditText.setText(user.getPhoneNumber());
        bioEditText.setText(user.getBio());
        instagramEditText.setText(user.getInstagram());
        facebookEditText.setText(user.getFacebook());
    }

    private void editUserProfile() {

        saveButton.setOnClickListener(v -> {
            if (user == null) {
                user = new User();
            }

            // Get user inputs
            String name = nameEditText.getText().toString().trim();
            String phoneNum = phoneNumEditText.getText().toString().trim();
            String bio = bioEditText.getText().toString().trim();
            String instagram = instagramEditText.getText().toString().trim();
            String facebook = facebookEditText.getText().toString().trim();

            // Update fields only if non-empty
            if (!name.isEmpty()) user.setName(name);
            if (!phoneNum.isEmpty()) user.setPhoneNumber(phoneNum);
            if (!bio.isEmpty()) user.setBio(bio);
            if (!instagram.isEmpty()) user.setInstagram(instagram);
            if (!facebook.isEmpty()) user.setFacebook(facebook);

            userController.userEditProfile(user, new DataFetchCallback<User>() {
                @Override
                public void onSuccess(List<User> updatedUsers) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // Pass updated user back to ProfileActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedUser", user);
                    setResult(RESULT_OK, resultIntent);

                    finish(); // Close EditProfileActivity
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        returnButton.setOnClickListener(v -> {
            // Close the activity without saving changes
            setResult(RESULT_CANCELED);
            finish();
        });
    }

}
