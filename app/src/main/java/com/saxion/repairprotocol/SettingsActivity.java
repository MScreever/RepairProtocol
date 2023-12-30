package com.saxion.repairprotocol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity
{
    public static String SERVERPATH;
    public static String SERVERUSERNAME;
    public static String SERVERPASSWORD;

    private EditText txtServerURL;
    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtServerURL = findViewById(R.id.txtServerPath);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        Button btnSave = findViewById(R.id.buttonSave);

        txtServerURL.setText(SERVERPATH);
        txtUsername.setText(SERVERUSERNAME);
        txtPassword.setText(SERVERPASSWORD);

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SERVERPATH = txtServerURL.getText().toString();
                SERVERUSERNAME = txtUsername.getText().toString();
                SERVERPASSWORD = txtPassword.getText().toString();

                writeJSON();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {

    }

    private void writeJSON()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ServerPath", SERVERPATH);
            jsonObject.put("ServerUserName", SERVERUSERNAME);
            jsonObject.put("ServerPassWord", SERVERPASSWORD);

            String jsonString = jsonObject.toString();

            FileOutputStream fileOutputStream = openFileOutput("settings.json", Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
        }
        catch (JSONException | IOException ex)
        {
            ex.printStackTrace();
        }
    }
}