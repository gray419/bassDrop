package com.graywolf.bassdrop.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.graywolf.bassdrop.Constants;
import com.graywolf.bassdrop.R;
import com.graywolf.bassdrop.WillTheBassDropApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PlayActivityFragment extends Fragment{
    private long mStartTime = 0L;
    private long mTimeInMilliseconds = 0L;
    private long mTimeSwapBuff = 0L;
    private long mUpdatedTime = 0L;
    private boolean mBuildUpStarted = false;
    private MediaPlayer mMediaPlayer;
    private Handler customHandler = new Handler();
    private TextView mTimerValue;
    private View mRootView;
    private AdView mBannerAdView;
    private Spinner mDropSelectorSpinner;
    private Context mContext;
    private Boolean mHasBassDropped;
    private int mSongResourceId;
    private long mElapsedTime;
    private Tracker mTracker;
    private long mCurrentScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        mRootView = rootView;
        mContext = getActivity().getApplicationContext();
        mHasBassDropped = false;

        final SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor sharedPrefsEditor = sharedPref.edit();

        initBannerAd();

        mMediaPlayer = MediaPlayer.create(mContext, R.raw.main_build);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        final Button dropTheBassButton = (Button) rootView.findViewById(R.id.bassButton);

        final Button buildButton = (Button) rootView.findViewById(R.id.buildButton);
        buildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();

                if (!mBuildUpStarted) {
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.build_up_1);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                    mBuildUpStarted = true;
                    buildButton.setText("Not Yet!");

                    //Show Bass Option
                    dropTheBassButton.setVisibility(View.VISIBLE);
                } else if (mBuildUpStarted) {
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

                if(mHasBassDropped){

                    mMediaPlayer.stop();
                    mMediaPlayer = MediaPlayer.create(mContext, mSongResourceId);
                    mMediaPlayer.start();

                }
                else{
                    buildButton.setEnabled(false);

                    mHasBassDropped = true;
                    mMediaPlayer.stop();
                    mSongResourceId = 0;

                    switch (mDropSelectorSpinner.getSelectedItemPosition()) {
                        case 1:
                            mSongResourceId = R.raw.main_drop_lonley_island;
                            break;
                        case 2:
                            mSongResourceId = R.raw.drop_snake_lunatic;
                            break;
                        case 3:
                            mSongResourceId = R.raw.drop_salto_wiwek_onyourmark;
                            break;
                        case 4:
                            mSongResourceId = R.raw.drop_brillz_swoop;
                            break;
                        case 5:
                            mSongResourceId = R.raw.carnage_drop;
                            break;
                        default:
                            mSongResourceId = R.raw.main_drop_lonley_island;
                    }

                    mMediaPlayer = MediaPlayer.create(mContext, mSongResourceId);
                    mMediaPlayer.start();

                   long previousScore = parseTime(sharedPref.getString(Constants.HIGH_SCORE,"00:00:00"));
                   mCurrentScore = parseTime(mTimerValue.getText().toString());

                   if (mCurrentScore > previousScore){
                       sharedPrefsEditor.putString(Constants.HIGH_SCORE, mTimerValue.getText().toString());
                       sharedPrefsEditor.commit();

                       Toast toast = Toast.makeText(mContext, "New High Score!!", Toast.LENGTH_SHORT);
                       toast.show();
                   }

                    trackSelectedDrop();
                    trackBuildDuration();

                    mTimeSwapBuff += mTimeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);

                    YoYo.with(Techniques.RubberBand)
                            .duration(1400)
                            .playOn(mRootView.findViewById(R.id.timerTextView));
                }
            }
        });

        mDropSelectorSpinner = (Spinner) mRootView.findViewById(R.id.dropSelecterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.drop_array, R.layout.drop_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_dropdown_item);
        mDropSelectorSpinner.setAdapter(adapter);

        mTimerValue = (TextView) rootView.findViewById(R.id.timerTextView);

        mStartTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);

        mTracker = ((WillTheBassDropApplication) getActivity().getApplication()).getTracker(
                WillTheBassDropApplication.TrackerName.APP_TRACKER);
    }

    @Override
    public void onResume(){
        super.onResume();

        mTracker.setScreenName(this.getActivity().getClass().getSimpleName());
        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onStart(){
        super.onStart();
        if(!mHasBassDropped){
            mStartTime = SystemClock.uptimeMillis();
            mTimeSwapBuff = mElapsedTime;
            customHandler.postDelayed(updateTimerThread, 0);
            mMediaPlayer.start();
           }
        }

    @Override
    public void onStop(){
        super.onStop();
        mMediaPlayer.pause();
        mElapsedTime = parseTime(mTimerValue.getText().toString());
        customHandler.removeCallbacks(updateTimerThread);
    }

    @Override
    public void onPause(){
        super.onPause();
        mMediaPlayer.pause();
        customHandler.removeCallbacks(updateTimerThread);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    private void initBannerAd() {
        mBannerAdView = new AdView(mContext);
        mBannerAdView.setAdSize(AdSize.BANNER);
        mBannerAdView.setAdUnitId("ca-app-pub-6013564165619332/177915880");
        LinearLayout linearLayout = (LinearLayout) mRootView.findViewById(R.id.linearlayoutAd);
        linearLayout.addView(mBannerAdView);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("41A8C241CE3F27B022E69AE9F5E4625D")
                .build();

        // Start loading the ad in the background.
        mBannerAdView.loadAd(adRequest);
    }

    private  Runnable updateTimerThread = new Runnable() {

        public void run() {

            mTimeInMilliseconds = SystemClock.uptimeMillis() - mStartTime;

            mUpdatedTime = mTimeSwapBuff + mTimeInMilliseconds;

            int milliseconds = (int) ((mUpdatedTime / 10) % 100);
            int seconds = (int) (mUpdatedTime / 1000) % 60 ;
            int minutes = (int) ((mUpdatedTime / (1000*60)) % 60);

            mTimerValue.setText(
                    String.format("%02d:%02d:%02d",
                            minutes,
                            seconds,
                            milliseconds));

            customHandler.postDelayed(this, 0);
        }
    };

    private long parseTime(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SS", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        long millis = 0;

        try {

            Date date = sdf.parse(time);
            millis =  date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return millis;
    }

    private void trackSelectedDrop(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Play Activity")
                .setAction("Dropping Bass Initial")
                .setLabel(mDropSelectorSpinner.getSelectedItem().toString())
                .build());
    }

    private void trackBuildDuration(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Play Activity")
                .setAction("Time Spent Building")
                .setLabel(mTimerValue.getText().toString())
                .setValue(mCurrentScore)
                .build());
    }
}
