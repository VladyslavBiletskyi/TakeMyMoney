package com.biletskyi.vladyslav.takemymoney;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;

import org.json.JSONObject;

public class Profile extends AppCompatActivity implements NetworkActivity {

    SharedPreferences preferences;
    ProgressDialog pd;
    String responce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_profile);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        pd=new ProgressDialog(this);
        pd.setMessage("Идет загрузка данных");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        HttpGETQuery query=new HttpGETQuery(this);
        preferences = getSharedPreferences("Important",MODE_PRIVATE);
        if (responce==null){
        query.execute("http://takemymoneyapi.azurewebsites.net/api/Account/GetUser",preferences.getString("token",""));
        }else{
            TextView email=(TextView)findViewById(R.id.email_text_view);
            email.setText(responce);
            pd.hide();
        }
        super.onResume();
    }

    @Override
    public void onBackgroundTaskFinish(String s) {
        TextView email=(TextView)findViewById(R.id.email_text_view);
        try {
            JSONObject jsonObject = new JSONObject(s);
            email.setText(jsonObject.getString("Email"));
            responce=jsonObject.getString("Email");
            pd.hide();
        }catch (Exception e){
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage("Ошибка при получении данных. овторите еще раз");
            alert.show();
            pd.hide();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("response",responce);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        responce=savedInstanceState.getString("response");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
