package org.mobile.android.footysortit.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.mobile.android.footysortit.R;
import org.mobile.android.footysortit.automategames.AutomateJobs;
import org.mobile.android.footysortit.automategames.MainAutomateGames;

import static org.mobile.android.footysortit.main.SendSms.requestPerm;


public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    //private SmsBroadcastRec textMessageRec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this,"ca-app-pub-2142360027287638~9531994105"); //Live
        //MobileAds.initialize(this,"ca-app-pub-3940256099942544/6300978111"); //test
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        checkPermissionSms();
        AutomateJobs.runAutomateGamesCheck(this);

    }

        public void newGame(View view){
            Intent composeMessageIntent = new Intent(this, ComposeMessage.class);
            startActivity(composeMessageIntent);

    }
    public  void openAutomateGame(View view){
        Intent gotoAutomateGameActivity = new Intent(this, MainAutomateGames.class);
        startActivity(gotoAutomateGameActivity);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent startSettingActivity = new Intent(this,SettingsActivity.class);
            startActivity(startSettingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void checkPermissionSms(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS},
                    requestPerm);                                 //If we don't have the permission, request it here
        }
    }

    /***
     * to test the push notification
     * @param view
     * not used
     */
    public void testNotification(View view){
        NotificationUtils.notifyHowManyPlayers(this);
    }

}



//TODO use onSavedInstanceState to store the message within the message box

