package com.saxion.repairprotocol;

import static com.saxion.repairprotocol.ScreenDecision.Screen.firstStep;
import static com.saxion.repairprotocol.ScreenDecision.Screen.stepData;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.saxion.repairprotocol.Dialogs.Dialogs;
import com.saxion.repairprotocol.ScreenDecision.Screen;

import java.util.Objects;

public class WindowVideo extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        int visibilityButtonPreviousStep = (firstStep) ? ImageButton.INVISIBLE : ImageButton.VISIBLE;
        String videoPath = getFilesDir().toString() + "/tempResources/" + stepData[8].toString().toLowerCase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_video);

        // Initialization
        TextView txtShortDescription = findViewById(R.id.VIDEO_shortDescription);
        TextView txtLongDescription = findViewById(R.id.VIDEO_longDescription);
        ImageButton btnNextStep = findViewById(R.id.VIDEO_nextStep);
        ImageButton btnPrevStep = findViewById(R.id.VIDEO_prevStep);
        ImageButton btnHome = findViewById(R.id.VIDEO_home);
        VideoView videoView = findViewById(R.id.VIDEO_videoView);

        // Set attributes
        txtShortDescription.setText(Objects.toString(stepData[1], ""));
        txtLongDescription.setText(Objects.toString(stepData[2], ""));
        btnPrevStep.setVisibility(visibilityButtonPreviousStep);

        // Set video in VideoView
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse(videoPath);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);

        // Wait 1 second before playing video
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        videoView.start();

        // OnClickListeners
        btnNextStep.setOnClickListener(e -> Screen.openNext(WindowVideo.this));
        btnPrevStep.setOnClickListener(e -> Screen.openPrevious(WindowVideo.this));
        btnHome.setOnClickListener(e-> Dialogs.showQuitDialog(WindowVideo.this));
    }
}