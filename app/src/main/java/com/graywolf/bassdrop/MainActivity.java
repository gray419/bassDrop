package com.graywolf.bassdrop;

import android.app.Activity;
import android.os.Bundle;

import com.graywolf.bassdrop.Fragments.MainActivityFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainActivityFragment())
                    .commit();
        }
    }
}
