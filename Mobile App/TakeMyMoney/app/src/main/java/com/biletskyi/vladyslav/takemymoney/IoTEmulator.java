package com.biletskyi.vladyslav.takemymoney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class IoTEmulator extends AppCompatActivity implements NetworkActivity{

    SharedPreferences preferences;
    ProgressDialog pd;
    String checkString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("Important",MODE_PRIVATE);
        setContentView(R.layout.activity_iot_emulator);
        LinearLayout field=(LinearLayout)findViewById(R.id.activity_iot_emulator);
        Button button=(Button)findViewById(R.id.emulator_btnCheck);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent,14);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 14) {
            pd = new ProgressDialog(this);
            pd.setMessage("Идет загрузка данных");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
            HttpPostQuery query=new HttpPostQuery(this);
            EditText num=(EditText)findViewById(R.id.emulator_PerfId);
            String queryString=data.getStringExtra("SCAN_RESULT")+";current:"+num.getText();
            checkString=queryString;
            query.execute(new Pair<String, String>("http://takemymoneyapi.azurewebsites.net/api/values/CheckTicket", preferences.getString("token", "")),
                    new Pair<String, String>("", queryString));
        }
    }

    @Override
    public void onBackgroundTaskFinish(String ans) {
        String msg="";
        if (ans.equals(checkString)){
            msg="Билет правильный, вы можете войти";
        }
        else{
            msg="Билет не верен или не существует";
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(msg);
        pd.hide();
        alert.show();
    }
}
