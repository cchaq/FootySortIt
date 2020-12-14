package org.mobile.android.footysortit.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import org.mobile.android.footysortit.R;

public class NotificationUtils  {

    private static int fullPlayerListID = 1234;
    private static int fullPlayerListNotificationID = 4321;
    private static String fullPlayerListStringID = "reminder_notification_channel";

    public static void notifyHowManyPlayers(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(fullPlayerListStringID,
                    context.getString(R.string.charged_for_sms),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context,fullPlayerListStringID).setColor(ContextCompat.getColor(context,R.color.cinna))
                .setSmallIcon(R.drawable.common_google_signin_btn_text_light_normal_background)
                .setAutoCancel(true)
                .setContentIntent(contentIntent(context))
                .setContentText("testing")
                .setContentTitle("title test")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("big test"));


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(fullPlayerListNotificationID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context){
        Intent startFullPlayerListActivity = new Intent(context, FullPlayerList.class);
        return PendingIntent.getActivity(context, fullPlayerListID,startFullPlayerListActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
