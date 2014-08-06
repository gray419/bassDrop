package com.graywolf.bassdrop;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class PlayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private long startTime = 0L;
        private long timeInMilliseconds = 0L;
        private long timeSwapBuff = 0L;
        private long updatedTime = 0L;
        private int mBuildCounter= 0;

        private MediaPlayer mMediaPlayer;

        Handler customHandler = new Handler();

        private TextView mTimerValue;
        private View mRootView;

        public PlaceholderFragment() {
        }

        @Override
        public void onPause(){
            super.onPause();
            mMediaPlayer.stop();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_play, container, false);
            mRootView = rootView;

            mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.carnage_build_2);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

            final Button buildButton = (Button) rootView.findViewById(R.id.buildButton);
            buildButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMediaPlayer.stop();

                    if(mBuildCounter==0){
                        mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.carnage_buildup_1);
                        mMediaPlayer.setLooping(true);
                        mMediaPlayer.start();
                        mBuildCounter++;
                        buildButton.setText("More Build");
                    }
                    else if(mBuildCounter==1){
                        mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.carnage_buildup_2);
                        mMediaPlayer.setLooping(true);
                        mMediaPlayer.start();
                        buildButton.setVisibility(View.INVISIBLE);
                    }
                }
            });

            Button dropTheBassButton = (Button) rootView.findViewById(R.id.bassButton);
            dropTheBassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mMediaPlayer.stop();

                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.carnage_drop);
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
}
