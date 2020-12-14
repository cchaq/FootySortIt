package org.mobile.android.footysortit.main;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class SmsBroadcastRec extends BroadcastReceiver {

    String textMessage;
    String smsNumber;
    private SQLiteDatabase playerDatabase;
    ArrayList<String> playerPositiveResponse = PlayerResponseList.returnPositiveResponses();
    ArrayList<String> playerNegativeResponse = PlayerResponseList.returnNegativeResponse();
    ArrayList<String> playerNeutralResponse = PlayerResponseList.returnNeutralResponse();
    ContentValues cv = new ContentValues();


    /***
     * If a text comes in, then get the database
     * check if the number matches any numbers within the DB
     * if it does, depending on the message, update the status
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
                FootySortItDatabase footySortItDatabase = new FootySortItDatabase(context);
                playerDatabase = footySortItDatabase.getWritableDatabase();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


                    for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                        try {
                            smsNumber = smsMessage.getOriginatingAddress();
                        }
                        catch (Exception e){
                            Log.e("Sms Exception","Not a number");
                        }

                        if (checkDataFromDatabase(smsNumber)) {
                            textMessage = smsMessage.getMessageBody();
                            updatePlayerStatusFromResponse(textMessage, smsNumber);
                            break;
                        }
                    }
                }
                else   {
                    Bundle smsBundle = intent.getExtras();
                    if(smsBundle != null){
                        Object[] pdus = (Object[]) smsBundle.get("pdus");
                        if(pdus == null){
                            Log.e(TAG, "pdus contains no keys");
                            return;
                        }
                        SmsMessage[] messages = new SmsMessage[pdus.length];
                        for(int i = 0; i < messages.length; i++){
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            textMessage += messages[i].getMessageBody();

                        }
                    }
                }
            }
    }

    private boolean checkDataFromDatabase(String number){
        Cursor player = playerDatabase.rawQuery(" select 1 from playerTable where exists (select * from playerTable where playerNumber =" +number +")",null);

      try {
          if (player.getCount() > 0) {
              player.close();
              return true;
          }
          else{
              return false;
          }

      }catch (Exception e){
          Log.e("Error from getting orginating address","Most because it is not a number");
      }
      finally {
          if(player != null){
              player.close();
          }
      }
      return false;
    }

    /**
     * Check with what the user has responded matches against the response list
     * update their status in the DB accordingly
     * @param message
     * @param number
     */
    private void updatePlayerStatusFromResponse(String message, String number){

        if(playerPositiveResponse.contains(message.toLowerCase())){
            cv.put(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING,"1");
            playerDatabase.update(PlayerDetails.PlayerTableEntry.TABLE_NAME,cv, "playerNumber="+number,null);
            //return true;
        }
        else if(playerNegativeResponse.contains(message.toLowerCase())){
            cv.put(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING,"0");
            playerDatabase.update(PlayerDetails.PlayerTableEntry.TABLE_NAME,cv,"playerNumber="+number ,null);
            //return true;
        }
        else if(playerNeutralResponse.contains(message.toLowerCase())){
            cv.put(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING,"2");
            playerDatabase.update(PlayerDetails.PlayerTableEntry.TABLE_NAME,cv,"playerNumber="+number ,null);
            //return true;
        }
    }

    }


