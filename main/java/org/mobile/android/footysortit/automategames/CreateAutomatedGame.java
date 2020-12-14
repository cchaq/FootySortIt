package org.mobile.android.footysortit.automategames;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.mobile.android.footysortit.R;
import org.mobile.android.footysortit.main.AllContacts;
import org.mobile.android.footysortit.main.ContactPicker;
import org.mobile.android.footysortit.main.FootySortItDatabase;
import org.mobile.android.footysortit.main.PlayerDetails;
import org.mobile.android.footysortit.main.PlayerList;

import java.util.Calendar;

public class CreateAutomatedGame extends AppCompatActivity implements View.OnClickListener {

    Button buttonDatePicker, buttonTimePicker;
    EditText etDate, etTime, smsMessage;
    private int mYear, mMonth, mDay, mHour, mMinute;
    PlayerList playerList = new PlayerList();
    TextView playersFromContactPicker;
    private SQLiteDatabase automateGameDB;
    String typedUpSmsMessage;
    long gameID;
    FloatingActionButton fab;
    String pickedDate;
    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_automated_game);

        playersFromContactPicker = findViewById(R.id.tvPickedPlayers);
        buttonDatePicker = findViewById(R.id.buttonChooseDate);
        buttonTimePicker = findViewById(R.id.buttonChooseTime);
        etDate = findViewById(R.id.etAutomateDate);
        etTime = findViewById(R.id.etAutomateTime);
        smsMessage = findViewById(R.id.editTextAutomateGameComposeMessage);
        fab = findViewById(R.id.doneCreatingGame);

        FootySortItDatabase dbHelper = new FootySortItDatabase(this);
        automateGameDB = dbHelper.getWritableDatabase();

        buttonDatePicker.setOnClickListener(this);
        buttonTimePicker.setOnClickListener(this);

        getPlayerListFromContactPicker();


    }

    /**
     * Start ContactPicker.class
     * Pass through a true boolean, so on the other side we can tell ContactPicker that we started it
     *
     * @param view
     */

    public void pickPlayersForAutomateGame(View view) {
        Intent startContactPicker = new Intent(this, ContactPicker.class);
        AllContacts allContacts = new AllContacts(this);
        Bundle bundle = new Bundle();
        //Need to send through the contacts through to the contact picker. This is why the search was breaking
        bundle.putParcelableArrayList("playerList",allContacts.getContacts().myPlayers);
        startContactPicker.putExtras(bundle);
        startContactPicker.putExtra("createAutomateGameTrue", true);
        startActivity(startContactPicker);
    }

    /**
     * Once the user has finished picking their players
     * use this method to get the data from contactPicker
     */

    private void getPlayerListFromContactPicker() {
        Bundle bundle = getIntent().getExtras();
        try {
            if (bundle != null) {
                playerList.myPlayers = bundle.getParcelableArrayList("playerList");
                for (PlayerDetails i : playerList.myPlayers) {
                    playersFromContactPicker.append("\n" + i.name);
                }
            }
        } catch (NullPointerException e) {
            Log.e("NullPointerException", "There has been a null pointer, most likely on PlayerList.MyPlayers");
        }
    }

    /**
     * Setting the date and time button to open up the calender and date picker
     * to let the user pick the date and time they want the text to be sent
     *
     * @param v
     */


    @Override
    public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        //Get the date
        if (v == buttonDatePicker) {
            //This is used to set the datePicker to the current date. I also use the same vars to pass the data through!
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH) + 1;
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            etDate.setText(dayOfMonth + "-" + month + "-" + year);
                            mYear = year;
                            mMonth = month;
                            mDay = dayOfMonth;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }
        //Get the time
        if (v == buttonTimePicker) {
            //This is used to set the datePicker to the current date. I also use the same vars to pass the data through!
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            etTime.setText(hourOfDay + ":" + minute);
                            mHour = hourOfDay;
                            mMinute = minute;
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();

        }
    }

    /**
     * Once user clicks done, we will add all the data to the database
     */

    public void doneCreatingAutomateGame(View view) {
        if(playerList.myPlayers.isEmpty()){
            Toast.makeText(this,R.string.empty_player_list,Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(smsMessage.getText())){
            Toast.makeText(this, R.string.emptyMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(etDate.getText())){
            Toast.makeText(this, R.string.agEmptyDate, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(etTime.getText())){
            Toast.makeText(this, R.string.agEmptyTime, Toast.LENGTH_SHORT).show();
            return;
        }

        pickedDate = mYear + "-"+ mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":00";
        DBAsyncTask dbAsyncTask = new DBAsyncTask();
        dbAsyncTask.doInBackground();
        Intent intent = new Intent(this, MainAutomateGames.class);
        startActivity(intent);

        //AddPlayersToTheTable addPlayersToTheTable = new AddPlayersToTheTable();
        //addPlayersToTheTable.doInBackground();

    }

    /**
     * Get the amount of rows within automatesGames
     * and set that to gameID.
     * This way, we'll always be +1 ahead of the amount
     */
    private void updateGameID() {
        Cursor cursor = automateGameDB.rawQuery("select * from automateGames", null);
        //it will be 0 for first time users. DB will have 0 rows. So have to increment by 1.
        //Also stops from duplicate the same highest row number
        gameID = cursor.getCount() + 1;

    }

    private class DBAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {

            updateGameID();
            typedUpSmsMessage = smsMessage.getText().toString();
            ContentValues cv = new ContentValues();
            cv.put("textMessage", typedUpSmsMessage);
            cv.put("dateToSendText",pickedDate);
            cv.put("gameID", gameID);
            cv.put("textSent",0);
            automateGameDB.insert("automateGames", null, cv);

            /**
             * The "isPlaying" number system is not consistent within this app
             * this is my own fault because I did not check it before.
             */
            ContentValues av = new ContentValues();
            for (PlayerDetails i : playerList.myPlayers) {
                av.put("playerName", i.name);
                av.put("playerNumber", i.number);
                av.put("isPlaying", 0); //Start with zero as we are unsure whether the person is playing or not
                av.put("gameID", gameID);
                automateGameDB.insert("automatePlayerTable", null, av);
            }
            return null;
        }
    }
}