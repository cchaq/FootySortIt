package org.mobile.android.footysortit.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import org.mobile.android.footysortit.R;
import org.mobile.android.footysortit.automategames.CreateAutomatedGame;
import org.mobile.android.footysortit.automategames.GameDetailsFromDatabase;
import org.mobile.android.footysortit.automategames.MainAutomateGames;

import java.util.ArrayList;

import static org.mobile.android.footysortit.main.PlayerDetails.PlayerTableEntry.TABLE_NAME;

public class ContactPicker extends AppCompatActivity implements ContactPickerInterface {

    private RecyclerView contactPickerRecyclerView;
    private ContactPickerRecyclerViewAdapter contactPickerAdapter;
    private RecyclerView.LayoutManager contactPickerLayout;
    private ArrayList<PlayerDetails> playerNameNumber;
    private ArrayList<PlayerDetails> addingMorePlayers;
    ArrayList<String> playersNameFromCPRVA = new ArrayList<>();
    private PlayerList pickedPlayers = new PlayerList();
    FloatingActionButton fabPickedPlayersDone;
    Bundle intentThatStartedThisActivity;
    int whichActivityToStart, gameID;
    private SQLiteDatabase playerDatabase;
    TextView tvDisplayPlayersPicked;
    TextView tvPickedPlayersFromContactPicker;
    private GameDetailsFromDatabase gameDetailsFromDatabase = new GameDetailsFromDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker_recycler_view);
        intentThatStartedThisActivity = getIntent().getExtras();
        whichActivityToStart = 0;
        FootySortItDatabase dbHelper = new FootySortItDatabase(this);
        playerDatabase = dbHelper.getWritableDatabase();
        SendNewPlayerListToCalledActivity();
        getPlayerData();

        contactPickerRecyclerView = findViewById(R.id.contactPicker);

        contactPickerLayout = new LinearLayoutManager(this);
        contactPickerRecyclerView.setLayoutManager(contactPickerLayout);

        contactPickerAdapter = new ContactPickerRecyclerViewAdapter(playerNameNumber, this, this);
        contactPickerRecyclerView.setAdapter(contactPickerAdapter);
        contactPickerRecyclerView.setHasFixedSize(true);   //this fixes the checked box being Recyecled
        contactPickerRecyclerView.setItemViewCacheSize(1000); //but this is what it is important, as it is saying, don't re use until after 500 recycles

        fabPickedPlayersDone = findViewById(R.id.addPlayers);
        fabPickedPlayersDone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                donePicking();
            }
        });

        tvDisplayPlayersPicked = findViewById(R.id.displayPlayersPicked);
        tvPickedPlayersFromContactPicker = findViewById(R.id.pickedPlayersFromContactPicker);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =   findViewById(R.id.search_view);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactPickerAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactPickerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    /***
     * getPlayerData is used to get the users phonebook and populate it through the RecyclerView
     * playerList is the key.
     * So anytime ContactPicker is called, the calling activity should call AllContacts .getContacts()
     * Else we can use this to list another set of data for the user to pick.
     * @playerNameNumber
     */
    public  ArrayList<PlayerDetails> getPlayerData(){
         Bundle playerData = getIntent().getExtras();
        playerNameNumber = playerData.getParcelableArrayList("playerList");
        return playerNameNumber;
    }

    /***
     * donePicking will send through the picked player list.
     */
    private void donePicking(){

        switch (whichActivityToStart) {
            case 0:
            Intent backToComposeMessage = new Intent(this, ComposeMessage.class);
            pickedPlayers = contactPickerAdapter.donePickingPlayers();
            if (!pickedPlayers.myPlayers.isEmpty()) {
                addPlayersToTheDatabase();
                backToComposeMessage.putParcelableArrayListExtra("playerList", pickedPlayers.myPlayers);
            }
            startActivity(backToComposeMessage);
                break;
            case 1:
                Intent backToFullPlayerList = new Intent(this, FullPlayerList.class);
                pickedPlayers = contactPickerAdapter.donePickingPlayers();

                addPlayersToTheDatabase();

                if (!pickedPlayers.myPlayers.isEmpty()) {
                    backToFullPlayerList.putParcelableArrayListExtra("list", pickedPlayers.myPlayers);
                }
                startActivity(backToFullPlayerList);
                break;
            case 2:
                Intent backToCreateAutomateGame = new Intent(this, CreateAutomatedGame.class);
                pickedPlayers = contactPickerAdapter.donePickingPlayers();
                if(!pickedPlayers.myPlayers.isEmpty()){
                    backToCreateAutomateGame.putExtra("playerList",pickedPlayers.myPlayers);
                }
                startActivity(backToCreateAutomateGame);
                break;
            case 3:
                pickedPlayers = contactPickerAdapter.donePickingPlayers();
                gameDetailsFromDatabase.agAddPlayers(pickedPlayers.myPlayers, gameID);
                Intent backToListOfAgGames = new Intent(this, MainAutomateGames.class);
                startActivity(backToListOfAgGames);
                break;
        }
    }

    /**
     * This will set whichAcitivtyToStart to a certain number
     * That number represents which activity to send it to.
     * Number is set by boolean from calling activity
     */
    private void SendNewPlayerListToCalledActivity(){
        if(intentThatStartedThisActivity.getBoolean("FullPlayerListBoolean")){
            whichActivityToStart = 1;
            addingMorePlayers = intentThatStartedThisActivity.getParcelableArrayList("FullPlayerList");
        }
        else if(intentThatStartedThisActivity.getBoolean("createAutomateGameTrue")){
            whichActivityToStart = 2;
        }
        else if(intentThatStartedThisActivity.getBoolean("agAddPlayer")){
            whichActivityToStart =  3;
            gameID = intentThatStartedThisActivity.getInt("gameID");
        }
    }

    private void addPlayersToTheDatabase(){

    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                playerDatabase.beginTransaction();
                playerDatabase.delete(TABLE_NAME,null,null);

            }
            catch (SQLException e){
                throw e;
            }
            finally {
                playerDatabase.endTransaction();
            }
            playerDatabase = ComposeMessage.addPlayerToTheDatabase(playerDatabase, pickedPlayers.myPlayers);
             playerDatabase.query(TABLE_NAME,
                    null,null,null,
                    null,null, PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NAME);
          }
        }).start();
        }




    @Override
    public void addTvDisplayPlayersPicked(String playerName) {

       playersNameFromCPRVA.add(playerName);

        tvDisplayPlayersPicked.setText(getString(R.string.totalPlayersPicked,playersNameFromCPRVA.size()));

        displayPlayersPicked();
    }

    public void removeTvDisplayPlayersPicked(String playerName){
        playersNameFromCPRVA.remove(playerName);

        tvDisplayPlayersPicked.setText(getString(R.string.totalPlayersPicked,playersNameFromCPRVA.size()));
        displayPlayersPicked();
    }
    private void displayPlayersPicked(){
        StringBuilder stringBuilder = new StringBuilder();

        for(String i : playersNameFromCPRVA){
            stringBuilder.append("--" + i);
        }
        tvPickedPlayersFromContactPicker.setText("");
        tvPickedPlayersFromContactPicker.setText(stringBuilder.toString());

    }

}


