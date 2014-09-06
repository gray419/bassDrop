package com.graywolf.bassdrop.Fragments;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

    Handler customHandler = new Handler();

    private TextView mTimerValue;
    private View mRootView;

    private AdView mBannerAdView;

    public PlayActivityFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        mRootView = rootView;

        mBannerAdView = new AdView(getActivity().getApplicationContext());
        mBannerAdView.setAdSize(AdSize.BANNER);
        mBannerAdView.setAdUnitId("ca-app-pub-6013564165619332/177915880");

        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.linearlayoutAd);
        linearLayout.addView(mBannerAdView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("41A8C241CE3F27B022E69AE9F5E4625D")
                .build();

        // Start loading the ad in the background.
        mBannerAdView.loadAd(adRequest);

        mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.main_build);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        final Button buildButton = (Button) rootView.findViewById(R.id.buildButton);
        buildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();

                if(!mBuildUpStarted){
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.build_up_1);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                    mBuildUpStarted = true;
                    buildButton.setText("Not Yet!");
                }
                else if(mBuildUpStarted){
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.main_build);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                    mBuildUpStarted = false;
                    buildButton.setText("Build Up");
                }
            }
        });

        Button dropTheBassButton = (Button) rootView.findViewById(R.id.bassButton);
        dropTheBassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buildButton.setEnabled(false);

                mMediaPlayer.stop();

                mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.main_drop_lonley_island);
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
