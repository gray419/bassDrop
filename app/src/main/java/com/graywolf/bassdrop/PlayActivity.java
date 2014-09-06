package com.graywolf.bassdrop;

import android.app.Activity;
import android.os.Bundle;

import com.graywolf.bassdrop.Fragments.PlayActivityFragment;

public class PlayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlayActivityFragment())
                    .commit();
        }
    }
}
