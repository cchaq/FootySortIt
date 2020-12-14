package org.mobile.android.footysortit.main;

import android.app.Activity;
import android.telephony.SmsManager;

import java.util.ArrayList;

public class SendSms extends Activity{
    static int requestPerm;
    private static Activity mActivity;

    public SendSms(Activity messageActivity){
        mActivity = messageActivity;

    }

    /**Get the message and the players we need to send the text
     * Call sms manager and send the text to each player*/
    protected static void sendTheText(String message, SendSms objectFromComposeMessage, ArrayList<PlayerDetails> playersNumber){
        SmsManager sendTheMessage = SmsManager.getDefault();
        //objectFromComposeMessage.checkPermissionSms();
        for(PlayerDetails i : playersNumber) {
            sendTheMessage.sendTextMessage(i.number, null, message, null, null);
        }
    }

}
