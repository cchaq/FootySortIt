package org.mobile.android.footysortit.main;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

/**
 * Created by yhaq on 22/03/2018.
 */

public class PlayerDetails implements Parcelable{
    int id;
    public String name;
    public String number;


    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){          //Write your objects data to the passed-in Parcel
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.number);

    }

    public PlayerDetails(){

    }

    protected PlayerDetails(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.number = in.readString();
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<PlayerDetails> CREATOR = new Parcelable.Creator<PlayerDetails>(){
      @Override
      public PlayerDetails createFromParcel(Parcel source){
          return new PlayerDetails(source);
      }

      @Override
        public PlayerDetails[] newArray(int size){
          return new PlayerDetails[size];
      }
    };


    /**
     * Main game database columns
     */
    public static final class PlayerTableEntry implements BaseColumns{
        public static final String TABLE_NAME = "playerTable";
        public static final String COLUMN_PLAYER_NAME = "playerName";
        public static final String COLUMN_PLAYER_NUMBER = "playerNumber";
        public static final String COLUMN_IS_PLAYING = "isPlaying";
    }

}
