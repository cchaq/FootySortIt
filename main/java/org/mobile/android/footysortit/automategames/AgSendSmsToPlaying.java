package org.mobile.android.footysortit.automategames;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mobile.android.footysortit.R;

import java.util.ArrayList;

public class AgSendSmsToPlaying extends AppCompatActivity{

    GameDetailsFromDatabase gameDetailsFromDatabase = new GameDetailsFromDatabase();
    Button sendSms;
    Bundle gameIDExtra;
    Cursor cursor;
    TextView tvMessage;
    ArrayList<String> playerNumbers = new ArrayList<>();
    SmsManager smsManager = SmsManager.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms_to_playing_players);
        tvMessage = findViewById(R.id.playingPlayersMessage);

        //Set onclickListener to the sendSms button.
        sendSms = findViewById(R.id.sendTheSmsToPlayingPlayers);
        setSendSms(); //Activate the onClickL

        gameIDExtra = getIntent().getExtras();

        agGetPlayersPlayingList();

    }

    private void setSendSms(){
        final Toast messageNotSent = Toast.makeText(this,R.string.SMSNotSent,Toast.LENGTH_SHORT);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(tvMessage.getText())){
                    Toast.makeText(v.getContext(),R.string.emptyMessage,Toast.LENGTH_SHORT).show();
                }
                else if(playerNumbers.isEmpty()){
                    Toast.makeText(v.getContext(),R.string.no_playing_players,Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(v.getContext());
                    adBuilder.setMessage(R.string.charged_for_sms)
                            .setPositiveButton(R.string.send_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendingMessage(tvMessage.getText().toString());                        }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    messageNotSent.show();
                                }
                            });
                    adBuilder.show();
                }
            }
        });
    }

    private void agGetPlayersPlayingList(){
        cursor = gameDetailsFromDatabase.returnPlayersPlayingList(gameIDExtra.getInt("gameID"));
        while (cursor.moveToNext()){
            playerNumbers.add(cursor.getString(cursor.getColumnIndex("playerNumber")));
        }
    }
    private void sendingMessage(String message){
        for (String i : playerNumbers) {
            smsManager.sendTextMessage(i, null, message, null, null);
             }
        Toast.makeText(this,R.string.text_sent,Toast.LENGTH_LONG).show();
    }
}
