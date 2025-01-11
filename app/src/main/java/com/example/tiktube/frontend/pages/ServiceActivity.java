package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;

public class ServiceActivity extends AppCompatActivity {

    LinearLayout backToPrevious, expandButtonContainer, expandableSection, videoContainer, videoSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        initializeViews();
        setUpExpandableSections();
        setUpBackNavigation();
    }

    private void initializeViews() {
        backToPrevious = findViewById(R.id.backToPrevious);
        expandButtonContainer = findViewById(R.id.accountContainer);
        expandableSection = findViewById(R.id.accountSection);
        videoContainer = findViewById(R.id.videoContainer);
        videoSection = findViewById(R.id.videoSection);
    }

    private void setUpExpandableSections() {
        expandButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSectionToggle(expandableSection);
            }
        });

        videoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSectionToggle(videoSection);
            }
        });
    }

    private void handleSectionToggle(LinearLayout sectionToToggle) {
        // Check if the section is already visible
        if (sectionToToggle.getVisibility() == View.VISIBLE) {
            // If the section is visible, hide it
            sectionToToggle.setVisibility(View.GONE);
        } else {
            // If the section is not visible, collapse all sections first
            collapseAllSections();
            // Then show the selected section
            sectionToToggle.setVisibility(View.VISIBLE);
        }
    }

    private void collapseAllSections() {
        expandableSection.setVisibility(View.GONE);
        videoSection.setVisibility(View.GONE);
    }

    private void setUpBackNavigation() {
        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
