package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.NotificationController;
import com.example.tiktube.backend.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private LinearLayout emptyState;
    private LinearLayout notificationList;

    private ImageView messageIcon;

    private NotificationController notificationController;

    private List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        initComponent();

        fetchAllNotification(); // fetch and display the notification

        onMessageIconClicked();
    }

    private void initComponent() {
        emptyState = findViewById(R.id.emptyState);
        notificationList = findViewById(R.id.notificationList);
        messageIcon = findViewById(R.id.messageIcon);

        notificationController = new NotificationController();
    }

    private void fetchAllNotification() {
        notificationController.getNotifications()
                .thenAccept(fetchedNotifications -> {
                    // Update notifications list
                    notifications.addAll(fetchedNotifications);

                    // Update the UI on the main thread
                    runOnUiThread(() -> {
                        if (notifications.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            notificationList.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            notificationList.setVisibility(View.VISIBLE);

                            for (Notification not : notifications) {
                                addNotification(not.getContent());
                            }
                        }
                    });
                })
                .exceptionally(e -> {
                    Log.e("Notification Activity", "Error fetching notifications", e);
                    return null;
                });
    }


    private void addNotification(String message) {
        emptyState.setVisibility(View.GONE);
        notificationList.setVisibility(View.VISIBLE);

        TextView notification = new TextView(this);
        notification.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        notification.setPadding(16, 16, 16, 16);
        notification.setText(message);
        notification.setTextSize(16f);
        notification.setBackgroundResource(R.drawable.notification_item_background); // Add a custom background if needed

        notificationList.addView(notification);
    }

    private void onMessageIconClicked() {
        messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }
}