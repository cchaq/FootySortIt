package org.mobile.android.footysortit.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.mobile.android.footysortit.R;

import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.provider.BaseColumns._ID;
import static org.mobile.android.footysortit.main.PlayerDetails.PlayerTableEntry.TABLE_NAME;


public class FullPlayerList extends Activity implements DatabaseUpdateInterface{
    private AdView mAdView;

    ArrayList<PlayerDetails> playerDataSet;
    private SQLiteDatabase playerDatabase;
    SharedPreferences prefs;


    private RecyclerView playerRecyclerView;
    private PlayerListRecyclerViewAdapter playerAdapter;
    private RecyclerView.LayoutManager recyclerViewLayout;
    TextView totalPlaying;
    TextView totalNotPlaying;
    TextView totalMaybePlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_player_list);
        mAdView = findViewById(R.id.adViewFullList);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        prefs = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        tutFullPlayerList();

        playerRecyclerView = findViewById(R.id.eachPlayer);
        FootySortItDatabase dbHelper = new FootySortItDatabase(this);
        playerDatabase = dbHelper.getWritableDatabase();
        playerRecyclerView.setHasFixedSize(true);
        playerRecyclerView.setItemViewCacheSize(20);


        final Cursor cursor = getLatestPlayerList();

        recyclerViewLayout = new LinearLayoutManager(this);
        playerRecyclerView.setLayoutManager(recyclerViewLayout);

        playerAdapter = new PlayerListRecyclerViewAdapter(this,cursor, playerDataSet, this);
        playerRecyclerView.setAdapter(playerAdapter);

        totalPlaying =  findViewById(R.id.totalIsPlaying);
        totalNotPlaying = findViewById(R.id.totalNotPlaying);
        totalMaybePlaying = findViewById(R.id.totalMaybePlaying);


        /**
         * Swipe to remove a player.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Do nothing, we are not moving anything
                //But maybe we can let the user arrange their list?
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removePlayer(id);
                getNumbersForPlayerStatus();
                playerAdapter.swapCursor(getLatestPlayerList());

            }
        }).attachToRecyclerView(playerRecyclerView);
        getNumbersForPlayerStatus();
    }

    /**
     * getPlayerData was used before I put in a database
     */
    public void getPlayerData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            playerDataSet = extras.getParcelableArrayList("list");
           // getAllPlayers();
        }

    }

    public SQLiteDatabase deleteAllRow(){
//        playerDatabase.execSQL(" DELETE FROM " + TABLE_NAME);
        playerDatabase.delete(TABLE_NAME,null,null);
        return playerDatabase;
    }

    /***
     *If user wants to add more players they can do here.
     * Pass through the playerDataSet which already contains all the original players
     *
     */
    public void addMorePlayers(View v){
        Intent contactPicker = new Intent(this,ContactPicker.class);
        Bundle bundleContactPicker = new Bundle();
        bundleContactPicker.putParcelableArrayList("FullPlayerList", playerDataSet);
        bundleContactPicker.putBoolean("FullPlayerListBoolean", true);
        AllContacts allContacts = new AllContacts(this);
        bundleContactPicker.putParcelableArrayList("playerList", allContacts.getContacts().myPlayers);
        contactPicker.putExtras(bundleContactPicker);
        startActivity(contactPicker);

    }
    private void removePlayer(long id){
    //   return playerDatabase.delete(PlayerDetails.PlayerTableEntry.TABLE_NAME,
      //=          _ID + "=" + id,null) > 0;
        DbQueryAsyncTask removeAPlayer = new DbQueryAsyncTask();
        removeAPlayer.doInBackground(id);
    }
    public void clearPlayerList(View view){
        deleteAllRow();
        recreate();

    }

    private Cursor getLatestPlayerList(){
        return playerDatabase.query(PlayerDetails.PlayerTableEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NAME);
    }

    @Override
    public void updatePlayerStatus(ContentValues cv, long id) {
        playerDatabase.update(PlayerDetails.PlayerTableEntry.TABLE_NAME,cv,_ID + "=" + id,null);

    }

    /**
     * I was going to use the below to get the latest isPlaying status
     * but used setHasFixedSize to resolve the issue of the status colour not staying the same
     * when the rview was being recyceld
     * @param id
     * @return
     */

    public int getPlayerStatus(long id){
       // playerDatabase.query(PlayerDetails.PlayerTableEntry.TABLE_NAME, PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING, )
      Cursor test =  playerDatabase.rawQuery("select * from playerTable where id ="+id,null);
      return test.getColumnIndex(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING);

    }

    /***
     * Below methods are used to display who is playing
     * who is not
     * and who is maybe
     * not sure why it does not like the set text string, as I used it in compose message just fine
     * that is why I added the SuppressLint
     */

    @SuppressLint("StringFormatInvalid")
    public void getNumbersForPlayerStatus(){
        Cursor amountPlaying = playerDatabase.rawQuery("select * from playerTable where isPlaying = 1",null,null);
      //  int number =  numberPlaying.getCount();
        totalPlaying.setText(getString(R.string.totalIsPlaying, amountPlaying.getCount()));
      //  amountPlaying.close();

        Cursor amountNotPlaying = playerDatabase.rawQuery("select * from playerTable where isPlaying = 0",null,null);
        //  int number =  numberPlaying.getCount();
        totalNotPlaying.setText(getString(R.string.totalNotPlaying, amountNotPlaying.getCount()));
        //amountNotPlaying.close();

        Cursor amountMaybePlaying = playerDatabase.rawQuery("select * from playerTable where isPlaying = 2",null,null);
        //  int number =  numberPlaying.getCount();
        totalMaybePlaying.setText(getString(R.string.totalMaybePlaying, amountMaybePlaying.getCount()));
        //amountMaybePlaying.close();

    }

    public void startSendToPlayingActivity(View view){
        Intent sendToPlaying = new Intent(this,SendSmsToPlayingPlayers.class);
        startActivity(sendToPlaying);
    }


    private class DbQueryAsyncTask extends AsyncTask<Long,Void,Void>{

        //If you want to turn this class to static, initiate the DB and use getApplicationContext():

        @Override
        protected Void doInBackground(Long... id) {
            long longID = id[0];
            playerDatabase.delete(PlayerDetails.PlayerTableEntry.TABLE_NAME,
                    _ID + "=" + longID,null);
           // playerDatabase.close();
            return null;
        }
    }

    public void toFootballPitch(View v){
        Intent toPitch =  new Intent(this,FootballPitchMain.class);
        startActivity(toPitch);
    }


    /**
     * First time tutorial for the user
     */

    private void tutFullPlayerList(){
       if(prefs.getBoolean(FullPlayerList.class.getCanonicalName(),true)){
            prefs.edit().putBoolean(FullPlayerList.class.getCanonicalName(),false).apply();
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.forTut)
                    .setPrimaryTextColour(getResources().getColor(R.color.softwhite))
                    .setPrimaryText(R.string.tutRvFullPlayers)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged( MaterialTapTargetPrompt prompt, int state) {
                            if(state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                new MaterialTapTargetPrompt.Builder(FullPlayerList.this)
                                        .setTarget(R.id.pitchActivity)
                                        .setPrimaryText(R.string.tutPitch)
                                        .show();
                            }
                        }
                    })
                    .show();
        }
    }

}
