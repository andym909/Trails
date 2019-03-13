package com.example.trails;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;
import com.wikitude.tools.location.LocationService;

import java.io.IOException;

public class SimpleAR extends AppCompatActivity {

    static final String TAG = SimpleAR.class.getSimpleName();

    private ArchitectView architectView; // = new ArchitectView(SimpleAR.this.getApplicationContext());

    boolean recording = false;
    Trail newTrail = new Trail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_ar);

        //this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration(); // Creates a config with its default values.
//        config.setLicenseKey(getString(R.string.wikitude_trial_license_key)); // Has to be set, to get a trial license key visit http://www.wikitude.com/developer/licenses.
//        config.setCameraPosition(sampleData.getCameraPosition());       // The default camera is the first camera available for the system.
//        config.setCameraResolution(sampleData.getCameraResolution());   // The default resolution is 640x480.
//        config.setCameraFocusMode(sampleData.getCameraFocusMode());     // The default focus mode is continuous focusing.
//        config.setCamera2Enabled(sampleData.isCamera2Enabled());        // The camera2 api is disabled by default (old camera api is used).
//        config.setFeatures(sampleData.getArFeatures());                 // This tells the ArchitectView which AR-features it is going to use, the default is all of them.
//
//        architectView = new ArchitectView(this);
//        architectView.onCreate(config); // create ArchitectView with configuration

        setContentView(architectView);

        if(ActivityCompat.checkSelfPermission(SimpleAR.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(SimpleAR.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.i(TAG,
                        "Displaying camera permission rationale to provide additional context.");
                Snackbar.make(architectView, "Camera permission is needed to show the camera preview.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(SimpleAR.this,
                                        new String[]{Manifest.permission.CAMERA,Manifest.permission.BLUETOOTH},
                                        0);
                            }
                        })
                        .show();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            loadArchitectView();
        }

        double x;
        double y;

        final TextView xText = findViewById(R.id.xText);
        final TextView yText = findViewById(R.id.yText);
        final TextView printTrail = findViewById(R.id.print_trail);
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
                if(recording)
                    newTrail.addNode(location.getLongitude(), location.getLatitude());
                else
                    printTrail.setText(newTrail.toString());
                xText.setText("X: " + Double.toString(location.getLatitude()));
                yText.setText("Y: " + Double.toString(location.getLongitude()));
                architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude());
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

    @Override
    public void onResume() {
        try{
            super.onResume();

            this.architectView.onResume();
        }catch(NullPointerException e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        try{
            super.onDestroy();
        }catch (NullPointerException e){
        }
    }

    @Override
    protected void onPause() {
        try{
            super.onPause();

            this.architectView.onResume();
            this.architectView.onPause();

        }catch (NullPointerException e){

        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();

        try {
            /*
             * Loads the AR-Experience, it may be a relative path from assets,
             * an absolute path (file://) or a server url.
             *
             * To get notified once the AR-Experience is fully loaded,
             * an ArchitectWorldLoadedListener can be registered.
             */
            architectView.load("app/src/main/assets/index.html");
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading arExperience ");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadArchitectView();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void loadArchitectView() {

        final ArchitectStartupConfiguration startupConfiguration = new ArchitectStartupConfiguration();
        startupConfiguration.setLicenseKey(getApplicationContext().getString(R.string.wikitude_trial_license_key));

        this.architectView.onCreate(startupConfiguration);

        this.architectView.onPostCreate();
        try {
            this.architectView.load("index.html");
        }
            catch (IOException e) {
        }

        //this.architectView.callJavascript("loadLogin()");
        //this.architectView.callJavascript("loadWelcome()");
        this.architectView.callJavascript("this.loadPoisFromJsonData");
    }

    public void startRecording(View view) {
        recording = !recording;
    }

}
