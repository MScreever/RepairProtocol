package com.saxion.repairprotocol;

import static com.saxion.repairprotocol.ScreenDecision.Screen.stepOrder;
import static com.saxion.repairprotocol.SettingsActivity.SERVERPATH;
import static com.saxion.repairprotocol.SettingsActivity.SERVERUSERNAME;
import static com.saxion.repairprotocol.SettingsActivity.SERVERPASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saxion.repairprotocol.FileSearch.ReadDIR;
import com.saxion.repairprotocol.FileSearch.SearchDIR;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
{
    public static final int BUFFERSIZE = 256 * 1024; // Buffer size [Kb]
    public static File TEMPRESOURCEDIRECTORY;

    public static String chosenDirectory;
    public static String chosenSubDirectory;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareVariables();

        Thread searchBrands = new Thread(new SearchDIR(MainActivity.this, "smb://" + SERVERPATH));
        searchBrands.start();

        // Initialization
        ImageButton btnSettings = findViewById(R.id.buttonSettings);
        ListView listView = findViewById(R.id.listView);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ImageButton btnBack = findViewById(R.id.buttonBack);

        // Set attributes
        ((TextView) findViewById(R.id.mainTitle)).setText("Selecteer het stofzuiger merk");
        btnSettings.setVisibility(ImageButton.VISIBLE);
        listView.setVisibility(ListView.INVISIBLE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        btnBack.setVisibility(Button.INVISIBLE);

        // Action listeners
        btnSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (chosenDirectory == null)
                {
                    chosenDirectory = parent.getItemAtPosition(position).toString();
                    ((TextView) findViewById(R.id.mainTitle)).setText("Selecteer het model van " + chosenDirectory);

                    SearchDIR searchSubDirectories = new SearchDIR(MainActivity.this, ("smb://" + SERVERPATH + chosenDirectory + "/"));
                    Thread searchModels = new Thread(searchSubDirectories);
                    searchModels.start();

                    // Indicate "Indexing models"
                    listView.setVisibility(ListView.INVISIBLE);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                } else
                {
                    chosenSubDirectory = parent.getItemAtPosition(position).toString();

                    Thread readData = new Thread(new ReadDIR(MainActivity.this, getFilesDir(), "smb://" + SERVERPATH + chosenDirectory + "/" + chosenSubDirectory + "/"));
                    readData.start();

                    // Indicate "Starting protocol"
                    ((TextView) findViewById(R.id.mainTitle)).setText("Stappenplan opstarten...");
                    btnBack.setVisibility(Button.INVISIBLE);
                    listView.setVisibility(ListView.INVISIBLE);
                    btnBack.setVisibility(Button.INVISIBLE);
                    btnSettings.setVisibility(ImageButton.INVISIBLE);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Reset variables
                chosenDirectory = null;
                chosenSubDirectory = null;

                ((TextView) findViewById(R.id.mainTitle)).setText("Selecteer het stofzuiger merk");
                btnBack.setVisibility(ImageButton.INVISIBLE);

                Thread searchBrands = new Thread(new SearchDIR(MainActivity.this, SERVERPATH));
                searchBrands.start();

                // Indicate "Indexing brands"
                listView.setVisibility(ListView.INVISIBLE);
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
    }

    private void prepareVariables()
    {
        readJSON();

        TEMPRESOURCEDIRECTORY = new File(getFilesDir().toString() + "/tempResources/");
        chosenDirectory = null;
        chosenSubDirectory = null;
        stepOrder = null;
        
    }

    private void readJSON()
    {
        try
        {
            // Read file from internal directory
            File settingsFile = new File(getFilesDir(), "settings.json");

            if (settingsFile.exists())
            {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(settingsFile));

                byte[] buffer = new byte[BUFFERSIZE];
                inputStream.read(buffer);
                inputStream.close();

                String jsonString = new String(buffer, "UTF-8");
                JSONObject jsonObject = new JSONObject(jsonString);

                SERVERPATH = jsonObject.getString("ServerPath");
                SERVERUSERNAME = jsonObject.getString("ServerUserName");
                SERVERPASSWORD = jsonObject.getString("ServerPassWord");
            } else
            {
                createInternalFile();

                // Open settings window to set server data
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
        }
        catch (IOException | JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    private void createInternalFile()
    {
        try
        {
            InputStream inputStream = getAssets().open("settings.json");
            File outputFile = new File(getFilesDir(), "settings.json");

            {
                OutputStream outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[BUFFERSIZE];
                int length;

                while ((length = inputStream.read(buffer)) > 0)
                {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


}