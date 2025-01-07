package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.helpers.GoogleDriveServiceHelper;
import com.example.tiktube.backend.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private UserController userController;
    private LoginController loginController;
    private User user;

    private ImageView profileImageView;

    EditText nameEditText;
    EditText phoneNumEditText;
    EditText bioEditText;
    EditText instagramEditText;
    EditText facebookEditText;

    Button saveButton;
    Button returnButton;

    private Uri selectedImageUri;
    private String driveFolderId = "1zH_RJTSrwGNgS0DIXk0rAqoCLESbcITW";

    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        userController = new UserController();
        loginController = new LoginController();

        requestStoragePermissions();

        initComponent();

        // Initialize Google Drive service
        GoogleDriveServiceHelper.getDriveService(this);

        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User currentUser) {
                user = currentUser;
                populateUserFields(user);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failed to fetch user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        editUserProfile();
    }


    private void requestStoragePermissions() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }


    private void initComponent() {
        nameEditText = findViewById(R.id.nameEditText);
        phoneNumEditText = findViewById(R.id.phoneNumEditText);
        bioEditText = findViewById(R.id.bioEditText);
        instagramEditText = findViewById(R.id.instagramEditText);
        facebookEditText = findViewById(R.id.facebookEditText);
        saveButton = findViewById(R.id.saveButton);
        returnButton = findViewById(R.id.returnButton);
        profileImageView = findViewById(R.id.profileImageView);

        profileImageView.setOnClickListener(v -> openFilePicker());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
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

            String name = nameEditText.getText().toString().trim();
            String phoneNum = phoneNumEditText.getText().toString().trim();
            String bio = bioEditText.getText().toString().trim();
            String instagram = instagramEditText.getText().toString().trim();
            String facebook = facebookEditText.getText().toString().trim();

            if (!name.isEmpty()) user.setName(name);
            if (!phoneNum.isEmpty()) user.setPhoneNumber(phoneNum);
            if (!bio.isEmpty()) user.setBio(bio);
            if (!instagram.isEmpty()) user.setInstagram(instagram);
            if (!facebook.isEmpty()) user.setFacebook(facebook);

            if (selectedImageUri != null) {
                try {
                    File imageFile = createTempFileFromUri(selectedImageUri);

                    new Thread(() -> {
                        String driveUrl = GoogleDriveServiceHelper.uploadImageFile(EditProfileActivity.this, imageFile, driveFolderId);
                        if (driveUrl != null) {
                            user.setImageUrl(driveUrl);
                            Log.d("EditProfileActivity", "Drive User URL: " + user.getImageUrl());
                            runOnUiThread(this::updateUserProfile);
                        } else {
                            runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                } catch (IOException e) {
                    Toast.makeText(this, "Error preparing file for upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                updateUserProfile();
            }
        });


        returnButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = new File(getCacheDir(), "upload_temp.jpg");

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return tempFile;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void updateUserProfile() {
        userController.userEditProfile(user, new DataFetchCallback<User>() {
            @Override
            public void onSuccess(List<User> updatedUsers) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedUser", user);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
