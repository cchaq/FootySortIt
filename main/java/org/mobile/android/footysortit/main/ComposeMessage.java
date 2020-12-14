package org.mobile.android.footysortit.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.mobile.android.footysortit.R;

import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static org.mobile.android.footysortit.main.SendSms.requestPerm;

public class ComposeMessage extends AppCompatActivity {
    private AdView mAdView;

    PlayerList playerListGame = new PlayerList();
    SendSms sendTheText = new SendSms(this);
    static final String aMessage = "orgMessage";
    TextView numberOfPlayers;
    EditText editMessage;
    String message;
    SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        finishedPlayersSet();
        editMessage = findViewById(R.id.theMessage);

        mAdView = findViewById(R.id.adViewCompose);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
       // checkPermissionSendSms();
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        firsTimeUsers();

    }

    /**
     * Used to have the below, until I finally learnt how to request
     * multiple permissions in one go
     */
    protected void checkPermissionSendSms(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    requestPerm);
        }
    }


    public void sendMessage(View view) {

        final Toast messageSent = Toast.makeText(this, R.string.text_sent, Toast.LENGTH_LONG);
        final Toast messageNotSent = Toast.makeText(this, R.string.SMSNotSent, Toast.LENGTH_LONG);
        message = editMessage.getText().toString();

        if (TextUtils.isEmpty(message)) {
            Toast messageIsNull = Toast.makeText(this, R.string.emptyMessage, Toast.LENGTH_LONG);
            messageIsNull.show();
        } else if (playerListGame.myPlayers.isEmpty()) {
            Toast.makeText(this, R.string.empty_player_list, Toast.LENGTH_LONG).show();

        }
    else{
            message= message.concat(getString(R.string.smsConcatMsgYesNoMaybe));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.charged_for_sms)
                    .setPositiveButton(R.string.send_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendSms.sendTheText(message, sendTheText, playerListGame.myPlayers);
                            messageSent.show();


                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            messageNotSent.show();
                        }
                    });
            builder.show();


    }

}

    public void playerSms(View view){

       // Toast.makeText(this,R.string.text_sent, Toast.LENGTH_LONG).show();
        sendPlayerDataMethod();

    }

    public void pickPlayerNumbers(View view){

        Intent pickThePlayers =  new Intent(this,ContactPicker.class);
        Bundle bundlePickThePlayers = new Bundle();
        AllContacts allContacts = new AllContacts(this);
        bundlePickThePlayers.putBoolean("pickThePlayersFalse",false);
            bundlePickThePlayers.putParcelableArrayList("playerList", allContacts.getContacts().myPlayers);
            pickThePlayers.putExtras(bundlePickThePlayers);
            startActivity(pickThePlayers);
        }

      /**  Intent contactIntent = new Intent(Intent.ACTION_PICK);
        contactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if(contactIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(contactIntent, PICK_CONTACT);
        }

    }                           ALL THIS ALLOWS THE USER TO PICK ONE CONTACT - CHanging this into a multiple contact picker using recycler view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK){
            Uri contactUri = data.getData();
            PlayerDetails player = new PlayerDetails();
           // String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            //Cursor cursor = getContentResolver().query(contactUri,projection,null,null,null);
            Cursor cursor = getContentResolver().query(contactUri,null,null,null,null);
            if(cursor !=null && cursor.moveToFirst()){
              //  int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
               // String number = cursor.getString(numberIndex);
               // player.number = number;
                player.name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                player.number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                playerListGame.addPlayer(player);
            }

        }
        showPlayerGameList();
    }

 */

 public void showPlayerGameList(){
        TextView playerTextView = findViewById(R.id.aPlayerBox);
        playerTextView.setText("");
    /** for(int i = 0; i < playerListGame.myPlayers.size(); i++){

            playerTextView.append(playerListGame.myPlayers.get(i).name + "\n" + playerListGame.myPlayers.get(i).number + "\n");

        }*/
       for(PlayerDetails i : playerListGame.myPlayers){
         playerTextView.append("\n" + i.name + "\n" + i.number + "\n");
        }
    }

    /** SendPlayerDataMethod is used to send the all the players picked by the user
      to FullPlayerList and opens that activity up*/
    public void sendPlayerDataMethod() {

        Intent sendPlayerData = new Intent(this, FullPlayerList.class);
        sendPlayerData.putParcelableArrayListExtra("list",playerListGame.myPlayers);
        startActivity(sendPlayerData);
    }

    /**
     * Get the players picked from contactPicker
     */
    private void finishedPlayersSet(){
        Bundle allPlayers = getIntent().getExtras();  //Called from ContactPicker
        if (allPlayers != null) {
            playerListGame.myPlayers = allPlayers.getParcelableArrayList("playerList");
            showPlayerGameList();
            displayTotalPlayers();

        }

    }

    private void displayTotalPlayers(){
        numberOfPlayers =  findViewById(R.id.numberOfTotalPlayers);
        numberOfPlayers.setText(getString(R.string.totalPlayers, playerListGame.myPlayers.size()));
    }

    /**
     * below is not used
     * because I can not get it to work at the moment...
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(aMessage, editMessage.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        message = savedInstanceState.getString(aMessage);
        editMessage.setText(message);
    }

    public static SQLiteDatabase addPlayerToTheDatabase(SQLiteDatabase playerDatabase, ArrayList<PlayerDetails> listGame) {

      /**  try {
         *       playerDatabase.beginTransaction();
          *      playerDatabase.delete (PlayerDetails.PlayerTableEntry.TABLE_NAME,null,null);
       *      */

            for (PlayerDetails i : listGame) {
                ContentValues cv = new ContentValues();

                cv.put(PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NAME, i.name);
                cv.put(PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NUMBER, i.number);
                cv.put(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING, 2);

                playerDatabase.insert(PlayerDetails.PlayerTableEntry.TABLE_NAME, null, cv);
            }
        return playerDatabase;
    }

    /**
     * A short burst of info on how to use this activity
     */
    private void firsTimeUsers() {

        if (prefs.getBoolean(ComposeMessage.class.getCanonicalName(), true)) {
            prefs.edit().putBoolean(ComposeMessage.class.getCanonicalName(),false).apply();
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.pickPlayers)
                    .setPrimaryText(R.string.tutPickPlayers)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                new MaterialTapTargetPrompt.Builder(ComposeMessage.this)
                                        .setTarget(R.id.sendToPlayers)
                                        .setPrimaryText(R.string.tutSendSms)
                                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                            @Override
                                            public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                                                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                                    new MaterialTapTargetPrompt.Builder(ComposeMessage.this)
                                                            .setTarget(R.id.smsNote)
                                                            .setPrimaryText(R.string.tutAddSen)
                                                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                                                @Override
                                                                public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                                                                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                                                        new MaterialTapTargetPrompt.Builder(ComposeMessage.this)
                                                                                .setTarget(R.id.playerSms)
                                                                                .setPrimaryText(R.string.tutViewPlayers)
                                                                                .show();
                                                                    }
                                                                }
                                                            })
                                                            .show();
                                                }
                                            }
                                        })
                                        .show();
                            }
                        }
                    })
                    .show();
        }
    }
}

