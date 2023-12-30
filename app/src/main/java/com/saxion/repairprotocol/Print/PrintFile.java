package com.saxion.repairprotocol.Print;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PrintFile
{
    private static final int bufferSize = 1 * 1024; // Buffer size [Kb]

    public static void print(Context context, String jobName, String filePath, String pdfName)
    {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        if (printManager == null) return;

        printManager.print(jobName, new MSPrintDocumentAdapter(context, filePath, pdfName), null);
    }

    private static class MSPrintDocumentAdapter extends PrintDocumentAdapter
    {
        private final Context context;
        private final String filePath;
        private final String pdfName;

        public MSPrintDocumentAdapter(Context context, String filePath, String pdfName)
        {
            this.context = context;
            this.filePath = filePath;
            this.pdfName = pdfName;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras)
        {
            if (cancellationSignal.isCanceled())
            {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo pdi = new PrintDocumentInfo
                    .Builder(pdfName + ".pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build();

            callback.onLayoutFinished(pdi, true);
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback)
        {
            try
            {
                OutputStream outputStream = new FileOutputStream(destination.getFileDescriptor());
                byte[] buffer = new byte[bufferSize];
                int bytesRead;

                InputStream inputStream = new FileInputStream(filePath);

                while ((bytesRead = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }
            catch (IOException e)
            {
                callback.onWriteFailed(e.toString());
            }
        }
    }
}
