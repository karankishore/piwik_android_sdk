package com.anupcowkur.piwiksample;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.anupcowkur.piwiksdk.PiwikClient;

public class MainActivity extends ListActivity {

    private static Sample[] mSamples;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the list of samples.
        mSamples = new Sample[]{new Sample(R.string.title_crossfade, CrossfadeActivity.class), new Sample(R.string.title_card_flip, CardFlipActivity.class), new Sample(R.string.title_screen_slide, ScreenSlideActivity.class), new Sample(R.string.title_zoom, ZoomActivity.class), new Sample(R.string.title_layout_changes, LayoutChangesActivity.class),};

        setListAdapter(new ArrayAdapter<Sample>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mSamples));

        PiwikClient.initPiwik(this, "http://www.mantish.com/piwik/piwik.php", null);
        PiwikClient.trackEvent(this, "SampleHome", null);
        PiwikClient.syncImmediately();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // Launch the sample associated with this list position.
        startActivity(new Intent(MainActivity.this, mSamples[position].activityClass));
    }

    /**
     * This class describes an individual sample (the sample title, and the activity class that
     * demonstrates this sample).
     */
    private class Sample {
        private CharSequence title;
        private Class<? extends Activity> activityClass;

        public Sample(int titleResId, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.title = getResources().getString(titleResId);
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }
}
