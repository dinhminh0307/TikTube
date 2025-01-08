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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.helpers.GoogleDriveServiceHelper;
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
import com.google.api.client.http.FileContent;
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

    private ImageView searchIcon, profileIcon, messagingIcon, shopIcon;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        userController = new UserController();
        loginController = new LoginController();

        currentUser = getIntent().getParcelableExtra("user");
        shopIcon = findViewById(R.id.shopIcon);

        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/drive.file"))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up ViewPager2
        viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL); // Enable vertical scrolling
        // add playback control
        handlePlaybackOnScroll();

        // Set click listener for upload icon
        ImageView uploadIcon = findViewById(R.id.uploadIcon);
        uploadIcon.setOnClickListener(view -> signInToGoogleDrive());

        fetchAllVideo();

        //setup component button
        onProfileImageClicked();

        onNotificationClicked();
        onSearchClicked();

        onShopIconClicked();
    }

    private RecyclerView getRecyclerViewFromViewPager2(ViewPager2 viewPager2) {
        return (RecyclerView) viewPager2.getChildAt(0);
    }

    private void handlePlaybackOnScroll() {
        RecyclerView recyclerView = getRecyclerViewFromViewPager2(viewPager2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Get the current ViewHolder
                RecyclerView.ViewHolder currentHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (currentHolder instanceof VideoPagerAdapter.VideoViewHolder) {
                    VideoPagerAdapter.VideoViewHolder videoHolder = (VideoPagerAdapter.VideoViewHolder) currentHolder;
                    if (videoHolder.videoView != null) {
                        videoHolder.videoView.start(); // Play the current video
                    }
                }

                // Pause all other videos
                for (int i = 0; i < videoPagerAdapter.getItemCount(); i++) {
                    if (i != position) {
                        RecyclerView.ViewHolder otherHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (otherHolder instanceof VideoPagerAdapter.VideoViewHolder) {
                            VideoPagerAdapter.VideoViewHolder videoHolder = (VideoPagerAdapter.VideoViewHolder) otherHolder;
                            if (videoHolder.videoView != null && videoHolder.videoView.isPlaying()) {
                                videoHolder.videoView.pause();
                            }
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // Optional: Additional behavior during scroll state changes
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCurrentUser();
    }

    private void onShopIconClicked() {
        shopIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoPageActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });
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
        openFilePicker(); // Directly call the file picker
    }


    // Step 3: Open File Picker to Select Video
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    // Step 4: Upload Video to Google Drive

    private void uploadVideoToGoogleDrive(String videoDescription) {
        new Thread(() -> {
            try {
                // Initialize the Drive service
                Drive driveService = GoogleDriveServiceHelper.getDriveService(this);

                // Check if video needs to be trimmed
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(this, videoUri);
                String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long duration = Long.parseLong(durationStr);

                Uri uploadUri = videoUri;
                if (duration > 60000) { // Trim video if duration > 1 minute
                    uploadUri = trimVideo(videoUri); // Call the trimming logic
                }

                // Prepare the video file for upload
                java.io.File videoFile = new java.io.File(getPathFromUri(uploadUri));
                String folderId = "1iyMdEMAvwpgR6WrqpVPkYnjEa3M7utcG"; // Target Google Drive folder ID

                // Upload the file
                String fileLink = GoogleDriveServiceHelper.uploadFile(videoFile, folderId);

                if (fileLink != null) {
                    Log.d("GoogleDrive", "Public Link: " + fileLink);

                    // Notify the user
                    runOnUiThread(() -> Toast.makeText(this, "Upload Successful! Public Link: " + fileLink, Toast.LENGTH_LONG).show());

                    // Optionally, upload the video description and link to Firebase
                    uploadVideoToFirebase(fileLink, videoDescription);
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("GoogleDrive", "Error uploading file", e);
                runOnUiThread(() -> Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData(); // Retrieve the selected video URI
            if (videoUri != null) {
                showVideoDetailsDialog();
            } else {
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showVideoDetailsDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_video_details, null);

        // Find views in the dialog
        TextView tvFileName = dialogView.findViewById(R.id.tvFileName);
        EditText etVideoDescription = dialogView.findViewById(R.id.etVideoDescription);
        Button btnUpload = dialogView.findViewById(R.id.btnUpload);

        // Get the file name from the URI
        String fileName = getFileNameFromUri(videoUri);
        tvFileName.setText(fileName);

        // Create and show the dialog
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        dialog.show();

        // Set button click listener
        btnUpload.setOnClickListener(v -> {
            String videoDescription = etVideoDescription.getText().toString().trim();

            if (videoDescription.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass the description to the upload method
            uploadVideoToGoogleDrive(videoDescription);

            // Dismiss the dialog
            dialog.dismiss();
        });
    }

    // Helper method to get file name from URI
    private String getFileNameFromUri(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                String fileName = cursor.getString(columnIndex);
                cursor.close();
                return fileName;
            }
            cursor.close();
        }
        return "Unknown File";
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
        long endUs = 12000000; // End at 12 seconds (12,000,000 microseconds)

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

    private void uploadVideoToFirebase(String link, String description) {
        Video vid = new Video(UidGenerator.generateUID(), description, link, loginController.getUserUID(), "12h", new ArrayList<>(), new ArrayList<>());
        userController.uploadVideo(vid, new DataFetchCallback<Void>() {
            @Override
            public void onSuccess(List<Void> data) {
                fetchCurrentUser();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firebase", "Failed to upload video metadata", e);
            }
        });
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

    private void onNotificationClicked() {
        messagingIcon = findViewById(R.id.messagingIcon);
        messagingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoPageActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onSearchClicked() {
        searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(VideoPageActivity.this, SearchActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });
    }
}
