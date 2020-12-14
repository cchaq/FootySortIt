package org.mobile.android.footysortit.automategames;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.mobile.android.footysortit.main.FootySortItDatabase;
import org.mobile.android.footysortit.main.PlayerDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.provider.BaseColumns._ID;
import static org.mobile.android.footysortit.automategames.MainAutomateGames.AutomateGamePlayerTable.COLUMN_PLAYER_NUMBER;

public class GameDetailsFromDatabase extends Application{

    String textMessage;
    SQLiteDatabase db;
    Cursor cursor;

    /**
     * From the RvAdapter, we pass in the ID into the object.
     * Using that ID we will query the row from the automateGames table
     * return all the data we need to with return methods.
     */

    public GameDetailsFromDatabase(){
       FootySortItDatabase dbHelper = new FootySortItDatabase(MyAppContext.getContext());
        db = dbHelper.getReadableDatabase();

    }

    public void getGameID(int id){
        id = id + 1;
        cursor = db.rawQuery("select * from automateGames where gameID = "+id,null);
        cursor.moveToFirst();


    }

    public int getTotalAmountOfRowsInAutomateGames(){
        return (int) DatabaseUtils.queryNumEntries(db,"automateGames");

    }

    /**
     * DOES NOT RETURN TEXT MESSAGE BUT THE DATE INSTEAD
     * @param gameID
     * @return
     */

    public String getTextMessage(int gameID){
        Cursor cursor = db.rawQuery("select textMessage from automateGames where gameID = " + gameID,null);
        cursor.moveToFirst();
        textMessage = cursor.getString(0);
        cursor.close();
        return textMessage;
    }

    /**
     * Since RV the tag that was passed through from MainAutoMateGames, which was set via RV, starts from 0
     * and in CreateAutomatedGames we set the gameID by + 1 (think this was to stop duplicates and it searching on 0)
     * we have to increment the gameID passed in from AGFullPlayerListAdapter to match gameID within the
     * automatePlayerTable
     * @param gameID
     * @return
     */
    public Cursor agGetPlayerList(int gameID){
        //gameID += 1;
        cursor = db.rawQuery("select * from automatePlayerTable where gameID = "+ gameID,null);
        return cursor;
    }

    /**
     * This will get the cursor from AFfullplayerlist and the position
     * using that, we will query the name number and pass it back
     * @param cursorFromAGRVFullPlayer
     * @param position
     * @return
     */
    public String agReturnPlayerName(Cursor cursorFromAGRVFullPlayer, int position){

        cursorFromAGRVFullPlayer.moveToPosition(position);
        String playerName = cursorFromAGRVFullPlayer.getString(cursorFromAGRVFullPlayer.getColumnIndex(MainAutomateGames.AutomateGamePlayerTable.COLUMN_PLAYER_NAME));
        String playerNumber = cursorFromAGRVFullPlayer.getString(cursorFromAGRVFullPlayer.getColumnIndex(COLUMN_PLAYER_NUMBER));
        String nameAndNumber = playerName + "\n" + playerNumber;
        return nameAndNumber;
    }

    /**
     * Return the player status
     * which is when used to determine which colour image to show against their name
     * @param cursorPlayerStatus
     * @param position
     * @return
     */

    public int agIsPlayerPlayingStatus(Cursor cursorPlayerStatus, int position){
        cursorPlayerStatus.moveToPosition(position);
        return cursorPlayerStatus.getInt(cursorPlayerStatus.getColumnIndex(MainAutomateGames.AutomateGamePlayerTable.COLUMN_IS_PLAYING));
    }

    /**
     * Update the player status, when the user presses on the colour image.
     */
    public void agUpdatePlayerStatus(Cursor cursorUpdatePlayerStatus, int position, int status){
        cursorUpdatePlayerStatus.moveToPosition(position);
        String playerID = cursorUpdatePlayerStatus.getString(cursorUpdatePlayerStatus.getColumnIndex(_ID));
        ContentValues contentValues = new ContentValues();
        contentValues.put(MainAutomateGames.AutomateGamePlayerTable.COLUMN_IS_PLAYING,status);
        db.update("automatePlayerTable",contentValues, _ID + "= " + playerID
                ,null);
    }

    /**
     * delete a single player from the list
     * used when swiping the user
     * @param gameID
     * @param playerID
     */
    public void agDeletePlayerFromList(int gameID, int playerID){
        gameID +=1;
        cursor = db.rawQuery("select * from automatePlayerTable where gameID ="  + gameID,null);
        db.delete("automatePlayerTable",_ID + "=" + playerID,null);
    }

