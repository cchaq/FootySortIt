package org.mobile.android.footysortit.automategames;

import android.content.Intent;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import org.mobile.android.footysortit.R;

public class MainAutomateGames extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RvAdapterListAutomateGames rvAdapterListAutomateGames;
    private RecyclerView.LayoutManager rvRVLayout;
    Button buttonViewFullPlayerListForThisGame;
    GameDetailsFromDatabase gameDetailsFromDatabase = new GameDetailsFromDatabase();
    int gameID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_automate_games);
        buttonViewFullPlayerListForThisGame = findViewById(R.id.AutomateGameButtonPickPlayers);


        //Setting up recycler view
        mRecyclerView = findViewById(R.id.rvAllAutomateGames);
        rvRVLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(rvRVLayout);
        rvAdapterListAutomateGames = new RvAdapterListAutomateGames(this);
        mRecyclerView.setAdapter(rvAdapterListAutomateGames);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                gameID = (int) viewHolder.itemView.getTag();
                gameDetailsFromDatabase.removeAgGameAndPlayers(gameID);
                rvAdapterListAutomateGames.notifyDataSetChanged();

            }
        }).attachToRecyclerView(mRecyclerView); //Got to learn to have a good naming convention!
    }

    /**
     * Go to create automate game.
     * @param view
     */
    public void startCreateNewGameForm(View view){
        Intent startCreateAutomateGame =  new Intent(this,CreateAutomatedGame.class);
        startActivity(startCreateAutomateGame);
    }

    /**
     * BaseColumns for Automate games table
     */

    public static final class AutomateGamesTable implements BaseColumns {
        public static final String TABLE_NAME = "automateGames";
        public static final String COLUMN_TEXT_MESSAGE = "textMessage";
        public static final String COLUMN_DATE_TO_SEND_TEXT = "dateToSendText";
        public static final String COLUMN_GAME_ID = "gameID";
        public static final String COLUMN_TEXT_SENT = "textSent";
    }

    public  final class AutomateGamePlayerTable implements BaseColumns{

        public static final String TABLE_NAME = "automatePlayerTable";
        public static final String COLUMN_PLAYER_NAME = "playerName";
        public static final String COLUMN_PLAYER_NUMBER = "playerNumber";
        public static final String COLUMN_IS_PLAYING = "isPlaying";
        public static final String COLUMN_GAME_PLAYER_ID = "gameID";
    }
}