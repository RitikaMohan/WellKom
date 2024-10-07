package com.example.wellkom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class ApprovalActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_WellKom);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approval_activity);

            VideoView videoView = (VideoView) findViewById(R.id.videoView);
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.aftereffects);
            videoView.start();

        TextView visitorId = (TextView) (findViewById(R.id.visitorId));
        Intent intent = getIntent();
        String userId = intent.getStringExtra("Visitor Id: ");
        visitorId.setText(userId);
        }
    }