    /**
     * get the ID of the player from the DB and then use that to assign it.
     * @param cursor
     * @return
     */
    public int agGetPlayerID(Cursor cursor){
        //int id = cursor.getInt(cursor.getColumnIndex(MainAutomateGames.AutomateGamePlayerTable._ID));
        return cursor.getInt(cursor.getColumnIndex(MainAutomateGames.AutomateGamePlayerTable._ID));
    }

    public Cursor agReturnDateTime(){
        Cursor cursor =  db.rawQuery("select * from automateGames",null);
        return cursor;

    }

    /**
     * Get the players by the status of them playing
     * Map all there into a hashmap, and send the hash map back
     * @param gameID
     * @return
     */

    public Map agReturnIsPlayingAmount(int gameID){
        //gameID += 1;
        Map arePlaying  = new HashMap();
        Cursor isPlaying = db.rawQuery("select * from automatePlayerTable where gameID =" + gameID + " and isPlaying = 1",null);
        arePlaying.put("isPlaying", isPlaying.getCount());
        isPlaying = db.rawQuery(" select * from automatePlayerTable where gameID = " + gameID + " and isPlaying = 2 ", null);
        arePlaying.put("notPlaying",isPlaying.getCount());
        isPlaying = db.rawQuery("select * from automatePlayerTable where gameID = " + gameID + " and isPlaying = 0",null);
        arePlaying.put("maybePlaying",isPlaying.getCount());
        isPlaying.close();
        return arePlaying;
    }

    /**
     * Delete player specific to that game.
     * @param gameID
     */

    public void agDeletePlayersFromThisGame(int gameID){
        gameID += 1; //this is costing me a lot :(
        db.delete("automatePlayerTable","gameID = " + gameID,null);
    }


    /**
     * This is used to remove the entire game, from the main automate games list
     * @param gameID
     */
    public void removeAgGameAndPlayers(int gameID){
        //gameID +=1; The function returnAgGameID may have fixed me needing to increment by one!

        db.delete("automateGames","gameID = "+ gameID,null);
        db.delete("automatePlayerTable","gameID = " + gameID,null);
    }

    /**
     * Used to set the correct gameID against the each holder in the main automategame RV
     * Used to fix the issue of the gameID not being reduced by one when the previous game was deleted
     * @param position
     * @return
     */

    public Object returnAgGameID(int position){
       // position += 1;
        Cursor cursor =  db.rawQuery("select * from automateGames",null);
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex("gameID"));
    }

    /**
     * Used to send a text to the players who are playing depending on the game ID passed through
     * @param gameID
     * @return
     */

    public Cursor returnPlayersPlayingList(int gameID){
        return db.rawQuery("select playerNumber from automatePlayerTable where isPlaying = 1 AND gameID = " + gameID,null);

    }

    /**
     * This is used to add players to a specific game.
     */
    public void agAddPlayers(ArrayList<PlayerDetails> playerNameNumber, int gameID){
        for(PlayerDetails i : playerNameNumber){
            ContentValues cv = new ContentValues();
            cv.put("playerName",i.name);
            cv.put("playerNumber",i.number);
            cv.put("isPlaying",0);
            cv.put("gameID",gameID);
            db.insert("automatePlayerTable",null,cv);
        }
    }

    /**
     * Used to return the date and time to RV in main AG, to display the datetime for the user
     * @param gameID
     * @return
     */
    public String agReturnDateTimeForRV(int gameID){

      Cursor cursor = db.rawQuery("select dateToSendText from automateGames where gameID = " + gameID,null);
      cursor.moveToNext();
      return cursor.getString(0);
    }

    /**
     * Check if text has been sent. return a boolean depending on the answer
     * @param gameID
     * @return
     */
    public boolean agTextNeedsSending(int gameID){
        Cursor cursor = db.rawQuery("select textSent from automateGames where gameID = " + gameID,null);
        cursor.moveToNext();
        int mTextSent = cursor.getInt(cursor.getColumnIndex("textSent"));
        cursor.close();
        return mTextSent == 0;
    }

    /**
     * If the text has been sent out via the automated message, then we'll update the textsent to 1
     * So that when it checks it again it does not send it out repeatedly
     * @param gameID
     */

    public void agUpdateTextSentColumn(int gameID){
        ContentValues cv = new ContentValues();
        cv.put("textSent", 1);
        db.update("automateGames",cv,"gameID = " + gameID,null);
    }

    /**
     * The methods below are for the main game to return
     * the data from playerTable
     * @param id
     * @return
     */

    public String returnPlayerName(int id){
        Cursor cursor = db.rawQuery("select playerName from playerTable ",null);
        cursor.moveToPosition(id);
        return cursor.getString(cursor.getColumnIndex(PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NAME));
    }

    public int totalPlayers(){
        Cursor cursor = db.rawQuery("select * from playerTable",null);
        int x = cursor.getCount();
        cursor.close();
        return x;
    }
}
