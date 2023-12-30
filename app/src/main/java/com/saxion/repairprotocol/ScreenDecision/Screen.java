package com.saxion.repairprotocol.ScreenDecision;

import static com.saxion.repairprotocol.FileSearch.ReadDIR.protocolData;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import com.saxion.repairprotocol.MainActivity;
import com.saxion.repairprotocol.ReportGeneration;
import com.saxion.repairprotocol.WindowImages;
import com.saxion.repairprotocol.WindowQuestion;
import com.saxion.repairprotocol.WindowSound;
import com.saxion.repairprotocol.WindowText;
import com.saxion.repairprotocol.WindowVideo;

import java.util.ArrayList;
import java.util.Objects;


public class Screen
{
    public static Object[] stepData;
    public static boolean firstStep;
    public static ArrayList<String> stepOrder;

    private static String nextStep;
    private static String yesStep;
    private static String noStep;
    private static String windowType;

    public static void openFirst(Activity activity)
    {
        if (stepOrder == null) stepOrder = new ArrayList<>();
        stepOrder.add("STEP 1");
        firstStep = true;

        stepData = protocolData.get("STEP 1");

        windowType = Objects.toString(stepData[0], "");
        nextStep = Objects.toString(stepData[3], "");
        yesStep = Objects.toString(stepData[4], "");
        noStep = Objects.toString(stepData[5], "");

        openIntent(activity);
    }

    public static void openNext(Activity activity)
    {
        if (stepOrder == null) stepOrder = new ArrayList<>();
        firstStep = false;

        if (nextStep.isEmpty())
        {
            windowType = "END";
        }
        else
        {
            stepOrder.add(nextStep);
            stepData = protocolData.get(nextStep);

            if (stepData == null) return;

            windowType = Objects.toString(stepData[0], "");
            nextStep = Objects.toString(stepData[3], "");
            yesStep = Objects.toString(stepData[4], "");
            noStep = Objects.toString(stepData[5], "");
        }

        openIntent(activity);
    }

    public static void openPrevious(Activity activity)
    {
        String previousStep = stepOrder.get(stepOrder.size() - 2);
        stepData = protocolData.get(previousStep);

        firstStep = previousStep.equals("STEP 1");

        windowType = Objects.toString(stepData[0], "");
        nextStep = Objects.toString(stepData[3], "");
        yesStep = Objects.toString(stepData[4], "");
        noStep = Objects.toString(stepData[5], "");

        stepOrder.remove(stepOrder.size() - 1);

        openIntent(activity);
    }

    public static void openYes(Activity activity)
    {
        if (stepOrder == null) stepOrder = new ArrayList<>();
        firstStep = false;

        if (yesStep.isEmpty())
        {
            windowType = "END";
        }
        else
        {
            stepOrder.add(yesStep);
            stepData = protocolData.get(yesStep);

            if (stepData == null) return;

            windowType = Objects.toString(stepData[0], "");
            nextStep = Objects.toString(stepData[3], "");
            yesStep = Objects.toString(stepData[4], "");
            noStep = Objects.toString(stepData[5], "");
        }

        openIntent(activity);
    }

    public static void openNo(Activity activity)
    {
        if (stepOrder == null) stepOrder = new ArrayList<>();
        firstStep = false;

        if (noStep.isEmpty())
        {
            windowType = "END";
        }
        else
        {
            stepOrder.add(noStep);
            stepData = protocolData.get(noStep);

            if (stepData == null) return;

            windowType = Objects.toString(stepData[0], "");
            nextStep = Objects.toString(stepData[3], "");
            yesStep = Objects.toString(stepData[4], "");
            noStep = Objects.toString(stepData[5], "");
        }

        openIntent(activity);
    }

    private static void openIntent(Activity activity)
    {
        Intent intent = new Intent();

        switch (windowType)
        {
            case "SCREEN_TEXT":
                intent = new Intent(activity, WindowText.class);
                break;
            case "SCREEN_IMAGES":
                intent = new Intent(activity, WindowImages.class);
                break;
            case "SCREEN_VIDEO":
                intent = new Intent(activity, WindowVideo.class);
                break;
            case "SCREEN_SOUNDS":
                intent = new Intent(activity, WindowSound.class);
                break;
            case "SCREEN_QUESTION":
                intent = new Intent(activity, WindowQuestion.class);
                break;
            case "END":
                intent = new Intent(activity, MainActivity.class);
                // intent = new Intent(activity, ReportGeneration.class);
                break;
        }

        activity.startActivity(intent);
    }
}
