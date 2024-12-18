package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;
import com.example.tiktube.backend.login.LoginController;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.users.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class VideoPageActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int REQUEST_CODE_SIGN_IN = 2;

    private Uri videoUri;
    private Drive googleDriveService;
    private GoogleSignInClient googleSignInClient;

    private UserService userService;

    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        userService = new UserService();
        loginController = new LoginController();
        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/drive.file"))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listener for upload icon
        ImageView uploadIcon = findViewById(R.id.uploadIcon);
        uploadIcon.setOnClickListener(view -> signInToGoogleDrive());
    }

    // Step 1: Start Google Sign-In
    private void signInToGoogleDrive() {
        startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            handleSignInResult(data);
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            if (videoUri != null) {
                Toast.makeText(this, "Uploading video...", Toast.LENGTH_SHORT).show();
                uploadVideoToGoogleDrive();
            }
        }
    }

    // Step 2: Handle Google Sign-In result
    private void handleSignInResult(Intent data) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    this, Collections.singleton("https://www.googleapis.com/auth/drive.file"));
            credential.setSelectedAccount(account.getAccount());

            googleDriveService = new Drive.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(), // Use this instead of newTrustedTransport
                    GsonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Google Drive Upload")
                    .build();

            openFilePicker();
        } catch (Exception e) {
            Log.e("GoogleDrive", "Sign-in failed", e);
            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
        }
    }


    // Step 3: Open File Picker to Select Video
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    // Step 4: Upload Video to Google Drive
    private void uploadVideoToGoogleDrive() {
        new Thread(() -> {
            try {
                // Open input stream for the selected video
                InputStream inputStream = getContentResolver().openInputStream(videoUri);

                // Set metadata for the file
                File metadata = new File();
                metadata.setName("uploaded_video.mp4");

                // Create the upload content
                com.google.api.client.http.InputStreamContent mediaContent =
                        new com.google.api.client.http.InputStreamContent("video/mp4", inputStream);

                // Upload the file
                File uploadedFile = googleDriveService.files().create(metadata, mediaContent)
                        .setFields("id, webViewLink")
                        .execute();

                // Make the file public
                String fileId = uploadedFile.getId();
                makeFilePublic(fileId);

                // Retrieve the public link
                String fileLink = uploadedFile.getWebViewLink();
                runOnUiThread(() -> {
                    uploadVideoToFirebase(fileLink);
                    Toast.makeText(this, "Upload Successful! Public Link: " + fileLink, Toast.LENGTH_LONG).show();
                    Log.d("GoogleDrive", "Public Link: " + fileLink);

                });

            } catch (Exception e) {

                Log.e("GoogleDrive", "Error uploading file", e);
                runOnUiThread(() -> Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void uploadVideoToFirebase(String link) {
        Video vid = new Video("hello", link, loginController.getUserUID(), "12h", new ArrayList<String>(), new ArrayList<String>());
        userService.uploadVideo(vid);
    }
    private void makeFilePublic(String fileId) {
        new Thread(() -> {
            try {
                // Create a permission object
                Permission permission = new Permission();
                permission.setType("anyone"); // Allow access to anyone
                permission.setRole("reader"); // Role: viewer (read-only)

                // Add the permission to the file
                googleDriveService.permissions().create(fileId, permission).execute();

                runOnUiThread(() -> Toast.makeText(this, "File is now public!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to make file public", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
