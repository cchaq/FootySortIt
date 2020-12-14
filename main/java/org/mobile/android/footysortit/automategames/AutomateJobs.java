package org.mobile.android.footysortit.automategames;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;


import java.util.concurrent.TimeUnit;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class AutomateJobs {
    private static final int REMINDER_MINUTES = 30;
    private static final int REMINDER_IN_SECONDS =  (int) (TimeUnit.MINUTES.toSeconds(REMINDER_MINUTES));
    private static final int FLEX_SECONDS = REMINDER_IN_SECONDS; // amount of seconds between each check

    private static final String REMINDER_JOB_TAG = "check_date_time_tag";
    private static boolean sInitialised;


    synchronized public static void runAutomateGamesCheck(@NonNull final Context context){

        if(sInitialised) return;
        Driver driver = new GooglePlayDriver(context);
      //  FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        Job constraintCheckDateTimeJob =  firebaseJobDispatcher.newJobBuilder()
                .setService(AutomateSendSms.class)
                .setTag(REMINDER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REMINDER_IN_SECONDS,REMINDER_IN_SECONDS + FLEX_SECONDS))
                .setReplaceCurrent(true) //if remade, replace the old one
                .build();
        firebaseJobDispatcher.schedule(constraintCheckDateTimeJob);
        sInitialised = true;
    }
}
