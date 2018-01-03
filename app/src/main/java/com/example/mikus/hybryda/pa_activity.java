package com.example.mikus.hybryda;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class pa_activity extends AppCompatActivity {
    String SSID="null";
    String SSID_2="null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pa_activity);
        //      punkt.Show_point_info();

    }

    protected void onStart() {
        super.onStart();
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void Next_Activity(View view) {
        Intent i = new Intent(this,pa_show_ext.class);
        i.putExtra("SSID",SSID);
        i.putExtra("SSID_2",SSID_2);
        startActivity(i);
        // Points.Show_point_info();
    }

    public void next_activity_adm_(View view) {
        Intent i = new Intent(this, pa_map_adm.class);
        i.putExtra("SSID",SSID);
        i.putExtra("SSID_2",SSID_2);
        startActivity(i);
    }

    public void register(View view) {
        Points punkt = new Points(this,SSID,SSID_2);
        punkt.Save_Point(0,-1);
    //    Points.Show_point_info();

        Toast toast = Toast.makeText(this,"Zarejestrowano punkt: " + (Points.PointsList.count_Points_local()-1),Toast.LENGTH_SHORT);
        toast.show();
        punkt.SaveFile(this);
    }

    public void modify(View view) {
        int number;
        EditText et = (EditText)findViewById(R.id.point_number);
        number = Integer.parseInt(et.getText().toString());

        if(number >= 0) {
            et.setText("");
            Points punkt = new Points(this, SSID, SSID_2);
            punkt.Save_Point(0, number);
            //       Points.Show_point_info();
            Toast toast = Toast.makeText(this, "Zmieniono punkt: " + number, Toast.LENGTH_SHORT);
            toast.show();
            punkt.SaveFile(this);
        }
    }

    public void filtr_btk(View view) {
        EditText ssid = (EditText)findViewById(R.id.editText);
        EditText ssid_2 = (EditText)findViewById(R.id.editText2);

        SSID = ssid.getText().toString();
        SSID_2 = ssid_2.getText().toString();

        Toast toast = Toast.makeText(this,"Ustawiono filtr!",Toast.LENGTH_SHORT);
        toast.show();
    }

}
