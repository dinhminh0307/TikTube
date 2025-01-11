package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;

public class ServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        LinearLayout expandButtonContainer = findViewById(R.id.accountContainer);
        LinearLayout expandableSection = findViewById(R.id.accountSection);

        expandButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableSection.getVisibility() == View.GONE) {
                    expandableSection.setVisibility(View.VISIBLE);
                } else {
                    expandableSection.setVisibility(View.GONE);
                }
            }
        });
    }

}