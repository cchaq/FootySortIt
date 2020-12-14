package org.mobile.android.footysortit.main;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.mobile.android.footysortit.R;

import java.util.ArrayList;

public class SendSmsToPlayingPlayers extends AppCompatActivity {
    private SQLiteDatabase playerDatabase;
    private String message;
    TextView tvMessage;
    SmsManager smsManager = SmsManager.getDefault();
    ArrayList<String> playerNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms_to_playing_players);
        AdView mAdView = findViewById(R.id.adSendToPlaying);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        tvMessage = findViewById(R.id.playingPlayersMessage);
        FootySortItDatabase dbHelper = new FootySortItDatabase(this);
        playerDatabase = dbHelper.getReadableDatabase();

        getPlayingPlayers();
    }

    private void getPlayingPlayers(){
       Cursor allPlaying = playerDatabase.rawQuery("select * from playerTable where isPlaying = 1",null,null);
       allPlaying.moveToFirst();
       while(!allPlaying.isAfterLast()){
           playerNumbers.add(allPlaying.getString(allPlaying.getColumnIndex("playerNumber")));
           allPlaying.moveToNext();
       }
       allPlaying.close();

    }

    public void sendTheSmsToPlayingPlayers(View view){
        final Toast messageNotSent = Toast.makeText(this,R.string.SMSNotSent, Toast.LENGTH_LONG);
        message = tvMessage.getText().toString();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(this,R.string.emptyMessage,Toast.LENGTH_LONG).show();
        }
        else if(playerNumbers.isEmpty()){
            Toast.makeText(this,R.string.empty_player_list,Toast.LENGTH_LONG).show();
        }

        else {
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
            adBuilder.setMessage(R.string.charged_for_sms)
                    .setPositiveButton(R.string.send_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendingMessage(message);                        }
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

    private void sendingMessage(String passedMessage){

        for (String i : playerNumbers) {
            smsManager.sendTextMessage(i, null, passedMessage, null, null);
        }
        Toast.makeText(this,R.string.text_sent,Toast.LENGTH_LONG).show();
    }


}
