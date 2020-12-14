package org.mobile.android.footysortit.automategames;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.telephony.SmsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AutomateSendSms extends com.firebase.jobdispatcher.JobService { //TODO find out why it won't let me import this

    private GameDetailsFromDatabase gameDetailsFromDatabase = new GameDetailsFromDatabase();
    SmsManager smsManager = SmsManager.getDefault();
    private Calendar calendar;
    private int mYear, mMonth, mDay, mHour, mMinute,mHourPlusOne;
    private Cursor listOfAllDateTimesToSendText;
    private SimpleDateFormat sdfCurrentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfCurrentDateTimePlusOne = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfCurrentDateTimeFromDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date currentDate,currentDatePlusOne,dateFromDB;
    private AsyncTask checkDateTime;

    @SuppressLint("StaticFieldLeak") //if not added, it shows an annoying yellow block all over the code
    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters params) {
        checkDateTime =  new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] objects) {
                listOfAllDateTimesToSendText = gameDetailsFromDatabase.agReturnDateTime();
                calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH) + 1;
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                mHourPlusOne = calendar.get(Calendar.HOUR_OF_DAY);
                String currentDateTime = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":00";
                String currentDateTimePlusOneHour = mYear + "-" + mMonth + "-" + mDay + " " + mHourPlusOne + ":" + mMinute + ":00";
                try {
                    currentDate = sdfCurrentDateTime.parse(currentDateTime);
                    currentDatePlusOne = sdfCurrentDateTimePlusOne.parse(currentDateTimePlusOneHour);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    while (listOfAllDateTimesToSendText.moveToNext()) {
                        String dateTimeFromCursor = listOfAllDateTimesToSendText.getString(listOfAllDateTimesToSendText.getColumnIndex(MainAutomateGames.AutomateGamesTable.COLUMN_DATE_TO_SEND_TEXT));
                        try {
                            dateFromDB = sdfCurrentDateTimeFromDB.parse(dateTimeFromCursor);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (dateFromDB.after(currentDate) && dateFromDB.before(currentDatePlusOne)) {
                            int gameID = (int) gameDetailsFromDatabase.returnAgGameID(listOfAllDateTimesToSendText.getPosition());// Because I know the index is 3. Else you can use getColumnIndex
                           if (gameDetailsFromDatabase.agTextNeedsSending(gameID)) {
                               sendSms(gameID);
                               gameDetailsFromDatabase.agUpdateTextSentColumn(gameID);
                           }
                           else break;
                        }
                    }
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o){
                jobFinished(params, false);
            }
        };
        checkDateTime.execute();
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters params) {
        return false;
    }

    /**
     * If the above job service find a dateTime within the DB that  matches the requirements
     * then we will use the gameID sent as an argument, to find the players from gameDetailsFromDatabase
     * and then send them the message
     * @param gameID
     */
    private void sendSms(int gameID){
       // int gameIDMinusOne = gameID - 2;// because I messed up with how the gameID adds one when call agGetPlayerList, I have minus two to get the right gaeID
        Cursor listOfPlayers =  gameDetailsFromDatabase.agGetPlayerList(gameID);
        String messageToPlayers = gameDetailsFromDatabase.getTextMessage(gameID);
        while (listOfPlayers.moveToNext()){
            String playerNumber =  listOfPlayers.getString(2);
            smsManager.sendTextMessage(playerNumber,null,messageToPlayers,null,null);
        }
    }
}