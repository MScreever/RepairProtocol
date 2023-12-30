package com.saxion.repairprotocol.Dialogs;

import static com.saxion.repairprotocol.MainActivity.TEMPRESOURCEDIRECTORY;
import static com.saxion.repairprotocol.ScreenDecision.Screen.stepOrder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.saxion.repairprotocol.FileSearch.DirectoryDeletion;
import com.saxion.repairprotocol.MainActivity;

public class Dialogs
{
    public static void showInfoDialog(Context context, String message)
    {

    }

    public static void showQuitDialog(Context context)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage("Weet je zeker dat je wilt stoppen?\nDe voortgang zal niet opgeslagen worden.");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DirectoryDeletion.deleteDirectory(TEMPRESOURCEDIRECTORY);
                stepOrder = null;

                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("Nee", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Nothing when no is pressed
                dialog.dismiss();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
