package org.mobile.android.footysortit.main;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

public class AllContacts extends Activity{
    PlayerList playerNameNumber = new PlayerList();
    Activity calledActivity;
    int pos = 0;
    int exists = 0;

    public AllContacts(Activity activity){
        calledActivity = activity;
    }

    /**
     * Get all contacts from the users phone but only their name and number
     *
     * @return
     */

    public PlayerList getContacts(){
        ContentResolver cr = calledActivity.getContentResolver();
        Cursor contactList = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (contactList.moveToNext()){
            PlayerDetails contactPlayer = new PlayerDetails();
            contactPlayer.name = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
           contactPlayer.number = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).
                                  replace(" ","").replace("-","");

            //this is supposed to only get the contacts name and number once. To stop the name and numbers repeating just because there is a space
            //in the number
            for( int i = 0; i < playerNameNumber.myPlayers.size(); i++){
               if(playerNameNumber.myPlayers.get(i).number.contains(contactPlayer.number)) {
                    exists = 1;
                    break;
                }
            }

            if(exists == 0) {
                playerNameNumber.myPlayers.add(pos, contactPlayer);
                pos++;
            }
            else{
                exists =0;
            }

        }

        playerNameNumber.sortMyPlayers(playerNameNumber);
        contactList.close();
        return playerNameNumber;
    }

    //The above works but in future consider using sets

}


