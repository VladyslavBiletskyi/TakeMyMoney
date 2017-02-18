package com.biletskyi.vladyslav.takemymoney;

import android.app.Activity;
import android.app.Notification;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vladyslav on 09.12.2016.
 */

public class HttpGETQuery extends AsyncTask<String,Void, String> {
    NetworkActivity sender;
    public HttpGETQuery(NetworkActivity cat){
        sender=cat;
    }
    @Override
    protected String doInBackground(String... params) {//[0] - host, [1] - token
        StringBuilder answer = new StringBuilder();
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (params.length>1){
                connection.setRequestProperty("Authorization","Bearer "+params[1]);
            }
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
        } catch (Exception e) {
            answer.append(e.getLocalizedMessage());
        } finally {
            return answer.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        sender.onBackgroundTaskFinish(s);
        super.onPostExecute(s);
    }
}
