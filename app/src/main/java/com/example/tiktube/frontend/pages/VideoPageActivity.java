package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tiktube.MainActivity;
import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.login.LoginController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.users.UserController;
import com.example.tiktube.frontend.adapters.VideoPagerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoPageActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int REQUEST_CODE_SIGN_IN = 2;

    private Uri videoUri;
    private Drive googleDriveService;
    private GoogleSignInClient googleSignInClient;

    private UserController userController;
    private LoginController loginController;

    private ViewPager2 viewPager2;
    private VideoPagerAdapter videoPagerAdapter;

    private List<Video> videoDataList = new ArrayList<>();

    private ImageView profileIcon;

    User user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        userController = new UserController();
        loginController = new LoginController();
        user = getIntent().getParcelableExtra("user");
        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/drive.file"))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up ViewPager2
        viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL); // Enable vertical scrolling

        // Set click listener for upload icon
        ImageView uploadIcon = findViewById(R.id.uploadIcon);
        uploadIcon.setOnClickListener(view -> signInToGoogleDrive());

        fetchAllVideo();

        //setup component button
        onProfileImageClicked();
    }

    private void onProfileImageClicked() {
        profileIcon = findViewById(R.id.profileIcon);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoPageActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
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
                    new com.google.api.client.http.javanet.NetHttpTransport(),
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
                InputStream inputStream = getContentResolver().openInputStream(videoUri);

                File metadata = new File();
                metadata.setName("uploaded_video.mp4");

                com.google.api.client.http.InputStreamContent mediaContent =
                        new com.google.api.client.http.InputStreamContent("video/mp4", inputStream);

                File uploadedFile = googleDriveService.files().create(metadata, mediaContent)
                        .setFields("id, webViewLink")
                        .execute();

                String fileId = uploadedFile.getId();
                String fileLink = "https://drive.google.com/uc?id=" + fileId + "&export=download";
                makeFilePublic(fileId);

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
        Video vid = new Video("hello", link, loginController.getUserUID(), "12h", new ArrayList<>(), new ArrayList<>());
        userController.uploadVideo(vid);
    }

    private void makeFilePublic(String fileId) {
        new Thread(() -> {
            try {
                Permission permission = new Permission();
                permission.setType("anyone");
                permission.setRole("reader");
                googleDriveService.permissions().create(fileId, permission).execute();

                runOnUiThread(() -> Toast.makeText(this, "File is now public!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to make file public", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager2 = null;
        videoPagerAdapter = null;
        googleDriveService = null;
        Log.d("VideoPageActivity", "Activity destroyed");
    }

    // Fetch all videos and set up adapter
    private void fetchAllVideo() {
        userController.getAllVideo(new DataFetchCallback<Video>() {
            @Override
            public void onSuccess(List<Video> data) {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        if (data != null && !data.isEmpty()) {
                            videoDataList.clear();
                            videoDataList.addAll(data);

                            // Set up adapter
                            if (videoPagerAdapter == null) {
                                videoPagerAdapter = new VideoPagerAdapter(VideoPageActivity.this, videoDataList);
                                viewPager2.setAdapter(videoPagerAdapter);
                            } else {
                                videoPagerAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("VideoPageActivity", "No videos found");
                            Toast.makeText(VideoPageActivity.this, "No videos available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        Log.d("VideoPageActivity", "Failed to fetch videos: " + e.getMessage());
                        Toast.makeText(VideoPageActivity.this, "Failed to load videos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
