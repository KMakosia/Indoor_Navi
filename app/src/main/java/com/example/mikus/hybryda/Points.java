package com.example.mikus.hybryda;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Points extends ContextWrapper {
    private String SSID,SSID_2;

    public Points(Context context) {
        super(context);
        SSID = "null";
        SSID_2 = "null";
    }

    public Points(Context context, String SSID, String SSID_2) {
        super(context);
        this.SSID=SSID.toLowerCase();
        this.SSID_2=SSID_2.toLowerCase();
    }

    static class PointsList {
        private static List<Point> Point_collection_Local = new ArrayList<>();
        private static List<Point> Point_collection_External = new ArrayList<>();

        static void add_point_L(Point point) {
                Point_collection_Local.add(point);
        }

        static void add_point_E(Point point) {
            if(Point_collection_External.size() == 0) {
                Point_collection_External.add(0, point);
            } else {
                Point_collection_External.set(0, point);
            }
        }

        public static Point getByIndexPointCollection_Local(int index) {
            return Point_collection_Local.get(index);
        }

        public static Point getByIndexPointCollection_External() {
            return Point_collection_External.get(0);
        }

        public static int count_Points_local() {
            return Point_collection_Local.size();
        }

        public static void removeByIndex(int index) {
            Point_collection_Local.remove(index);
        }

        public static void setPoint_local(int index, Point point) {
            if(index >= 0 && index <= Point_collection_Local.size()) {
                Point_collection_Local.set(index, point);
            }
        }
    }

    public class Point {
        private List<String> BSSID = new ArrayList<>();
        private List<Double> RSSI = new ArrayList<>();
        private double x;
        private double y;

        private Point(int type, int modify) {
            if (type == 0) {
                if (modify == -1) {
                    PointsList.add_point_L(this);
                } else {
                    PointsList.setPoint_local(modify, this);
                }
            } else if (type == 1) {
                PointsList.add_point_E(this);
            }
        }

        public void setBSSID(String BSSID) {
            this.BSSID.add(BSSID);
        }

        public void setRSSI(String RSSI) {
            double rssi = Double.parseDouble(RSSI);
            this.RSSI.add(rssi);
        }

        public void replaceRSSI(int index, String var) {
            double rssi = Double.parseDouble(var);
            this.RSSI.set(index, rssi);
        }

        public String getMAC(int index) {
            return BSSID.get(index);
        }

        public String getRSSI_s(int index) {
            return RSSI.get(index).toString();

        }

        public double getRSSI(int index) {
            return RSSI.get(index);
        }

        public int CountListRSSI() {
            return RSSI.size();
        }

        public List<String> returnBSSID(){
            return BSSID;
        }

        public void setX(String x){
            double X = Double.parseDouble(x);
            this.x = X;
        }

        public void setY(String y){
            double Y = Double.parseDouble(y);
            this.y = Y;
        }

        public double getX(){
            return x;
        }

        public double getY() {
            return y;
        }

    }

    public void SaveFile(Context context) {
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/Navi_fp/");

        if(!path.exists()) {
            path.mkdirs();
        }

        final File file = new File(path,"Point_Local_Lists.txt");

            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                for(int i = 0; i< PointsList.count_Points_local(); i++) {
                    Points.Point point = PointsList.getByIndexPointCollection_Local(i);
                    for(int j=0; j<point.CountListRSSI();j++) {
                        outputStreamWriter.write(point.getMAC(j) + ",");
                        outputStreamWriter.write(point.getRSSI_s(j) +",");
                    }
                    if(point.CountListRSSI() > 0) {
                        outputStreamWriter.write("\n");
                    }
                }
                outputStreamWriter.close();
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void ReadFile() {
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/Navi_fp/");
        File file = new File(path,"Point_Local_Lists.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String data;
                while ((data = br.readLine()) != null) {
                    String[] values = data.split(",");
                    Point point = new Point(0,-1);

                    for(int i=0; i<values.length; i++) {
                        point.setBSSID(values[i]);
                        point.setRSSI(values[i+1]);
                        i++;
                    }
                }
                br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadFileXY(Context context) {
        int i=0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("p-1")))) {
            String data;
            int k=Points.PointsList.count_Points_local();
            while ((data = br.readLine()) != null && i<k) {
                String[] values = data.split(",");
                Point points_local = PointsList.getByIndexPointCollection_Local(Integer.parseInt(values[2])-1);
                points_local.setX(values[0]);
                points_local.setY(values[1]);
                i++;
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Save_Point(int type,int modify) {
        WifiManager wifiManager;
        List<ScanResult> wifiList;

        wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();

        if(type == 0) {
            Point point_local = new Point(0, modify);
            for (ScanResult results : wifiList) {
                if(results.SSID.toLowerCase().startsWith(SSID) == true || results.SSID.toLowerCase().startsWith(SSID_2) == true || SSID.toLowerCase() == "null") {
                    if(results.BSSID != null) {
                        point_local.setBSSID(results.BSSID);
                        point_local.setRSSI(Integer.toString(results.level));
                    }
                }
            }
        } else if (type == 1) {
            Point point_external = new Point(1, -1);
            for (ScanResult results : wifiList) {
                if(results.BSSID != null) {
                    if(results.SSID.toLowerCase().startsWith(SSID) == true || results.SSID.toLowerCase().startsWith(SSID_2) == true || SSID.toLowerCase() == "null"){
                    point_external.setBSSID(results.BSSID);
                    point_external.setRSSI(Integer.toString(results.level));
                    }
                }
            }
        }
    }

    public static void Show_point_info() {
        for (int i = 0; i < PointsList.count_Points_local(); i++) {
            Point points_local = PointsList.getByIndexPointCollection_Local(i);
            Log.v("Local points: ", "Punkt: " + i);
            Log.v("Local point X : ", "" + points_local.getX());
            Log.v("Local point Y : ", "" + points_local.getY());

            for (int j = 0; j < points_local.CountListRSSI(); j++) {
                Log.v("Local points - BSSID", "" + points_local.getMAC(j));
                Log.v("Local points - RSSI", "" + points_local.getRSSI(j));
            }
        }

        if(PointsList.Point_collection_External.size() == 1) {
            Point point_external = PointsList.getByIndexPointCollection_External();
            Log.v("External Point:", "Punkt: 0");
            for (int j = 0; j < point_external.CountListRSSI(); j++) {
                Log.v("External Point - BSSID", "" + point_external.getMAC(j));
                Log.v("External Point - RSSI", "" + point_external.getRSSI(j));
            }
        }
    }

}

