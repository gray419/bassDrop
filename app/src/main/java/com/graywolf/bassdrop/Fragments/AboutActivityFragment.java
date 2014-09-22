package com.graywolf.bassdrop.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.graywolf.bassdrop.R;
import com.graywolf.bassdrop.WillTheBassDropApplication;

import java.util.List;


public class AboutActivityFragment extends Fragment {

    private Tracker mTracker;

    public AboutActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        Button emailButton = (Button) rootView.findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trackRequestMade("email");
                if(!createShareIntent("gmail")){
                    createShareIntent("email");
                }
            }
        });

        Button tweetButton = (Button) rootView.findViewById(R.id.tweetButton);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackRequestMade("twitter");
                createShareIntent("twitter");
            }
        });

        TextView samTextView = (TextView) rootView.findViewById(R.id.featuredArtist1);
        samTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView brillzTextView = (TextView) rootView.findViewById(R.id.featuredArtist2);
        brillzTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView etcTextView = (TextView) rootView.findViewById(R.id.featuredArtist4);
        etcTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView carnageTextView = (TextView) rootView.findViewById(R.id.featuredArtist5);
        carnageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView gregoSaltoTextView = (TextView) rootView.findViewById(R.id.featuredArtist6);
        gregoSaltoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView wiwekTextView = (TextView) rootView.findViewById(R.id.featuredArtist7);
        wiwekTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView djsankeTextView = (TextView) rootView.findViewById(R.id.featuredArtist3);
        djsankeTextView.setMovementMethod(LinkMovementMethod.getInstance());

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

    private void trackRequestMade(String method){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("About Activity")
                .setAction("Drop Request Method")
                .setLabel(method)
                .build());
    }

}
