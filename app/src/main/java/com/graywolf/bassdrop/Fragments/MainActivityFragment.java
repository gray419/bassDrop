package com.graywolf.bassdrop.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.graywolf.bassdrop.Constants;
import com.graywolf.bassdrop.PlayActivity;
import com.graywolf.bassdrop.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivityFragment extends Fragment {
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRootView = rootView;

        checkForHighScore();

        MyTimerTask myTask = new MyTimerTask();
        Timer myTimer = new Timer();

        myTimer.schedule(myTask,1000,4000);

        Button playButton = (Button) rootView.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity().getApplicationContext(), PlayActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        checkForHighScore();
    }

    private void checkForHighScore() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        String highScore = sharedPrefs.getString(Constants.HIGH_SCORE,"");

        TextView highScoreTextView = (TextView) mRootView.findViewById(R.id.highscoreTextView);
        highScoreTextView.setText("No Highscore Yet");

        if(!highScore.isEmpty()){
            highScoreTextView.setText("Highscore is: "+ highScore);
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run(){

            if(isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.Wobble)
                                .duration(700)
                                .playOn(mRootView.findViewById(R.id.dropTextView));
                    }
                });
            }
        }
    }
}
