package com.example.trails;

import java.io.IOException;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.opengl.GLES20;
import android.os.Bundle;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

/**
 * Abstract activity which handles live-cycle events.
 * Feel free to extend from this activity when setting up your own AR-Activity
 *
 */
public abstract class ARViewActivity extends Activity{

    /**
     * holds the Wikitude SDK AR-View, this is where camera, markers, compass, 3D models etc. are rendered
     */
    protected ArchitectView					architectView;

    /**
     * sensor accuracy listener in case you want to display calibration hints
     */
    protected SensorAccuracyChangeListener	sensorAccuracyListener;

    /**
     * last known location of the user, used internally for content-loading after user location was fetched
     */
    protected Location 						lastKnownLocaton;

    /**
     * sample location strategy, you may implement a more sophisticated approach too
     */
    /**
     * location listener receives location updates and must forward them to the architectView
     */
    protected LocationListener 				locationListener;

    /**
     * urlListener handling "document.location= 'architectsdk://...' " calls in JavaScript"
     */

    /** Called when the activity is first created. */
    @Override
    public void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        /* pressing volume up/down should cause music volume changes */
        this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

        /* set samples content view */
        this.setContentView( this.getContentViewId() );

        this.setTitle( this.getActivityTitle() );

        /* set AR-view for life-cycle notifications etc. */
        this.architectView = (ArchitectView)this.findViewById( this.getArchitectViewId()  );

        /* pass SDK key if you have one, this one is only valid for this package identifier and must not be used somewhere else */

        /* first mandatory life-cycle notification */

        // set accuracy listener if implemented, you may e.g. show calibration prompt for compass using this listener
        this.sensorAccuracyListener = this.getSensorAccuracyListener();

        // set urlListener, any calls made in JS like "document.location = 'architectsdk://foo?bar=123'" is forwarded to this listener, use this to interact between JS and native Android activity/fragment

        // register valid urlListener in architectView, ensure this is set before content is loaded to not miss any event

        // listener passed over to locationProvider, any location update is handled here
        this.locationListener = new LocationListener() {

            @Override
            public void onStatusChanged( String provider, int status, Bundle extras ) {
            }

            @Override
            public void onProviderEnabled( String provider ) {
            }

            @Override
            public void onProviderDisabled( String provider ) {
            }

            @Override
            public void onLocationChanged( final Location location ) {
                // forward location updates fired by LocationProvider to architectView, you can set lat/lon from any location-strategy
                if (location!=null) {
                    // sore last location as member, in case it is needed somewhere (in e.g. your adjusted project)
                    ARViewActivity.this.lastKnownLocaton = location;
                    if ( ARViewActivity.this.architectView != null ) {
                        // check if location has altitude at certain accuracy level & call right architect method (the one with altitude information)
                        if ( location.hasAltitude() && location.hasAccuracy() && location.getAccuracy()<7) {
                            ARViewActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy() );
                        } else {
                            ARViewActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
                        }
                    }
                }
            }
        };

        // locationProvider used to fetch user position

    }

    @Override
    protected void onPostCreate( final Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );

        if ( this.architectView != null ) {

            // call mandatory live-cycle method of architectView
            this.architectView.onPostCreate();

            try {
                // load content via url in architectView, ensure '<script src="architect://architect.js"></script>' is part of this HTML file, have a look at wikitude.com's developer section for API references
                this.architectView.load( this.getARchitectWorldPath() );



            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // call mandatory live-cycle method of architectView
        if ( this.architectView != null ) {
            this.architectView.onResume();

            // register accuracy listener in architectView, if set
            if (this.sensorAccuracyListener!=null) {
                this.architectView.registerSensorAccuracyChangeListener( this.sensorAccuracyListener );
            }
        }

        // tell locationProvider to resume, usually location is then (again) fetched, so the GPS indicator appears in status bar

    }

    @Override
    protected void onPause() {
        super.onPause();

        // call mandatory live-cycle method of architectView
        if (this.architectView != null) {
            this.architectView.onPause();

            // unregister accuracy listener in architectView, if set
            if (this.sensorAccuracyListener != null) {
                this.architectView.unregisterSensorAccuracyChangeListener(this.sensorAccuracyListener);
            }
        }

        // tell locationProvider to pause, usually location is then no longer fetched, so the GPS indicator disappears in status bar
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // call mandatory live-cycle method of architectView
        if ( this.architectView != null ) {
            this.architectView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if ( this.architectView != null ) {
            this.architectView.onLowMemory();
        }
    }

    /**
     * title shown in activity
     * @return
     */
    public abstract String getActivityTitle();

    /**
     * path to the architect-file (AR-Experience HTML) to launch
     * @return
     */
    public abstract String getARchitectWorldPath();

    /**
     * url listener fired once e.g. 'document.location = "architectsdk://foo?bar=123"' is called in JS
     * @return
     */


    /**
     * @return layout id of your layout.xml that holds an ARchitect View, e.g. R.layout.camview
     */
    public abstract int getContentViewId();

    /**
     * @return Wikitude SDK license key, checkout www.wikitude.com for details
     */
    public abstract String getWikitudeSDKLicenseKey();

    /**
     * @return layout-id of architectView, e.g. R.id.architectView
     */
    public abstract int getArchitectViewId();

    /**
     *
     * @return Implementation of a Location
     */


    /**
     * @return Implementation of Sensor-Accuracy-Listener. That way you can e.g. show prompt to calibrate compass
     */
    public abstract ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener();

    /**
     * helper to check if video-drawables are supported by this device. recommended to check before launching ARchitect Worlds with videodrawables
     * @return true if AR.VideoDrawables are supported, false if fallback rendering would apply (= show video fullscreen)
     */
    public static final boolean isVideoDrawablesSupported() {
        String extensions = GLES20.glGetString( GLES20.GL_EXTENSIONS );
        return extensions != null && extensions.contains( "GL_OES_EGL_image_external" ) && android.os.Build.VERSION.SDK_INT >= 14 ;
    }

}