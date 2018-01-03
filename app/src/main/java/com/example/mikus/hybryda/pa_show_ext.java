package com.example.mikus.hybryda;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class pa_show_ext extends AppCompatActivity {
    static int index=0;
    String SSID;
    String SSID_2;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pa_show_ext_activity);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SSID = extras.getString("SSID");
            SSID_2 = extras.getString("SSID_2");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Wywołanie funkcji do zapisywania punktów external
        wifi_external_loader(200);
    }

    public void wifi_external_loader(final int delayMseconds) {
        final Points punkt = new Points(this, SSID, SSID_2);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                punkt.Save_Point(1,-1);
                TextView count = (TextView)findViewById(R.id.count);
                count.setText(Integer.toString(Points.PointsList.getByIndexPointCollection_External().CountListRSSI()));
                DistanceAlgorithm da = new DistanceAlgorithm();
                index = da.Distance();
                TextView point = (TextView)findViewById(R.id.point);
                point.setText(Integer.toString(index));
                ListView listView = (ListView)findViewById(R.id.listView);
                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);
                handler.postDelayed(this, delayMseconds);
            }
        },delayMseconds);
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Points.PointsList.getByIndexPointCollection_External().CountListRSSI();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.pa_list_item,null);
            TextView bssid = (TextView)view.findViewById(R.id.BSSID);
            TextView rssi = (TextView)view.findViewById(R.id.RSSI);

            bssid.setText(Points.PointsList.getByIndexPointCollection_External().getMAC(i));
            double rssi_d = Points.PointsList.getByIndexPointCollection_External().getRSSI(i);
            int rssi_i = (int)rssi_d;
            rssi.setText(Integer.toString(rssi_i));

            return view;
        }
    }

}
