package com.example.telepastev4;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import javax.net.ssl.HttpsURLConnection;

public class ServerRequest {
    static InputStream is = null;
    static JSONObject jObj = null;
    static Uri.Builder builder =null;
    static HttpURLConnection conn=null;
    static String json = "";
    public ServerRequest() {
    }
    public JSONObject getJSONFromUrl(String url, ContentValues params) {
        try {
//          DefaultHttpClient httpClient = new DefaultHttpClient();
//          HttpPost httpPost = new HttpPost(url);
//          httpPost.setEntity(new UrlEncodedFormEntity(params));
//          HttpResponse httpResponse = httpClient.execute(httpPost);
//          HttpEntity httpEntity = httpResponse.getEntity();
//          is = httpEntity.getContent();
            URL myURL = new URL (url);
            HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            String query = builder.build().getEncodedQuery();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();

//            OutputStream os = conn.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
//            osw.write(jObj.toString());
//            System.out.println("hey")
//            osw.flush();
//            osw.close();

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                json = sb.toString();
                Log.e("JSON", json);
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }


            conn.disconnect();



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }
    JSONObject jobj;
    public JSONObject getJSON(String url, ContentValues params) {
        Params param = new Params(url, params);
        Request myTask = new Request();
        try{
            jobj= myTask.execute(param).get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }
        return jobj;
    }
    private static class Params {
        String url;
        ContentValues params;
        Params(String url, ContentValues params) {
            this.url = url;
            this.params = params;
        }
    }
    private class Request extends AsyncTask<Params, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(Params... args) {
            ServerRequest request;
            request = new ServerRequest();
            JSONObject json = request.getJSONFromUrl(args[0].url,args[0].params);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
        }
    }
}