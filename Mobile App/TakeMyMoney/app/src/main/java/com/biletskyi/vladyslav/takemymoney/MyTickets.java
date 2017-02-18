package com.biletskyi.vladyslav.takemymoney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class MyTickets extends AppCompatActivity implements NetworkActivity,View.OnClickListener {

    SharedPreferences preferences;
    ProgressDialog pd;
    String responce;
    HashMap<Integer,Integer> numForId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pd=new ProgressDialog(this);
        pd.setMessage("Идет загрузка данных");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        HttpGETQuery query=new HttpGETQuery(this);
        preferences = getSharedPreferences("Important",MODE_PRIVATE);
        if (responce==null){
            query.execute("http://takemymoneyapi.azurewebsites.net/api/values/GetTickets",preferences.getString("token",""));
        }else{
            TextView email=(TextView)findViewById(R.id.email_text_view);
            email.setText(responce);
            pd.hide();
        }

    }

    @Override
    public void onBackgroundTaskFinish(String s) {
        LinearLayout field=(LinearLayout)findViewById(R.id.ticketsField) ;
        try {
            JSONArray json = new JSONArray(s);
            numForId=new HashMap();
            responce=s;
            for (int i=0;i<json.length();i++){
                JSONObject jsonObject=json.getJSONObject(i);
                TextView num=new TextView(this);
                int iNum=jsonObject.getInt("TicketNumber");
                num.setText(jsonObject.getString("TicketNumber"));
                num.setTextSize(20);
                field.addView(num);
                int Id=jsonObject.getInt("Id");
                String qr=String.format("id:%s;num:%s;user:%s;perf:%s",jsonObject.getString("Id"),
                        jsonObject.getString("TicketNumber"),jsonObject.getString("UserId"),jsonObject.getString("PerformanceId"));
                ImageView image=new ImageView(this);
                image.setImageBitmap(QrEncoder.encodeAsBitmap(qr,BarcodeFormat.QR_CODE,800,800));
                field.addView(image);
                Button del=new Button(this);
                del.setText("Удалить билет");
                del.setId(jsonObject.getInt("Id"));
                del.setOnClickListener(listener);
                field.addView(del);
                numForId.put(Id,iNum);
            }
            pd.hide();
        }catch (Exception e){
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage("Ошибка при получении данных. овторите еще раз");
            alert.show();
            pd.hide();
        }
    }
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent rtn = new Intent(MyTickets.this, ReturnTicket.class);
            int Id=v.getId();
            rtn.putExtra("Id",v.getId());
            rtn.putExtra("Num",numForId.get(Id));
            startActivity(rtn);
        }
    };
    @Override
    public void onClick(View v) {

    }
}
