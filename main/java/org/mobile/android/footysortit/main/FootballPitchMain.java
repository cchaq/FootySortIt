package org.mobile.android.footysortit.main;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.mobile.android.footysortit.R;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class FootballPitchMain extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FootballPitchMainAdapter footballPitchMainAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;
    private RelativeLayout pitchRelativeLayout;
    static ImageView ll;
    static RelativeLayout viewHolder;
    static Bitmap mBImage;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_pitch_main);
        pitchRelativeLayout = findViewById(R.id.mainPitch);
        pitchRelativeLayout.setOnDragListener(new MyDragListener());
        ll=findViewById(R.id.llTop);
        prefs = getSharedPreferences(getPackageName(),MODE_PRIVATE);

        recyclerView = findViewById(R.id.rvPlayerNameTops);
        rvLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));

        footballPitchMainAdapter = new FootballPitchMainAdapter(this, 1);
        recyclerView.setAdapter(footballPitchMainAdapter);
        recyclerView.setHasFixedSize(true);
        tutForPitch();

    }


    class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            if (event.getAction() ==   DragEvent.ACTION_DROP){
                final ImageView pitchTop = createDuplicateTop();
                int x_cord = (int) event.getX();
                int  y_cord = (int) event.getY();
                RelativeLayout.LayoutParams rLay = new RelativeLayout.LayoutParams(viewHolder.getWidth(),viewHolder.getHeight());
                rLay.leftMargin = x_cord-75;
                rLay.topMargin = y_cord-75;
                pitchTop.setLayoutParams(rLay);
                setPitchTopListener(pitchTop);
            }
            return true;
        }
    }

    /**
     * Pass in the image from the adapter and set it to the bitmap here
     * @param mViewHolder
     * @param aBitmap
     */

    public static void passHolder(RelativeLayout mViewHolder, Bitmap aBitmap){

        if(mViewHolder != null) {
            viewHolder = mViewHolder;
        }
        mBImage = aBitmap;

    }

    /**
     * Create a new image every time the user drags a player from RV
     * @return
     */

    private ImageView createDuplicateTop(){
       final ImageView duplicateTop = new ImageView(this);
        duplicateTop.setImageBitmap(mBImage);
        pitchRelativeLayout.addView(duplicateTop);
        return duplicateTop;
    }

    /**
     * Set on onTouchListener so we can move the players after we drop them
     * Had to recreate the Bitmap again using the same image
     * Because in the onDrag listener, we kept using the previous image, which meant
     * each player kept changing to the previous name
     * @param pitchTop
     */

    @SuppressLint("ClickableViewAccessibility")
    private void setPitchTopListener(final ImageView pitchTop){

        pitchTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(pitchTop);

                    pitchTop.startDrag(data, shadowBuilder, pitchTop, 0);
                    pitchTop.setVisibility(View.INVISIBLE);
                    pitchTop.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    pitchTop.layout(0, 0, pitchTop.getMeasuredWidth(), pitchTop.getMeasuredHeight());
                    pitchTop.buildDrawingCache();
                    Bitmap  bImage = Bitmap.createBitmap(pitchTop.getDrawingCache());
                    pitchTop.setDrawingCacheEnabled(false);

                    FootballPitchMain.passHolder(null,bImage);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
    }

    /**
     * Take a screenshot of the screen by getting the root decorview of the current window
     * save it to the users phone as a bitmap
     * then use URI to allow them to send it via an intent
     * This makes it easier for them to share their formation.
     * @param view
     */
    public void takeScreenShot(View view){
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap screenShotImage = v1.getDrawingCache();
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),screenShotImage,"title",null);
        Uri screenshot = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setType("*/*");
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,screenshot);
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.SMSNotSent)));

    }

    /**
     * Tut for new users
     * should of made a single class -_-
     */

    private void tutForPitch(){
        if(prefs.getBoolean(FootballPitchMain.class.getCanonicalName(),true)){
            prefs.edit().putBoolean(FootballPitchMain.class.getCanonicalName(),false).apply();
            new MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.rvPlayerNameTops)
                .setPrimaryText(R.string.tutPitchPlayers)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if(state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                            new MaterialTapTargetPrompt.Builder(FootballPitchMain.this)
                                    .setTarget(R.id.bScreenshot)
                                    .setPrimaryText(R.string.tutSharePitch)
                                    .show();
                        }
                    }
                })
                .show();
        }
    }
}