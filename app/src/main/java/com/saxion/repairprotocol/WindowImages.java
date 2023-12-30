package com.saxion.repairprotocol;

import static com.saxion.repairprotocol.MainActivity.TEMPRESOURCEDIRECTORY;
import static com.saxion.repairprotocol.ScreenDecision.Screen.firstStep;
import static com.saxion.repairprotocol.ScreenDecision.Screen.stepData;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.saxion.repairprotocol.Dialogs.Dialogs;
import com.saxion.repairprotocol.ScreenDecision.Screen;

import java.io.File;
import java.util.Objects;

public class WindowImages extends AppCompatActivity
{
    private ImageButton btnPrevImage;
    private ImageButton btnNextImage;
    private int imageIndex;
    private String[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        images = new String[stepData.length - 8];
        for (int i = 0; i < images.length; i++)
        {
            images[i] = "/" + stepData[8 + i].toString().toLowerCase();
        }


        int visibilityButtonPreviousStep = (firstStep) ? ImageButton.INVISIBLE : ImageButton.VISIBLE;
        int visibilityButtonNextImage = (images.length > 1) ? ImageButton.VISIBLE : ImageButton.INVISIBLE;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_images);

        // Initialization
        TextView txtShortDescription = findViewById(R.id.IMAGES_shortDescription);
        TextView txtLongDescription = findViewById(R.id.IMAGES_longDescription);
        ImageButton btnNextStep = findViewById(R.id.IMAGES_nextStep);
        ImageButton btnPrevStep = findViewById(R.id.IMAGES_prevStep);
        ImageButton btnHome = findViewById(R.id.IMAGES_home);
        btnPrevImage = findViewById(R.id.IMAGES_prevImage);
        btnNextImage = findViewById(R.id.IMAGES_nextImage);
        ImageView image = findViewById(R.id.IMAGES_imageView);

        // Set attributes
        txtShortDescription.setText(Objects.toString(stepData[1], ""));
        txtLongDescription.setText(Objects.toString(stepData[2], ""));
        btnPrevStep.setVisibility(visibilityButtonPreviousStep);
        btnPrevImage.setVisibility(ImageButton.INVISIBLE);
        btnNextImage.setVisibility(visibilityButtonNextImage);
        image.setImageBitmap(BitmapFactory.decodeFile(TEMPRESOURCEDIRECTORY.getPath() + images[imageIndex]));

        // OnClickListeners
        btnPrevImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (imageIndex > 0)
                {
                    imageIndex--;
                    imageButtonVisibility();
                }

                image.setImageBitmap(BitmapFactory.decodeFile(TEMPRESOURCEDIRECTORY.getPath() + images[imageIndex]));
            }
        });
        btnNextImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (imageIndex < images.length - 1)
                {
                    imageIndex++;
                    imageButtonVisibility();
                }

                image.setImageBitmap(BitmapFactory.decodeFile(TEMPRESOURCEDIRECTORY.getPath() + images[imageIndex]));
            }
        });
        btnNextStep.setOnClickListener(e -> Screen.openNext(WindowImages.this));
        btnPrevStep.setOnClickListener(e -> Screen.openPrevious(WindowImages.this));
        btnHome.setOnClickListener(e -> Dialogs.showQuitDialog(WindowImages.this));
    }

    @Override
    public void onBackPressed()
    {
    }

    private void imageButtonVisibility()
    {
        if (imageIndex == 0)
        {
            btnPrevImage.setVisibility(ImageButton.INVISIBLE);
        } else {
            btnPrevImage.setVisibility(ImageButton.VISIBLE);
        }

        if (imageIndex == images.length - 1)
        {
            btnNextImage.setVisibility(ImageButton.INVISIBLE);
        } else
        {
            btnNextImage.setVisibility(ImageButton.VISIBLE);
        }
    }
}