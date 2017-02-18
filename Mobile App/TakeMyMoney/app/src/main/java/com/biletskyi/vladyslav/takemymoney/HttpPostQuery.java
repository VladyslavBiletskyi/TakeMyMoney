package com.biletskyi.vladyslav.takemymoney;

import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Created by Vladyslav on 10.12.2016.
 */

public class HttpPostQuery extends AsyncTask<Pair<String,String>,Void,String> {
    NetworkActivity sender;
    public HttpPostQuery(NetworkActivity cat){
        sender=cat;
    }
    @Override
    protected String doInBackground(Pair<String,String>... params) {//[0][0] - host, [0][1] - token,[1]..[n-1] param_names. [2]..[n] params
        StringBuilder answer = new StringBuilder();
        try{URL url = new URL(params[0].first);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (params[0].second!=null){
            connection.setRequestProperty("Authorization","Bearer "+params[0].second);
            }
            String query="";
            JSONObject object=new JSONObject();
            if (params[0].second!=null&&params[0].second.equals("Register")){
                for (int i = 1; i < params.length; i++) {
                    object.put(params[i].first, params[i].second);
                    query = object.toString();
                }
            }//не надо было собирать сервер, не разобравшись в технологии
            else {
                StringBuilder stringBuilder=new StringBuilder();
                for (int i=1;i<params.length;i++){
                    if (i!=1){
                        stringBuilder.append("&");
                    }
                    if(params[i].first!="") {
                        //query.append(URLEncoder.encode(params[i].first, "UTF-8"));
                        stringBuilder.append(params[i].first);
                        stringBuilder.append("=");
                    }
                    stringBuilder.append(params[i].second);
                }
                query=stringBuilder.toString();
            }
            OutputStream stream=connection.getOutputStream();
            OutputStreamWriter writer=new OutputStreamWriter(stream);
            writer.write(query);
            writer.close();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            answer.append(query.toString());
        }catch (Exception e){
            answer.append(e.getClass().getName());
        }finally {
            return answer.toString();
        }
    }
    @Override
    protected void onPostExecute(String s) {
        sender.onBackgroundTaskFinish(s);
        super.onPostExecute(s);
    }
}
