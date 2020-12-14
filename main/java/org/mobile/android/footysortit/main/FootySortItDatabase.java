package org.mobile.android.footysortit.main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mobile.android.footysortit.automategames.MainAutomateGames;

public class FootySortItDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FootySortItDatabase";
    private static final int DATABASE_VERSION = 4;

    final String SQL_CREATE_PLAYER_TABLE = "CREATE TABLE " +
            PlayerDetails.PlayerTableEntry.TABLE_NAME + "(" +
            PlayerDetails.PlayerTableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NAME + " TEXT NOT NULL, " +
            PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NUMBER + " TEXT NOT NULL, " +
            PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING + " INTEGER NOT NULL " +
            ");";
    final String SQL_CREATE_AUTOMATE_GAME_TABLE = " CREATE TABLE " +
            MainAutomateGames.AutomateGamesTable.TABLE_NAME + "(" +
            MainAutomateGames.AutomateGamesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MainAutomateGames.AutomateGamesTable.COLUMN_TEXT_MESSAGE + " TEXT NOT NULL, " +
            MainAutomateGames.AutomateGamesTable.COLUMN_DATE_TO_SEND_TEXT + " TEXT NOT NULL, " +
            MainAutomateGames.AutomateGamesTable.COLUMN_GAME_ID + " INTERGER NOT NULL, " +
            MainAutomateGames.AutomateGamesTable.COLUMN_TEXT_SENT + " INTEGER NOT NULL " +
            ");";
    final String CREATE_NEW_PLAYER_TABLE = " CREATE TABLE " +
            MainAutomateGames.AutomateGamePlayerTable.TABLE_NAME + "(" +
            MainAutomateGames.AutomateGamePlayerTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MainAutomateGames.AutomateGamePlayerTable.COLUMN_PLAYER_NAME + " TEXT NOT NULL, " +
            MainAutomateGames.AutomateGamePlayerTable.COLUMN_PLAYER_NUMBER + " TEXT NOT NULL, " +
            MainAutomateGames.AutomateGamePlayerTable.COLUMN_IS_PLAYING + " INTEGER NOT NULL, " +
            MainAutomateGames.AutomateGamePlayerTable.COLUMN_GAME_PLAYER_ID + " INTEGER NOT NULL " + //TODO could this be a FK?
            ");";



    public FootySortItDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_PLAYER_TABLE);
        db.execSQL(SQL_CREATE_AUTOMATE_GAME_TABLE);
        db.execSQL(CREATE_NEW_PLAYER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //this is used to update the DB version on the users phone without dropping the DB

        switch (DATABASE_VERSION) {
            case 4:
                db.execSQL(" DROP TABLE IF EXISTS " + MainAutomateGames.AutomateGamePlayerTable.TABLE_NAME);
                db.execSQL(" DROP TABLE IF EXISTS " + MainAutomateGames.AutomateGamesTable.TABLE_NAME);
                db.execSQL(SQL_CREATE_AUTOMATE_GAME_TABLE);
                db.execSQL(CREATE_NEW_PLAYER_TABLE);
                break;
        }

    }
}

