package org.mobile.android.footysortit.main;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mobile.android.footysortit.R;
import org.mobile.android.footysortit.automategames.GameDetailsFromDatabase;


public class FootballPitchMainAdapter extends RecyclerView.Adapter<FootballPitchMainAdapter.ViewHolder>{
    private Bitmap bImage;
    private GameDetailsFromDatabase gameDetailsFromDatabase = new GameDetailsFromDatabase();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout linearLayoutFull;
        private TextView rvPlayerNameTop;


        private ViewHolder(View p) {
            super(p);
            linearLayoutFull = p.findViewById(R.id.linerLayoutPlayerFull);
            rvPlayerNameTop = p.findViewById(R.id.playerNameTop);

        }
    }

    public FootballPitchMainAdapter(Context context, Object id){
        Context pContext = context;
        int playerID = (int) id;
    }


    public FootballPitchMainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_name_and_top_scheme,
                parent,false);
        FootballPitchMainAdapter.ViewHolder vh = new FootballPitchMainAdapter.ViewHolder(v);

        return vh;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(final FootballPitchMainAdapter.ViewHolder holder, int position) {

        holder.rvPlayerNameTop.setText(gameDetailsFromDatabase.returnPlayerName(position));
        holder.rvPlayerNameTop.setTextColor(Color.GREEN);

        FootballPitchMain.passHolder(holder.linearLayoutFull,bImage); //to avoid null ref on getwidth in the main activity

        holder.linearLayoutFull.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(holder.linearLayoutFull);

                    holder.linearLayoutFull.startDrag(data, shadowBuilder, holder.linearLayoutFull, 0);
                    holder.linearLayoutFull.setVisibility(View.INVISIBLE);


                    holder.linearLayoutFull.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    holder.linearLayoutFull.layout(0, 0, holder.linearLayoutFull.getMeasuredWidth(), holder.linearLayoutFull.getMeasuredHeight());
                    holder.linearLayoutFull.buildDrawingCache();
                    bImage = Bitmap.createBitmap(holder.linearLayoutFull.getDrawingCache());
                    holder.linearLayoutFull.setDrawingCacheEnabled(false);

                    FootballPitchMain.passHolder(holder.linearLayoutFull,bImage);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

    }

    public int getItemCount() {
        return gameDetailsFromDatabase.totalPlayers();
    }
}
