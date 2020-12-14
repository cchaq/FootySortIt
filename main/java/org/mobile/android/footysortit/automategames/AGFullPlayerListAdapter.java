package org.mobile.android.footysortit.automategames;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mobile.android.footysortit.R;
import org.mobile.android.footysortit.main.DatabaseUpdateInterface;

public class AGFullPlayerListAdapter extends RecyclerView.Adapter<AGFullPlayerListAdapter.ViewHolder>{

    private GameDetailsFromDatabase gameDetailsFromDatabase = new GameDetailsFromDatabase();
    private Cursor mCursor;
    private Context pContext;
    private int playerID;
    DatabaseUpdateInterface databaseUpdateInterface;

    public  class ViewHolder extends RecyclerView.ViewHolder {
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
            allTheViews = new View[]{amberView, greenView, redView};

        }
    }

    /**
     * Get the passed through tag ID and assign it to playerID
     * which is used to search the automatePlayerTable to get the list of players
     * @param context
     * @param id
     */

    public AGFullPlayerListAdapter(Context context, Object id, DatabaseUpdateInterface databaseUpdateInterface){
        pContext = context;
        playerID = (int) id;
        this.databaseUpdateInterface = databaseUpdateInterface;

    }
    @Override
    public AGFullPlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.is_player_playing_scheme,
                parent, false);
        AGFullPlayerListAdapter.ViewHolder vh = new AGFullPlayerListAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final AGFullPlayerListAdapter.ViewHolder holder, final int position) {
        final Cursor playerInfoCursor = gameDetailsFromDatabase.agGetPlayerList(playerID);
        holder.playerView.setText( gameDetailsFromDatabase.agReturnPlayerName(playerInfoCursor,position));
        setColourStatusForPlayer(gameDetailsFromDatabase.agIsPlayerPlayingStatus(playerInfoCursor, position), holder);
        int id = gameDetailsFromDatabase.agGetPlayerID(playerInfoCursor);
        holder.itemView.setId(id);

        holder.allTheViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameDetailsFromDatabase.agUpdatePlayerStatus(playerInfoCursor,position,2);
                setColourStatusForPlayer(2, holder); //gameDetailsFromDatabase.agIsPlayerPlayingStatus(playerInfoCursor, position) - DB was not updating fast enough to be able to call an update?
                databaseUpdateInterface.getNumbersForPlayerStatus();
            }
        });
        holder.allTheViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameDetailsFromDatabase.agUpdatePlayerStatus(playerInfoCursor,position,0);
                setColourStatusForPlayer(0, holder);
                databaseUpdateInterface.getNumbersForPlayerStatus();
            }
        });
        holder.allTheViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameDetailsFromDatabase.agUpdatePlayerStatus(playerInfoCursor,position,1);
                setColourStatusForPlayer(1, holder);
                databaseUpdateInterface.getNumbersForPlayerStatus();
            }
        });

    }

    /**
     *  call agGetPlayerList using the playerID to get all the players corresponding to the ID
     * @return
     */
    @Override
    public int getItemCount() {
        mCursor = gameDetailsFromDatabase.agGetPlayerList(playerID);
        return mCursor.getCount();
    }

    private void setColourStatusForPlayer(int playerStatus, AGFullPlayerListAdapter.ViewHolder viewHolder){

        switch (playerStatus){
            case 0: viewHolder.greenView.setVisibility(View.INVISIBLE);
                    viewHolder.amberView.setVisibility(View.VISIBLE);
                    viewHolder.redView.setVisibility(View.INVISIBLE);
                    break;
            case 1: viewHolder.greenView.setVisibility(View.VISIBLE);
                    viewHolder.amberView.setVisibility(View.INVISIBLE);
                    viewHolder.redView.setVisibility(View.INVISIBLE);
                    break;
            case 2:viewHolder.greenView.setVisibility(View.INVISIBLE);
                    viewHolder.amberView.setVisibility(View.INVISIBLE);
                    viewHolder.redView.setVisibility(View.VISIBLE);
                    break;
        }

    }
}
