package com.saxion.repairprotocol.FileSearch;

import static com.saxion.repairprotocol.MainActivity.chosenDirectory;
import static com.saxion.repairprotocol.MainActivity.chosenSubDirectory;
import static com.saxion.repairprotocol.SettingsActivity.SERVERPASSWORD;
import static com.saxion.repairprotocol.SettingsActivity.SERVERUSERNAME;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.saxion.repairprotocol.MainActivity;
import com.saxion.repairprotocol.ScreenDecision.Screen;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeMap;

import jcifs.CIFSContext;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;

public class ReadDIR extends Thread
{
    public static TreeMap<String, Object[]> protocolData;

    private final File filesDir;
    private final String filePath;
    private final Activity activity;
    private final int BUFFERSIZE = 8 * 1024; // Buffer size [Kb]

    public ReadDIR(Activity activity, File filesDir, String filePath)
    {
        this.activity = activity;
        this.filesDir = filesDir;
        this.filePath = filePath;
    }

    @Override
    public void run()
    {
        protocolData = new TreeMap<>();

        try
        {
            Log.i("Read DIR", "Starting connection to server: " + filePath);

            NtlmPasswordAuthenticator personalAuthentication = new NtlmPasswordAuthenticator(SERVERUSERNAME, SERVERPASSWORD);

            CIFSContext baseContext = new BaseContext(new PropertyConfiguration(System.getProperties()));
            CIFSContext context = baseContext.withCredentials(personalAuthentication);

            SmbFile smbFile = new SmbFile(filePath, context);

            if (smbFile.exists())
            {
                SmbFile[] files = smbFile.listFiles();

                for (SmbFile file : files)
                {
                    String fileName = file.getName().toUpperCase();

                    if (fileName.startsWith(chosenDirectory + "_" + chosenSubDirectory))
                    {
                        readXLSX(file);
                    }
                    else
                    {
                        saveFile(file);
                    }
                }

                smbFile.close();

                // Run screen change on UI thread
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Screen.openFirst(activity);
                    }
                });
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void readXLSX(SmbFile file) throws IOException
    {
        // Initialize variables
        XSSFWorkbook workbook;
        XSSFSheet sheet;
        XSSFRow row;
        XSSFCell currentCell;

        int totalRows;
        Object[] stepData;

        // Element 0 is Window Type
        // Element 1 is Short Description
        // Element 2 is Long Description
        // Element 3 is Next Step
        // Element 4 is Yes Step (Only when SCREEN_QUESTION is selected)
        // Element 5 is No Step (Only when SCREEN_QUESTION is selected)
        // Element 6 is Upper Sound File Name (Only when SCREEN_SOUNDS is selected)
        // Element 7 is Bottom Sound File Name (Only when SCREEN_SOUNDS is selected)
        // Element 8 and more is File Names (Only when SCREEN_IMAGES and SCREEN_VIDEO is selected)

        workbook = new XSSFWorkbook(new BufferedInputStream(file.getInputStream()));
        int totalSheets = workbook.getNumberOfSheets();


        for (int i = 1; i < totalSheets; i++)
        {
            sheet = workbook.getSheetAt(i);
            totalRows = sheet.getLastRowNum();

            stepData = new Object[totalRows + 1];
            String sheetName = workbook.getSheetName(i);

            for (int r = 0; r <= totalRows; r++)
            {
                row = sheet.getRow(r);
                currentCell = row.getCell(1);

                if (currentCell == null) continue;

                switch (currentCell.getCellType())
                {
                    case Cell.CELL_TYPE_BOOLEAN:
                        stepData[r] = currentCell.getBooleanCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        stepData[r] = currentCell.getNumericCellValue();
                        break;
                    case Cell.CELL_TYPE_STRING:
                        stepData[r] = currentCell.getStringCellValue();
                        break;
                }
            }

            protocolData.put(sheetName, stepData);
        }
    }

    private void saveFile(SmbFile file) throws Exception
    {
        BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());

        File outputFile = new File(filesDir, "/tempResources/" + file.getName().toLowerCase());
        outputFile.getParentFile().mkdirs();

        OutputStream outputStream = new FileOutputStream(outputFile);

        byte[] buffer = new byte[BUFFERSIZE];
        int len;

        while ((len = inputStream.read(buffer)) > 0)
        {
            outputStream.write(buffer, 0, len);
        }
    }

}