package com.anupcowkur.piwiksample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anupcowkur.piwiksdk.PiwikClient;

public class ActivityB extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        ((Button)findViewById(R.id.btn_activityB)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PiwikClient.trackEvent(ActivityB.this, "onClick", null);
                startActivity(new Intent(ActivityB.this, ActivityC.class));
                finish();
            }
        });

    }
}