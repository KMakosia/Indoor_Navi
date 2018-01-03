package com.example.mikus.hybryda;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.functions.Function;
import com.mapbox.mapboxsdk.style.functions.stops.Stop;
import com.mapbox.mapboxsdk.style.functions.stops.Stops;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.io.InputStream;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class navi_map extends AppCompatActivity {
    static int index = -1;
    static int second_index = -1337;
    static double X=0,Y=0;
    static boolean create_marker = false;
    private MapView mapView;
    private Marker current_pos;
    private GeoJsonSource indoorBuildingSource;
    private MapboxMap map;
    private Marker nowy; // do wyswietlania drugiego markera
    double x;
    double y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_map_activity);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZGFyZXExMTIiLCJhIjoiY2o5dml2b3ltN2w4MDJxcXQydTV3ZWFjOSJ9.X3FKr69RwqmzPHPQ6J460Q"); //access token z konta MapBox
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                //Funkcja Tworzy markera z obecną lokalizacją
                create_marker = true;
                current_pos = mapboxMap.addMarker(new MarkerOptions().position(new LatLng(X, Y)).title("Lokalizacja").snippet("You'are here"));
                create_marker = false;
                
                map = mapboxMap;
                indoorBuildingSource = new GeoJsonSource("indoor-building", loadJsonFromAsset("parter.geojson"));
                mapboxMap.addSource(indoorBuildingSource);
                loadBuildingLayer();
                addMapClickListener(); // do wyswietlania drugiego markera

            }});
    }

    private void addMapClickListener() { // do wyswietlania drugiego markera
        map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng punkt) {
                x=punkt.getLatitude();
                y=punkt.getLongitude();
                if(nowy == null)
                    nowy = map.addMarker(new MarkerOptions().position(new LatLng(x, y)));
                else
                    nowy.setPosition(new LatLng(x,y));
            }
        });

        Button button_Parter = (Button) findViewById(R.id.parter_button);
        button_Parter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indoorBuildingSource.setGeoJson(loadJsonFromAsset("parter.geojson"));
            }
        });

        Button button_Piwnica = (Button) findViewById(R.id.piwnica_button);
        button_Piwnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indoorBuildingSource.setGeoJson(loadJsonFromAsset("piwnica.geojson"));
            }
        });

        Button button_Pietro2 = (Button) findViewById(R.id.pietro2_button);
        button_Pietro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indoorBuildingSource.setGeoJson(loadJsonFromAsset("pietro2.geojson"));
            }
        });

    };

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        wifi_external_loader(200,"edu","konf");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void wifi_external_loader(final int delayMseconds, String SSID, String SSID_2) {
        final Points punkt = new Points(this, SSID, SSID_2);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                punkt.Save_Point(1,-1);
                DistanceAlgorithm da = new DistanceAlgorithm();
                index = da.Distance();
                Points.Point points_local = Points.PointsList.getByIndexPointCollection_Local(index);
                X = points_local.getX();
                Y = points_local.getY();
                //Log.v("XiY", "" + X + ", " + Y + " Punkt: " + index);
                AsyncMap();
                handler.postDelayed(this, delayMseconds);
            }
        },delayMseconds);
    }

    public void AsyncMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                if(second_index != index) {
                    current_pos.setPosition(new LatLng(X, Y));
                }

                second_index = index;
                //DrowPath();

                //if(index >0 && index <=60)
                //indoorBuildingSource.setGeoJson(loadJsonFromAsset("piwnica"));
                //if(index >60 && index <=120)
                //indoorBuildingSource.setGeoJson(loadJsonFromAsset("parter"));

            }
        });
    }

   /* public void DrowPath(){
        List<LatLng> PointsL = new ArrayList<>();

        for(int i=0;i< Points.PointsList.count_Points_local();i++){
            LatLng ll= new LatLng(Points.PointsList.getByIndexPointCollection_Local(i).getX(),Points.PointsList.getByIndexPointCollection_Local(i).getY());
            PointsL.add(ll);
        }

        LatLng[] pointsArray = PointsL.toArray(new LatLng[PointsL.size()]);

        LatLng lastPoint = pointsArray[0];

        for(LatLng point : pointsArray) {
            map.addPolyline(new PolylineOptions()
                    .add(lastPoint)
                    .add(point)
                    .color(Color.parseColor("#F13C6E"))
                    .width(4));

            lastPoint = point;
        }
    }*/

    private void loadBuildingLayer() {


        //     FillLayer indoorBuildingLayer = new FillLayer("indoor-building-fill", "indoor-building").withProperties(
        //             fillColor(Color.parseColor("#eeeeee")),
        //             fillOpacity(Function.zoom(Stops.exponential(
        //                     Stop.stop(17f, fillOpacity(1f)),
        //                     Stop.stop(16.5f, fillOpacity(0.5f)),
        //                     Stop.stop(16f, fillOpacity(0f))
        //             )))
        //     );

        //     map.addLayer(indoorBuildingLayer);

        LineLayer indoorBuildingLineLayer = new LineLayer("indoor-building-line", "indoor-building").withProperties(
                lineColor(Color.parseColor("#000000")),
                lineWidth(1f),
                lineOpacity(Function.zoom(Stops.exponential(
                        Stop.stop(17f, lineOpacity(1f)),
                        Stop.stop(16.5f, lineOpacity(0.5f)),
                        Stop.stop(16f, lineOpacity(0f))
                )))

        );
        map.addLayer(indoorBuildingLineLayer);

        SymbolLayer symbolBuildingLayer = new SymbolLayer("indoor-building-symbol","indoor-building");
        symbolBuildingLayer.withProperties(iconOpacity(5f),
                textField("{nazwa}"),
                textSize(Function.zoom(Stops.exponential
                        (Stop.stop(20f,textSize(12f)),
                                Stop.stop(19f,textSize(10f)),
                                Stop.stop(18f,textSize(8f)),
                                Stop.stop(17f,textSize(6f)),
                                Stop.stop(16f,textSize(0f))
                        )))
        );
        map.addLayer(symbolBuildingLayer);

    }

    private String loadJsonFromAsset(String filename) {
        // Using this method to load in GeoJSON files from the assets folder.

        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}


