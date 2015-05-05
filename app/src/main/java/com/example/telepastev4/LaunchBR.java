package com.example.telepastev4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

public class LaunchBR extends BroadcastReceiver {

    public static final String PREFERENCESSETNAME = "Onb-InstID-DevType" ;
    public static final String Notfirsttime = "NotfirsttimeKey";
    SharedPreferences sharedprefset;
    Context launchBRContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "LaunchBR has launched", Toast.LENGTH_LONG).show();
        sharedprefset = context.getSharedPreferences(PREFERENCESSETNAME, Context.MODE_PRIVATE);
        launchBRContext = context;
        new DetermineIfLaunchOBTask().execute();
    }

    private class DetermineIfLaunchOBTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void...voids) {
            String resultToastString = "";
            if (sharedprefset.contains(Notfirsttime)){
                //finish();
                resultToastString= "This the firste tahme";
                String notfirsttime  = "true";
                Editor editor = sharedprefset.edit();
                editor.putString(Notfirsttime, notfirsttime);
                editor.commit();
                System.out.println(launchBRContext);
                Intent onbactivity = new Intent(launchBRContext, OnboardingActivity.class);
                System.out.println(onbactivity);
                onbactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchBRContext.startActivity(onbactivity);
            }
            else {
                //finish();
                resultToastString= "Not the first time";
            }
            return resultToastString;
        }
        @Override
        protected void onPostExecute(String toasttouse){
            Toast.makeText(launchBRContext, toasttouse, Toast.LENGTH_LONG).show();
        }

    }

}
