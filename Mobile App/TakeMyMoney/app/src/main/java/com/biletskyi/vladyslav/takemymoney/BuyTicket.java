package com.biletskyi.vladyslav.takemymoney;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

public class BuyTicket extends AppCompatActivity implements NetworkActivity, View.OnClickListener{

    SharedPreferences preferences;
    ProgressDialog pd;
    int Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);
        Intent intent=getIntent();
        TextView pName=(TextView)findViewById(R.id.perfName);
        Id=intent.getIntExtra("Id",0);
        pName.setText(intent.getStringExtra("PerfName"));
        Button accept=(Button)findViewById(R.id.btnAcceptBuy);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToBuy(Id);
            }
        });
        Button cancel=(Button)findViewById(R.id.btnCancelBuy);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void tryToBuy(int id) {
        if (id == 0) {
            return;
        }
        pd = new ProgressDialog(this);
        pd.setMessage("Идет загрузка данных");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        preferences = getSharedPreferences("Important", MODE_PRIVATE);
        HttpPostQuery query = new HttpPostQuery(this);
        query.execute(new Pair<String, String>("http://takemymoneyapi.azurewebsites.net/api/values/BuyTicket", preferences.getString("token", "")),
                new Pair<String, String>("", Integer.toString(id)));
    }
    public void onBackgroundTaskFinish(String ans) {
        if(ans.equals("1")) {
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage("Билет успешно куплен");
            alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.show();
            pd.hide();
            finish();
        }
        else{
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage("Ошибка при попытке покупки. повторите еще раз");
            alert.show();
            pd.hide();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
