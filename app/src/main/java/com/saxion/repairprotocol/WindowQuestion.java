package com.saxion.repairprotocol;

import static com.saxion.repairprotocol.ScreenDecision.Screen.firstStep;
import static com.saxion.repairprotocol.ScreenDecision.Screen.stepData;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.saxion.repairprotocol.Dialogs.Dialogs;
import com.saxion.repairprotocol.ScreenDecision.Screen;

import java.util.Objects;

public class WindowQuestion extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        int visibilityButtonPreviousStep = (firstStep) ? ImageButton.INVISIBLE : ImageButton.VISIBLE;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_question);

        // Initialization
        TextView txtQuestion = findViewById(R.id.QUESTION_questionText);
        ImageButton btnPrevStep = findViewById(R.id.QUESTION_prevStep);
        ImageButton btnHome = findViewById(R.id.QUESTION_home);
        Button btnYes = findViewById(R.id.QUESTION_yes);
        Button btnNo = findViewById(R.id.QUESTION_no);

        // Set attributes
        txtQuestion.setText(Objects.toString(stepData[1], ""));
        btnPrevStep.setVisibility(visibilityButtonPreviousStep);

        // OnClickListeners
        btnYes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Screen.openYes(WindowQuestion.this);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Screen.openNo(WindowQuestion.this);
            }
        });
        btnPrevStep.setOnClickListener(e -> Screen.openPrevious(WindowQuestion.this));
        btnHome.setOnClickListener(e -> Dialogs.showQuitDialog(WindowQuestion.this));

    }
}