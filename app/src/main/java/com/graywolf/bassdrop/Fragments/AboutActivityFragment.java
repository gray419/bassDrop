package com.graywolf.bassdrop.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.graywolf.bassdrop.R;
import com.graywolf.bassdrop.WillTheBassDropApplication;

import java.util.List;


public class AboutActivityFragment extends Fragment {


    public AboutActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        Tracker t = ((WillTheBassDropApplication) getActivity().getApplication()).getTracker(
                WillTheBassDropApplication.TrackerName.APP_TRACKER);

        t.setScreenName(this.getActivity().getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());

        Button emailButton = (Button) rootView.findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!createShareIntent("gmail")){
                    createShareIntent("email");
                }
            }
        });

        Button tweetButton = (Button) rootView.findViewById(R.id.tweetButton);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createShareIntent("twitter");
            }
        });

        return rootView;
    }

    private boolean createShareIntent(String type) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    if(type.contains("email")||type.contains("gmail")){
                        share.putExtra(Intent.EXTRA_EMAIL, new String[] { "willthebassdrop@gmail.com"});
                        share.putExtra(Intent.EXTRA_SUBJECT, "Drop Request");
                    }
                    else if(type.contains("twitter")){
                        share.putExtra(Intent.EXTRA_TEXT, "@willthebassdrop Drop Request: ");
                    }
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return found;

            startActivity(Intent.createChooser(share, "Select"));
        }
        startActivity(Intent.createChooser(share, "Select"));
        return found;
    }


}
