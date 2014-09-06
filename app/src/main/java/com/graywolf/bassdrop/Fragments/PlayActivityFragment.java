package com.graywolf.bassdrop.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.graywolf.bassdrop.R;

public class PlayActivityFragment extends Fragment{
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean mBuildUpStarted = false;

    private MediaPlayer mMediaPlayer;
    private Handler customHandler = new Handler();
    private TextView mTimerValue;
    private View mRootView;
    private AdView mBannerAdView;
    private Spinner mDropSelectorSpinner;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        mRootView = rootView;
        mContext = getActivity().getApplicationContext();

        initBannerAd();

        mMediaPlayer = MediaPlayer.create(mContext, R.raw.main_build);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        final Button dropTheBassButton = (Button) rootView.findViewById(R.id.bassButton);
        final Spinner spinner = (Spinner) mRootView.findViewById(R.id.dropSelecterSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                        R.array.drop_array, R.layout.drop_spinner_item);
        // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.spinner_drop_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        final Button buildButton = (Button) rootView.findViewById(R.id.buildButton);
        buildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();

                if(!mBuildUpStarted){
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.build_up_1);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                    mBuildUpStarted = true;
                    buildButton.setText("Not Yet!");

                    //Show Bass Option
                    dropTheBassButton.setVisibility(View.VISIBLE);
                }
                else if(mBuildUpStarted){
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.main_build);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                    mBuildUpStarted = false;
                    buildButton.setText("Build Up");

                    //Hide Bass Button
                    dropTheBassButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        dropTheBassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buildButton.setEnabled(false);

                mMediaPlayer.stop();

                int selectedDrop = spinner.getSelectedItemPosition();
                int songResourceId=0;

                switch (selectedDrop){
                    case 0:
                        songResourceId = R.raw.main_drop_lonley_island;
                        break;
                    case 1:
                        songResourceId = R.raw.drop_snake_lunatic;
                        break;
                    case 2:
                        songResourceId = R.raw.drop_salto_wiwek_onyourmark;
                        break;
                    case 3:
                        songResourceId = R.raw.drop_brillz_swoop;
                        break;
                    case 4:
                        songResourceId = R.raw.carnage_drop;
                        break;
                }

                mMediaPlayer = MediaPlayer.create(mContext, songResourceId);
                mMediaPlayer.start();

                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);

                YoYo.with(Techniques.RubberBand)
                        .duration(1400)
                        .playOn(mRootView.findViewById(R.id.timerTextView));
            }
        });

        mTimerValue = (TextView) rootView.findViewById(R.id.timerTextView);

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        return rootView;
    }



    @Override
    public void onPause(){
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    public void onStop(){
        super.onStop();

        mMediaPlayer.release();
    }

    private void initBannerAd() {
        mBannerAdView = new AdView(mContext);
        mBannerAdView.setAdSize(AdSize.BANNER);
        mBannerAdView.setAdUnitId("ca-app-pub-6013564165619332/177915880");

        LinearLayout linearLayout = (LinearLayout) mRootView.findViewById(R.id.linearlayoutAd);
        linearLayout.addView(mBannerAdView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("41A8C241CE3F27B022E69AE9F5E4625D")
                .build();

        // Start loading the ad in the background.
        mBannerAdView.loadAd(adRequest);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hours = mins / 60;

            secs = secs % 60;

            int milliseconds = (int) (updatedTime % 1000);

            mTimerValue.setText("" + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%01d", milliseconds));

            customHandler.postDelayed(this, 0);
        }
    };
}
