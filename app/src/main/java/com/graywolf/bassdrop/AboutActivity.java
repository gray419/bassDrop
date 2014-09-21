package com.graywolf.bassdrop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);

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
}
