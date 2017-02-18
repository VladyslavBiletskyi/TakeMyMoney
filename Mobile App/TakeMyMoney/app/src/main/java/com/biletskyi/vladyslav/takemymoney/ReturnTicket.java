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

public class ReturnTicket extends AppCompatActivity implements NetworkActivity,View.OnClickListener {

    SharedPreferences preferences;
    ProgressDialog pd;
    int Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_ticket);
        TextView pNum=(TextView)findViewById(R.id.ticketNum);
        Intent intent=getIntent();
        Id=intent.getIntExtra("Id",0);
        int num=intent.getIntExtra("Num",0);
        pNum.setText(Integer.toString(num));
        Button accept=(Button)findViewById(R.id.btnAcceptRtn);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToReturn(Id);
            }
        });
        Button cancel=(Button)findViewById(R.id.btnCancelRtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void tryToReturn(int id) {
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
        query.execute(new Pair<String, String>("http://takemymoneyapi.azurewebsites.net/api/values/ReturnTicket", preferences.getString("token", "")),
                new Pair<String, String>("", Integer.toString(id)));

    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackgroundTaskFinish(String ans) {
        if(ans.equals(Integer.toString(Id))) {
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage("Билет успешно сдан");
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
            alert.setMessage("Ошибка при попытке возврата. повторите еще раз");
            alert.show();
            pd.hide();
        }
    }
}
