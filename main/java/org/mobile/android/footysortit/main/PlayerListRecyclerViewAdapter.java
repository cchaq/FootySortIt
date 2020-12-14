package org.mobile.android.footysortit.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mobile.android.footysortit.R;

import java.util.ArrayList;

public class PlayerListRecyclerViewAdapter extends RecyclerView.Adapter<PlayerListRecyclerViewAdapter.ViewHolder> {

    ArrayList<PlayerDetails> playerDataSet;
    private Cursor pCursor;
    private Context pContext;
    DatabaseUpdateInterface updatePlayerDB;
    int answer;
    //int[] playerStatus = new int[]{0,1,2};

    /**
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView playerView;
        public ImageView greenView;
        public ImageView amberView;
        public ImageView redView;
        private View[] allTheViews;

        private ViewHolder(View p) {
            super(p);
            playerView = p.findViewById(R.id.aPlayerInTheList);
            greenView = p.findViewById(R.id.isPlaying);
            amberView = p.findViewById(R.id.mightBePlaying);
            redView = p.findViewById(R.id.notPlaying);
            allTheViews = new View[]{greenView, amberView, redView};

        }
    }

    public PlayerListRecyclerViewAdapter(Context context, Cursor cursor, ArrayList<PlayerDetails> aPlayerList, DatabaseUpdateInterface updatePlayerDB) {
        playerDataSet = aPlayerList;
        this.pCursor = cursor;
        this.pContext = context;
        this.updatePlayerDB = updatePlayerDB;
        // mCount = count;

    }

    @Override
    public PlayerListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.is_player_playing_scheme,
                parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (!pCursor.moveToPosition(position)) {
            return;
        }

        String name = pCursor.getString(pCursor.getColumnIndex(PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NAME));
        String number = pCursor.getString(pCursor.getColumnIndex(PlayerDetails.PlayerTableEntry.COLUMN_PLAYER_NUMBER));
        Long id = pCursor.getLong(pCursor.getColumnIndex(PlayerDetails.PlayerTableEntry._ID));
        final long forUpdateID = id;
        holder.playerView.setTag(id);
        holder.itemView.setTag(id);
        holder.playerView.setText(name + "\n" + number);
        final ContentValues cv = new ContentValues();


        if (checkAnswer(forUpdateID) == 0) {
            holder.redView.setVisibility(View.VISIBLE);
            holder.greenView.setVisibility(View.INVISIBLE);
            holder.amberView.setVisibility(View.INVISIBLE);


        } else if (checkAnswer(forUpdateID) == 1) {
            holder.greenView.setVisibility(View.VISIBLE);
            holder.amberView.setVisibility(View.INVISIBLE);
            holder.redView.setVisibility(View.INVISIBLE);


        } else if (checkAnswer(forUpdateID) == 2) {
            holder.amberView.setVisibility(View.VISIBLE);
            holder.redView.setVisibility(View.INVISIBLE);
            holder.greenView.setVisibility(View.INVISIBLE);

        }

        holder.allTheViews[0].setOnClickListener(new View.OnClickListener() {

                                                     @Override
                                                     public void onClick(View view) {
                                                         ManualPlayingChange.notToPlaying(holder);
                                                         cv.put(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING,ManualPlayingChange.updateTheStatus());
                                                         updatePlayerDB.updatePlayerStatus(cv,forUpdateID);
                                                         updatePlayerDB.getNumbersForPlayerStatus();
                                                     }
                                                 }

        );

        holder.allTheViews[1].setOnClickListener(new View.OnClickListener() {

                                                     @Override
                                                     public void onClick(View view) {

                                                         ManualPlayingChange.notToPlaying(holder);
                                                         cv.put(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING,ManualPlayingChange.updateTheStatus());
                                                         updatePlayerDB.updatePlayerStatus(cv,forUpdateID);
                                                         updatePlayerDB.getNumbersForPlayerStatus();
                                                     }
                                                 }

        );

        holder.allTheViews[2].setOnClickListener(new View.OnClickListener() {

                                                     @Override
                                                     public void onClick(View view) {
                                                         ManualPlayingChange.notToPlaying(holder);
                                                         cv.put(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING,ManualPlayingChange.updateTheStatus());
                                                         updatePlayerDB.updatePlayerStatus(cv,forUpdateID);
                                                         updatePlayerDB.getNumbersForPlayerStatus();
                                                     }
                                                 }


        );



        //The above is code I need to revisit, there is a better way to do this
    }


    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (pCursor != null) {
            pCursor.close();
        }
        pCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {

        return pCursor.getCount();
    }

    private int checkAnswer(long id){
       answer = pCursor.getInt(pCursor.getColumnIndex(PlayerDetails.PlayerTableEntry.COLUMN_IS_PLAYING));
      //  answer = updatePlayerDB.getPlayerStatus(id);
        return answer;
    }

}
