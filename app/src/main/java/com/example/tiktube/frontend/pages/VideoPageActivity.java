package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.utils.UidGenerator;
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
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.media.MediaExtractor;

import java.io.InputStream;
import java.nio.ByteBuffer;
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

    User currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        userController = new UserController();
        loginController = new LoginController();

        currentUser = getIntent().getParcelableExtra("user");

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

    @Override
    protected void onResume() {
        super.onResume();
        fetchCurrentUser();
    }

    // Fetch the current user from the database or server
    private void fetchCurrentUser() {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.setUser(user); // Update the current user object
                Log.d("VideoPageActivity", "Current user updated: " + currentUser.getName());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("VideoPageActivity", "Failed to fetch current user: " + e.getMessage());
            }
        });
    }

    public void updateVideo(Video updatedVideo) {
        int position = videoDataList.indexOf(updatedVideo);
        if (position != -1) {
            videoDataList.set(position, updatedVideo);
            videoPagerAdapter.notifyItemChanged(position);
        }
    }


    private void onProfileImageClicked() {
        profileIcon = findViewById(R.id.profileIcon);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoPageActivity.this, ProfileActivity.class);
                intent.putExtra("user", currentUser);
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

                // Retrieve video duration
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(this, videoUri);
                String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long duration = Long.parseLong(durationStr);

                // Check if video duration exceeds 1 minute
                Uri videoToUploadUri = videoUri;
                if (duration > 60000) { // 1 minute = 60000 ms
                    videoToUploadUri = trimVideo(videoUri);
                }

                File metadata = new File(); // Google Drive File object
                metadata.setName("uploaded_video.mp4");

                com.google.api.client.http.InputStreamContent mediaContent =
                        new com.google.api.client.http.InputStreamContent("video/mp4", getContentResolver().openInputStream(videoToUploadUri));

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

    private Uri trimVideo(Uri sourceUri) throws Exception {
        // Resolve the file path from Uri
        String sourcePath = getPathFromUri(sourceUri);

        // Validate and log the source path
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid source path for the video");
        }
        Log.d("VideoPageActivity", "Source path: " + sourcePath);

        // Output path for the trimmed video
        java.io.File outputDir = getCacheDir(); // Use the app's cache directory
        java.io.File outputFile = java.io.File.createTempFile("trimmed_video", ".mp4", outputDir);
        String outputPath = outputFile.getAbsolutePath();

        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(sourcePath); // This line causes the exception

        MediaMuxer muxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        int videoTrackIndex = -1;
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            if (extractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                videoTrackIndex = muxer.addTrack(extractor.getTrackFormat(i));
                extractor.selectTrack(i);
                break;
            }
        }

        if (videoTrackIndex == -1) {
            throw new IllegalArgumentException("No video track found in the provided file.");
        }

        muxer.start();
        long startUs = 0; // Start at the beginning
        long endUs = 120000000; // End at 1 minute (60000000 microseconds)
        extractor.seekTo(startUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // Allocate a buffer of 1 MB

        while (true) {
            int sampleSize = extractor.readSampleData(buffer, 0);
            if (sampleSize < 0 || extractor.getSampleTime() >= endUs) {
                break; // End of stream or beyond the trimming range
            }

            bufferInfo.offset = 0;
            bufferInfo.size = sampleSize;
            bufferInfo.presentationTimeUs = extractor.getSampleTime();

            // Translate MediaExtractor flags to MediaCodec.BufferInfo flags
            int sampleFlags = extractor.getSampleFlags();
            bufferInfo.flags = 0; // Default to no flags
            if ((sampleFlags & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                bufferInfo.flags |= MediaCodec.BUFFER_FLAG_SYNC_FRAME;
            }

            muxer.writeSampleData(videoTrackIndex, buffer, bufferInfo);
            extractor.advance();
        }

        muxer.stop();
        muxer.release();
        extractor.release();

        return Uri.fromFile(outputFile); // Return trimmed video URI
    }



    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            if (cursor.moveToFirst()) {
                String path = cursor.getString(columnIndex);
                cursor.close();
                if (path != null && !path.isEmpty()) {
                    return path;
                }
            }
            cursor.close();
        }

        // Fallback: Copy the content to a temporary file
        return copyUriToTempFile(uri).getAbsolutePath();
    }


    private java.io.File copyUriToTempFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            java.io.File tempFile = java.io.File.createTempFile("temp_video", ".mp4", getCacheDir());
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy URI to temporary file: " + e.getMessage(), e);
        }
    }





    private void uploadVideoToFirebase(String link) {
        Video vid = new Video(UidGenerator.generateUID(), "hello", link, loginController.getUserUID(), "12h", new ArrayList<>(), new ArrayList<>());
        userController.uploadVideo(vid, new DataFetchCallback<Void>() {
            @Override
            public void onSuccess(List<Void> data) {
                fetchCurrentUser();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
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
        fetchAllVideo();
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
