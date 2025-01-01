package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tiktube.R;

public class NotificationActivity extends AppCompatActivity {
    private LinearLayout emptyState;
    private LinearLayout notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        emptyState = findViewById(R.id.emptyState);
        notificationList = findViewById(R.id.notificationList);

        // Example: Add notifications dynamically
        addNotification("You have a new follower!");
        addNotification("Your video reached 1K views!");
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
}