package org.mobile.android.footysortit.automategames;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.mobile.android.footysortit.R;

public class RvAdapterListAutomateGames extends RecyclerView.Adapter<RvAdapterListAutomateGames.ViewHolder>{

    private Context mContext;
    private GameDetailsFromDatabase gameDetailsFromDatabase = new GameDetailsFromDatabase();
    private int amountOfGames;
    private Object gameID;



    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvAgDate;
        Button buttonFullPlayerList;

        public ViewHolder(View v){
            super(v);
            tvAgDate = v.findViewById(R.id.aAutomatedGame);
            buttonFullPlayerList = v.findViewById(R.id.buttonViewPlayers);

        }
    }

    public RvAdapterListAutomateGames(Context context){
        mContext = context;
    }

    /**
     * Inflate our list of automate games for RV
     * @param parent
     * @param viewType
     * @return
     */

    @Override
    public RvAdapterListAutomateGames.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_automate_game_scheme,
                parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * if > 0 is for users who open this for the first time and have nothing in the DB.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RvAdapterListAutomateGames.ViewHolder holder, final int position) {


            gameDetailsFromDatabase.getGameID(position);//??
            int mGameID = (int) gameDetailsFromDatabase.returnAgGameID(position);
            holder.tvAgDate.setText(gameDetailsFromDatabase.agReturnDateTimeForRV(mGameID));
            holder.buttonFullPlayerList.setTag(position);
            holder.itemView.setTag( gameDetailsFromDatabase.returnAgGameID(position));
         //  gameID = gameDetailsFromDatabase.returnAgGameID(position);
            holder.buttonFullPlayerList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPlayersForThisGame((int) holder.buttonFullPlayerList.getTag());
                }
            });

    }

    @Override
    public int getItemCount() {
        amountOfGames = gameDetailsFromDatabase.getTotalAmountOfRowsInAutomateGames();
        return amountOfGames;
    }

    /**
     * Each button has an ID, we pass that through to make the query to the DB
     * to get the right player list
     * @param tagId
     */
    private void viewPlayersForThisGame(int tagId){
        Intent startViewPlayersRV =  new Intent(mContext, AGFullPlayerList.class);
        Bundle sendTagId = new Bundle();
        sendTagId.putInt("buttonTagId",tagId); // originally used
        sendTagId.putInt("correctGameID", (int) gameDetailsFromDatabase.returnAgGameID(tagId));
        startViewPlayersRV.putExtras(sendTagId);
        mContext.startActivity(startViewPlayersRV);
    }
}
