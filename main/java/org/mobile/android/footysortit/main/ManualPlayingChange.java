package org.mobile.android.footysortit.main;

import android.view.View;

public class ManualPlayingChange{

    private static int status;
   // private SQLiteDatabase playerDatabase;

    /**
     * Ok, so I messed up the number system to decide whether someone is playing or not
     * @param a
     */

    public static void notToPlaying(PlayerListRecyclerViewAdapter.ViewHolder a){
        if(a.redView.isShown()){
            a.redView.setVisibility(View.INVISIBLE);
            a.greenView.setVisibility(View.VISIBLE);
            status = 1 ;

        }
        else if (a.greenView.isShown()){
            a.greenView.setVisibility(View.INVISIBLE);
            a.amberView.setVisibility(View.VISIBLE);
            status = 2;

        } else if (a.amberView.isShown()) {
            a.amberView.setVisibility(View.INVISIBLE);
            a.redView.setVisibility(View.VISIBLE);
            status = 0;
        }

    }

    public static int updateTheStatus(){
        return status;
    }

}
