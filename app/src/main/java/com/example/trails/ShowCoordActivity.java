package com.example.trails;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.wikitude.tools.location.LocationService;

public class ShowCoordActivity extends AppCompatActivity /*implements LocationListener*/{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        double x;
        double y;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_coord);
        final TextView xText = findViewById(R.id.xText);
        final TextView yText = findViewById(R.id.yText);
        String coords;

        //make location manager
        final LocationManager locationManager = (LocationManager)
                getSystemService(this.LOCATION_SERVICE); //might wanna do this

        //check if gps
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

          ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, LocationService.CONTEXT_INCLUDE_CODE
                );
        }

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                xText.setText("X: " + Double.toString(location.getLatitude()));
                yText.setText("Y: " + Double.toString(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

}