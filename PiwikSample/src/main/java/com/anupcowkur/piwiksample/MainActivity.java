package com.anupcowkur.piwiksample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.anupcowkur.piwiksdk.PiwikClient;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PiwikClient.initPiwik(this, "http://www.mantish.com/piwik/piwik.php", null);
        PiwikClient.trackEvent(this, "home", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                PiwikClient.syncImmediately();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
