package com.saxion.repairprotocol.FileSearch;

import static com.saxion.repairprotocol.MainActivity.chosenDirectory;
import static com.saxion.repairprotocol.SettingsActivity.SERVERPASSWORD;
import static com.saxion.repairprotocol.SettingsActivity.SERVERUSERNAME;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.saxion.repairprotocol.MainActivity;
import com.saxion.repairprotocol.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import jcifs.CIFSContext;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;

public class SearchDIR extends Thread
{
    private final WeakReference<MainActivity> activityReference;
    private final String filePath;
    private ArrayList<String> directories;
    private ArrayAdapter<String> listViewAdapter;

    public SearchDIR(MainActivity context, String filePath)
    {
        this.filePath = filePath;
        this.activityReference = new WeakReference<>(context);
    }

    @Override
    public void run()
    {
        directories = new ArrayList<>();
        String directoryName;

        try
        {
            Log.i("Read DIR", "Starting connection to server: " + filePath);

            NtlmPasswordAuthenticator personalAuthentication = new NtlmPasswordAuthenticator(SERVERUSERNAME, SERVERPASSWORD);

            CIFSContext baseContext = new BaseContext(new PropertyConfiguration(System.getProperties()));
            CIFSContext context = baseContext.withCredentials(personalAuthentication);

            SmbFile smbFile = new SmbFile(filePath, context);

            if (smbFile.exists())
            {
                Log.i("Read DIR", "Connection established to: " + filePath);

                smbFile.connect();

                SmbFile[] files = smbFile.listFiles();

                if (files != null)
                {
                    for (SmbFile file : files)
                    {
                        if (file.isDirectory())
                        {
                            directoryName = file.getName();
                            directoryName = directoryName.substring(0, directoryName.length() - 1);

                            directories.add(directoryName);
                        }
                    }
                }

                smbFile.close();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        // Pre-Checks
        MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Set ProgressBar attributes
                activity.findViewById(R.id.progressBar).setVisibility(ProgressBar.INVISIBLE);

                // Set ListView attributes
                Context listViewContext = activity.findViewById(R.id.listView).getContext();
                listViewAdapter = new ArrayAdapter<>(listViewContext, R.layout.customlistview, directories);
                ((ListView) activity.findViewById(R.id.listView)).setAdapter(listViewAdapter);
                activity.findViewById(R.id.listView).setVisibility(ListView.VISIBLE);

                // Set buttonBack attributes
                if (chosenDirectory != null)
                {
                    activity.findViewById(R.id.buttonBack).setVisibility(ImageButton.VISIBLE);
                }
            }
        });

        Log.i("Read DIR", "Directories written to MainActivity.directories");
    }
}
