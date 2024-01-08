package com.saxion.repairprotocol;

import static com.saxion.repairprotocol.MainActivity.textToSpeech;
import static com.saxion.repairprotocol.ScreenDecision.Screen.firstStep;
import static com.saxion.repairprotocol.ScreenDecision.Screen.stepData;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.saxion.repairprotocol.Dialogs.Dialogs;
import com.saxion.repairprotocol.ScreenDecision.Screen;

import java.util.Objects;

public class WindowText extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        int visibilityButtonPreviousStep = (firstStep) ? ImageButton.INVISIBLE : ImageButton.VISIBLE;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_text);

        // Initialization
        TextView txtShortDescription = findViewById(R.id.TEXT_shortDescription);
        TextView txtLongDescription = findViewById(R.id.TEXT_longDescription);
        ImageButton btnNextStep = findViewById(R.id.TEXT_nextStep);
        ImageButton btnPrevStep = findViewById(R.id.TEXT_prevStep);
        ImageButton btnHome = findViewById(R.id.TEXT_home);
        ImageButton btnTxt2Speech = findViewById(R.id.TEXT_txt2speech);

        // Set attributes
        txtShortDescription.setText(Objects.toString(stepData[1], ""));
        txtLongDescription.setText(Objects.toString(stepData[2], ""));
        btnPrevStep.setVisibility(visibilityButtonPreviousStep);

        // OnClickListeners
        btnNextStep.setOnClickListener(e -> Screen.openNext(WindowText.this));
        btnPrevStep.setOnClickListener(e -> Screen.openPrevious(WindowText.this));
        btnHome.setOnClickListener(e -> Dialogs.showQuitDialog(WindowText.this));
        btnTxt2Speech.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textToSpeech.speak(txtLongDescription.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
    }
}