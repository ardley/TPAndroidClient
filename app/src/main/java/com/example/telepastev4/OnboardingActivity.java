package com.example.telepastev4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class OnboardingActivity extends Activity {

    public static final String PREFERENCESSETNAME = "Onb-InstID-DevType" ;
    public static final String installIDKey = "InstallID";
    public static final String deviceTypeKey = "deviceType";
    public static final String installRHKey = "InstallRandHex";
    ServerRequest2 sr;
    ContentValues params;
    SharedPreferences sharedprefset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_onboarding);
        String installIDval = "--";
        try {  //indenting is conceptual organization on thie try/catch block, not actual syntax indenting
            installIDval = new MakeInstallIDTask().execute().get();
        } catch (InterruptedException e) {
            Log.e("MakeInstallID AsyncTask", "Error on .execute().get()(InterruptedException) " + e.toString());
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e("MakeInstallID AsyncTask", "Error on .execute().get()(ExecutionException) " + e.toString());
            e.printStackTrace();
        }
        String installRHval = "--";
        try {  //indenting is conceptual organization on this try/catch block, not actual syntax indenting
            installRHval = new MakeInstallRHTask().execute().get();
        } catch (InterruptedException e) {
            Log.e("MakeInstallRH AsyncTask", "Error on .execute().get()(InterruptedException) " + e.toString());
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e("MakeInstallRH AsyncTask", "Error on .execute().get()(ExecutionException) " + e.toString());
            e.printStackTrace();
        }
        sr = new ServerRequest2();
        sharedprefset = getSharedPreferences(PREFERENCESSETNAME, Context.MODE_PRIVATE);
        Editor editor = sharedprefset.edit();
        editor.putString(deviceTypeKey, getAndroidDeviceType());
        editor.putString(installIDKey, installIDval);
        editor.putString(installRHKey, installRHval);
        editor.commit();
        //on SharedPreferences.getString(key, default) key is key in sharedprefs, default is what's returned if key not found or null)
        Toast.makeText(getBaseContext(), sharedprefset.getString(deviceTypeKey, "no devtype got")+" and "+ sharedprefset.getString(installIDKey,  "noinstID"), Toast.LENGTH_LONG).show();
        params = new ContentValues();
        params.put(deviceTypeKey, sharedprefset.getString(deviceTypeKey, "no devtype got"));
        params.put(installIDKey, sharedprefset.getString(installIDKey,  "noinstID"));
        params.put(installRHKey, sharedprefset.getString(installRHKey, "noRandHexPassgot"));
        Log.d("params sent SvRqst():", params.toString());
        JSONObject json = sr.getJSON("http://sean5.das.perforce.com:3000/api/testing", params);
        String jsonresponsestr="jsonresponsestr initialized but has not had value fed from server response JSONobj";



        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }



        try {
            jsonresponsestr = json.getString("response");
        } catch (JSONException e) {
            Log.e("JSON Parsing", "JSON Exception parsing response string from JSONObj returned by server in OnboardingActivity");
            e.printStackTrace();
        }
        Toast.makeText(getBaseContext(), jsonresponsestr, Toast.LENGTH_LONG).show();

        finish();
    }

    private class MakeInstallIDTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            Time now = new Time();
            now.setToNow();
            long nowmillis = now.toMillis(false);
            int rando = (int) (10000*Math.random());
            System.out.println(rando);
            String result=(nowmillis+"-"+rando);
            return result;//should actually make the sha256 of this with the rand and send that as uuid.
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private class MakeInstallRHTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            String result = Integer.toString((int) Math.floor(Math.random()*2147483647), 36);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }



    private String getAndroidDeviceType(){
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalizeFirstChar(model);
        }
        else {
            return capitalizeFirstChar(manufacturer) + " " + model;
        }
    }


    private String capitalizeFirstChar(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        }
        else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


}
