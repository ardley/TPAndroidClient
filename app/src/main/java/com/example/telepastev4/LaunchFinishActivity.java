package com.example.telepastev4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LaunchFinishActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_finish);
        Intent intent = new Intent(this, LaunchBR.class);
        sendBroadcast(intent);
        finish();
    }
}
//This Activity ONLY Launches the rest of the app in a BR, then gets out of the way.  That's ALL.