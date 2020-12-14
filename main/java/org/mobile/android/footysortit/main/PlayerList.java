package org.mobile.android.footysortit.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yhaq on 22/03/2018.
 */

public class PlayerList implements Parcelable{


  public  ArrayList<PlayerDetails> myPlayers = new ArrayList<>();

      public PlayerList(){

    }


    void addPlayer(PlayerDetails player){
        myPlayers.add(player);

        }

        void removePlayer(int player){
           myPlayers.remove(player);
        }


    void removePlayerMethod(String number){

        for (PlayerDetails i : myPlayers) {
            if (i.number.equals(number)) {
                myPlayers.remove(i);
                break;
            }
        }

    }
      void sortMyPlayers(PlayerList playerList) {

          Collections.sort(playerList.myPlayers, new Comparator<PlayerDetails>() {
              @Override
              public int compare(PlayerDetails o1, PlayerDetails o2) {
                  return o1.name.compareTo(o2.name);
              }
          });
      }

    @Override
    public int describeContents() {
    return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag){
           dest.writeTypedList(this.myPlayers);
    }

    protected PlayerList(Parcel in){
           this.myPlayers = in.createTypedArrayList(PlayerDetails.CREATOR);
    }

    public static final Parcelable.Creator<PlayerList> CREATOR = new Parcelable.Creator<PlayerList>(){
           @Override
        public PlayerList createFromParcel(Parcel source){
               return new PlayerList(source);
           }
           @Override
        public PlayerList[] newArray(int size){
               return new PlayerList[size];
           }
    };
}
