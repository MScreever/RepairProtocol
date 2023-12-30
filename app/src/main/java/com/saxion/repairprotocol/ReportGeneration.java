package com.saxion.repairprotocol;

import static com.saxion.repairprotocol.MainActivity.chosenDirectory;
import static com.saxion.repairprotocol.MainActivity.chosenSubDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.multidex.BuildConfig;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ReportGeneration extends AppCompatActivity
{
    private static JSONObject jsonObject;
    private static String[] picturePaths;
    private static File qualityReport;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generation);

        // Initialization
        Button btnOpenFile = findViewById(R.id.buttonOpen);
        ProgressBar progressBar = findViewById(R.id.progressBar2);
        CheckBox indicatorStep1 = findViewById(R.id.indicatorStep_1);
        CheckBox indicatorStep2 = findViewById(R.id.indicatorStep_2);
        CheckBox indicatorStep3 = findViewById(R.id.indicatorStep_3);

        // Set attributes
        btnOpenFile.setVisibility(Button.INVISIBLE);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // Execute steps
        Thread printSteps = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    {
                        readSpecifiations();
                        runOnUiThread(new Runnable()    // Set Indicator step 1 checked
                        {
                            @Override
                            public void run()
                            {
                                indicatorStep1.setChecked(true);
                            }
                        });

                        generateQRCodes();
                        runOnUiThread(new Runnable()    // Set Indicator step 2 checked
                        {
                            @Override
                            public void run()
                            {
                                indicatorStep2.setChecked(true);
                            }
                        });

                        updateQualityReport();
                        // Set Indicator step 3 checked & ProgressBar and Print button Visible
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                indicatorStep3.setChecked(true);
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                btnOpenFile.setVisibility(Button.VISIBLE);
                            }
                        });
                    }
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        };
        printSteps.start();


        // OnClickListeners
        btnOpenFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(qualityReport).toString());
                String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri fileUri;

                if (Build.VERSION.SDK_INT >= 24)
                {
                    fileUri = FileProvider.getUriForFile(ReportGeneration.this, BuildConfig.APPLICATION_ID + ".provider", qualityReport);
                }
                else
                {
                    fileUri = Uri.fromFile(qualityReport);
                }
                intent.setDataAndType(fileUri, mimetype);

                startActivity(intent);
            }
        });

        // To set a check in Excel: "ü" in Wingdings font

        //FIXME generate report
        // 1) read specs.json file
        // 2) Create QR code of "_MANUAL"
        // 3) read report.xlsx file
        // 4) replace "_..." text blocks with actual specs.
        // 5) insert QR Codes in excel
        // 6) print filled in report.xlsx file to pdf and save in "/tempResources/" folder and maybe elsewhere (Later)
        // 7) send report.pdf to printer to print file
    }

    private void readSpecifiations()
    {
        try
        {
            File specificationsFile = new File(getFilesDir(), "/tempResources/specs.json.txt");

            if (specificationsFile.exists())
            {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(specificationsFile));

                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();

                String jsonString = new String(buffer, "UTF-8");
                jsonObject = new JSONObject(jsonString);
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void generateQRCodes()
    {
        try
        {
            // Initialization
            JSONArray links = jsonObject.getJSONArray("_MANUAL");
            picturePaths = new String[links.length()];

            for (int i = 0; i < links.length(); i++)
            {
                String link = links.getString(i);

                if (link.isEmpty()) continue;

                // Set parameters
                File filePath = new File(getFilesDir() + "/tempResources/ManualLink_" + i + ".png");
                int width = 300;
                int height = 300;

                // Create QR Code
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix matrix = writer.encode(link, BarcodeFormat.QR_CODE, width, height);

                // Convert to BitMap
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap qrCode = barcodeEncoder.createBitmap(matrix);

                // Write BitMap to .png file
                FileOutputStream outputStream = new FileOutputStream(filePath);
                qrCode.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                outputStream.flush();
                outputStream.close();
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void updateQualityReport()
    {
        try
        {
            // Declare variables
            XSSFWorkbook workbook;
            XSSFSheet sheet;
            XSSFRow row;
            XSSFCell currentCell;

            // Read template file
            File reportFile = new File(getFilesDir().toString() + "/tempResources/report.xlsx");
            workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(reportFile)));

            // Get sheet
            sheet = workbook.getSheetAt(0);
            int rows = sheet.getLastRowNum();

            if (rows == -1) return; // If no rows exists, stop fill process

            // Iterate over cells
            for (int r = 0; r < rows; r++)
            {
                row = sheet.getRow(r);
                if (row == null) continue;

                int totalColumns = row.getLastCellNum();

                for (int c = 0; c < totalColumns; c++)
                {
                    currentCell = row.getCell(c);
                    if (currentCell == null) continue;

                    String cellValue = currentCell.getStringCellValue();

                    if (cellValue.startsWith("_"))
                    {
                        if (jsonObject.has(cellValue))
                        {
                            String jsonValue = jsonObject.getString(cellValue);
                            currentCell.setCellValue(jsonValue);
                        }
                    }
                }

            }

            // Save .xlsx file
            File documentDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File qualityReportDestination = new File(documentDirectory, "/QualityReports");
            qualityReportDestination.mkdirs();
            qualityReport = new File(documentDirectory, "/QualityReports/QualityReport_" + chosenDirectory + "_" + chosenSubDirectory + ".xlsx");

            // Write file
            FileOutputStream outputStream = new FileOutputStream(qualityReport);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}