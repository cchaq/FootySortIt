package org.mobile.android.footysortit.automategames;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mobile.android.footysortit.R;
import org.mobile.android.footysortit.main.AllContacts;
import org.mobile.android.footysortit.main.ContactPicker;
import org.mobile.android.footysortit.main.DatabaseUpdateInterface;

import java.util.HashMap;
import java.util.Map;

public class AGFullPlayerList extends Activity
        implements DatabaseUpdateInterface{

    private RecyclerView recyclerView;
    private AGFullPlayerListAdapter agFullPlayerListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    GameDetailsFromDatabase gameDetailsFromDatabase =  new GameDetailsFromDatabase();
    TextView totalPlaying,totalNotPlaying,totalMaybePlaying;
    Button clearList,smsToPlayingPlayers;
    FloatingActionButton addPlayer;
    Bundle tagID;
    Map arePlaying = new HashMap();

    /**
     * tagID is used to get which game was pressed
     * gameID is tagID + 1, which is stored against each player depending on the game
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_player_list);

        //Get the button ID that was pressed to come here, to pass through to RV
        //It is the button ID
        tagID = getIntent().getExtras();

        recyclerView = findViewById(R.id.eachPlayer);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        agFullPlayerListAdapter = new AGFullPlayerListAdapter(this, tagID.get("correctGameID"),this);
        recyclerView.setAdapter(agFullPlayerListAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Maybe one day I'll let the user change the order of their players
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                gameDetailsFromDatabase.agDeletePlayerFromList((int) tagID.get("buttonTagId"),viewHolder.itemView.getId());
               agFullPlayerListAdapter.notifyDataSetChanged(); //Refreshes RV, Better than the way I did it previously
                getNumbersForPlayerStatus();
            }
        }).attachToRecyclerView(recyclerView);

        //Setting the butons up and then using onClicklistener, as in the xml I set onClick to run
        //methods within the main main
        totalPlaying = findViewById(R.id.totalIsPlaying);
        totalNotPlaying = findViewById(R.id.totalNotPlaying);
        totalMaybePlaying = findViewById(R.id.totalMaybePlaying);
        getNumbersForPlayerStatus();
        clearList = findViewById(R.id.clear_list);
        deleteAllPlayersFromAgGame();
        smsToPlayingPlayers = findViewById(R.id.sendSmsToPlaying);
        setSmsToPlayingPlayers();
        addPlayer = findViewById(R.id.addPlayers);
        setAddPlayer();
    }

    @Override
    public void updatePlayerStatus(ContentValues cv, long id) {
        //because this was in the interface I have to keep it
        //I now update the status from within the RV adapter

    }

    @Override
    public void getNumbersForPlayerStatus() {
        arePlaying = gameDetailsFromDatabase.agReturnIsPlayingAmount(tagID.getInt("correctGameID"));
        totalPlaying.setText(getString(R.string.totalIsPlaying, arePlaying.get("isPlaying")));
        totalMaybePlaying.setText(getString(R.string.totalMaybePlaying, arePlaying.get("maybePlaying")));
        totalNotPlaying.setText(getString(R.string.totalNotPlaying, arePlaying.get("notPlaying")));
    }
    public void deleteAllPlayersFromAgGame(){
        clearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  deleteAllPlayersFromAgGame();
                gameDetailsFromDatabase.agDeletePlayersFromThisGame(tagID.getInt("buttonTagID"));
                agFullPlayerListAdapter.notifyDataSetChanged();
                getNumbersForPlayerStatus();
            }
        });
    }

    public void setSmsToPlayingPlayers(){
        final Intent intentSendSmsToPlaying =  new Intent(this, AgSendSmsToPlaying.class);
        smsToPlayingPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSendSmsToPlaying.putExtra("gameID",tagID.getInt("correctGameID"));
                startActivity(intentSendSmsToPlaying);
            }
        });
    }
    private void setAddPlayer(){
        final AllContacts allContacts = new AllContacts(this); //Messed up here, think I should of passed context as para instead
        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPicker = new Intent(v.getContext(), ContactPicker.class);
                Bundle bundle = new Bundle();
                //Need to send through the contacts through to the contact picker. This is why the search was breaking
                bundle.putParcelableArrayList("playerList",allContacts.getContacts().myPlayers);
                contactPicker.putExtras(bundle);
                contactPicker.putExtra("agAddPlayer",true);
                int gameID = (int) gameDetailsFromDatabase.returnAgGameID( tagID.getInt("buttonTagID"));
                contactPicker.putExtra("gameID",gameID);
                startActivity(contactPicker);
            }
        });
    }
}
