package com.biletskyi.vladyslav.takemymoney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Catalogue extends AppCompatActivity implements View.OnClickListener,NetworkActivity {
    LinearLayout field;
    String response=null;
    ProgressDialog pd;
    HashMap namesForId;
    @Override
    protected void onResume() {
        setContentView(R.layout.activity_catalogue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        field=(LinearLayout)findViewById(R.id.catalogueField);
        pd=new ProgressDialog(this);
        pd.setMessage("Идет загрузка данных");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        try  {
            if (response==null) {
                HttpGETQuery query=new HttpGETQuery(this);
                query.execute("http://takemymoneyapi.azurewebsites.net/api/values/GetPerformances");
            }else{
                drawObjectFromJSONAnswer(response);
                pd.hide();
            }
        } catch (Exception e) {
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage(e.getLocalizedMessage());
            alert.show();
            finish();
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent buy = new Intent(this, BuyTicket.class);
        buy.putExtra("Id",v.getId());
        buy.putExtra("PerfName",(String)namesForId.get(v.getId()));
        startActivity(buy);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("response",response);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        response=savedInstanceState.getString("response");
    }

    public void onBackgroundTaskFinish(String result){
        response=result;
        try {
            drawObjectFromJSONAnswer(response);
        }catch (Exception e){
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage(e.getLocalizedMessage());
            alert.show();
            finish();
        }
        finally {
            pd.hide();
        }
    }
    private void drawObjectFromJSONAnswer(String json) throws JSONException {
        JSONArray jsonArray=new JSONArray(json);
        namesForId=new HashMap();
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            TextView name=new TextView(this);//название мероприятия
            String sName=jsonObject.getString("Name");
            name.setText(sName);
            name.setTextSize(20);
            name.setTextColor(Color.BLACK);
            field.addView(name);
            TextView date=new TextView(this);//дата проведения
            String d=jsonObject.getString("BeginingDateTime");
            date.setText(String.format("Дата и время проведения: %s, %s:%s",d.split("T")[0],
                    d.split("T")[1].split(":")[0],d.split("T")[1].split(":")[1]));
            field.addView(date);
            TextView place=new TextView(this);//место проведения
            place.setText(String.format("Место проведения: %s",jsonObject.getString("Place")));
            field.addView(place);
            ImageView image=new ImageView(this);//изображение
            if (jsonObject.getString("Image")=="null"){
                image.setImageResource(R.drawable.no_photo);
            }
            else{
                JSONObject jsonImage=jsonObject.getJSONObject("Image");
                byte[] im= Base64.decode(jsonImage.getString("Image"),Base64.DEFAULT);
                Bitmap bmp= BitmapFactory.decodeByteArray(im,0,im.length);
                image.setImageBitmap(bmp);
            }
            field.addView(image);
            TextView tickets=new TextView(this);//количество билетов
            int ticketCount=Integer.parseInt(jsonObject.getString("TicketCount"))-
                    Integer.parseInt(jsonObject.getString("FacticalCount"));
            tickets.setText(String.format("Билетов осталось: %d",ticketCount));
            field.addView(tickets);
            Button btn=new Button(this);//кнопка перехода
            if (ticketCount>0) {
                btn.setText("Купить билет");
            }else{
                btn.setText("Билетов не осталось");
                btn.setEnabled(false);
            }
            int id=Integer.parseInt(jsonObject.getString("Id"));
            btn.setId(id);
            namesForId.put(id,sName);
            btn.setOnClickListener(this);
            field.addView(btn);
        }


    }


}
