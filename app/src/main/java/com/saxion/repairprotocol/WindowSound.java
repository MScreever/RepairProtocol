package com.saxion.repairprotocol;

import static com.saxion.repairprotocol.ScreenDecision.Screen.firstStep;
import static com.saxion.repairprotocol.ScreenDecision.Screen.stepData;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.saxion.repairprotocol.Dialogs.Dialogs;
import com.saxion.repairprotocol.ScreenDecision.Screen;

import java.util.Objects;

public class WindowSound extends AppCompatActivity
{
    private SoundPool soundPool;
    private int goodSound;
    private int badSound;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        int visibilityButtonPreviousStep = (firstStep) ? ImageButton.INVISIBLE : ImageButton.VISIBLE;
        String goodSoundPath = getFilesDir().toString() + "/tempResources/" + stepData[6].toString();
        String badSoundPath = getFilesDir().toString() + "/tempResources/" + stepData[7].toString();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_sound);

        // Initialization
        TextView txtShortDescription = findViewById(R.id.SOUNDS_shortDescription);
        TextView txtLongDescription = findViewById(R.id.SOUNDS_longDescription);
        ImageButton btnPlayGood = findViewById(R.id.SOUNDS_playGood);
        ImageButton btnPlayBad = findViewById(R.id.SOUNDS_playBad);
        ImageButton btnNextStep = findViewById(R.id.SOUNDS_nextStep);
        ImageButton btnPrevStep = findViewById(R.id.SOUNDS_prevStep);
        ImageButton btnHome = findViewById(R.id.SOUNDS_home);

        // Set attributes
        txtShortDescription.setText(Objects.toString(stepData[1], ""));
        txtLongDescription.setText(Objects.toString(stepData[2], ""));
        btnPrevStep.setVisibility(visibilityButtonPreviousStep);

        // Setup SoundPool
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        goodSound = soundPool.load(goodSoundPath, 1);
        badSound = soundPool.load(badSoundPath, 1);

        // OnClickListeners
        btnPlayGood.setOnClickListener(e -> soundPool.play(goodSound, 1, 1, 1, 0, 1));
        btnPlayBad.setOnClickListener(e -> soundPool.play(badSound, 1, 1, 1, 0, 1));
        btnNextStep.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                soundPool.release();

                Screen.openNext(WindowSound.this);
            }
        });
        btnPrevStep.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                soundPool.release();

                Screen.openPrevious(WindowSound.this);
            }
        });

        btnHome.setOnClickListener(e -> Dialogs.showQuitDialog(WindowSound.this));
    }
}