package com.anupcowkur.piwiksample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anupcowkur.piwiksdk.PiwikClient;

public class ActivityC extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);
        ((Button)findViewById(R.id.btn_activityC)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PiwikClient.trackEvent(ActivityC.this, "onClick", null);
                startActivity(new Intent(ActivityC.this, MainActivity.class));
                finish();
            }
        });

    }
}