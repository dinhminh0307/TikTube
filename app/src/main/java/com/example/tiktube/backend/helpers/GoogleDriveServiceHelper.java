package com.example.tiktube.backend.helpers;

import android.content.Context;
import android.util.Log;

import com.example.tiktube.R;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.InputStream;
import java.util.Collections;

public class GoogleDriveServiceHelper {

    private static final String TAG = "GoogleDriveServiceHelper";
    private static Drive driveService;

    /**
     * Get Google Drive service instance using a service account.
     *
     * @param context The application context.
     * @return The Drive service instance.
     */
    public static Drive getDriveService(Context context) {
        if (driveService == null) {
            try {
                // Load the service account JSON key file from res/raw
                InputStream serviceAccountStream = context.getResources().openRawResource(R.raw.service_account);

                // Create GoogleCredential with proper token lifetime
                GoogleCredential credential = GoogleCredential
                        .fromStream(serviceAccountStream)
                        .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive"))
                        .setExpiresInSeconds(3600L); // Set token to expire in 60 minutes

                driveService = new Drive.Builder(
                        new com.google.api.client.http.javanet.NetHttpTransport(),
                        com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("TikTube").build();
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize Google Drive service", e);
            }
        }
        return driveService;
    }

    /**
     * Upload a file to Google Drive and make it public.
     *
     * @param file     The file to upload.
     * @param folderId The ID of the target folder in Google Drive.
     * @return The public link to the uploaded file.
     */
    public static String uploadFile(java.io.File file, String folderId) {
        try {
            // Metadata for the file
            File metadata = new File();
            metadata.setName(file.getName());
            metadata.setParents(Collections.singletonList(folderId));

            // File content to upload
            com.google.api.client.http.FileContent mediaContent = new com.google.api.client.http.FileContent("video/mp4", file);

            // Upload the file to Google Drive
            File uploadedFile = driveService.files().create(metadata, mediaContent)
                    .setFields("id, webViewLink")
                    .execute();

            // Make the file public
            Permission permission = new Permission()
                    .setType("anyone")
                    .setRole("reader");
            driveService.permissions().create(uploadedFile.getId(), permission).execute();

            // Return the public link to the uploaded file
            return "https://drive.google.com/uc?id=" + uploadedFile.getId() + "&export=download";
        } catch (Exception e) {
            Log.e(TAG, "Error uploading file to Google Drive", e);
            return null;
        }
    }
}
