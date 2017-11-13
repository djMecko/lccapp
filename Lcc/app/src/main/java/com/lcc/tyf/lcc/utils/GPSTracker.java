package com.lcc.tyf.lcc.utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by max on 6/17/17.
 */

public class GPSTracker extends Service implements android.location.LocationListener {

    private final Context mContext;
    // private final Context mContextActivity;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    //   private static final int MY_LOCATION_REQUEST_CODE = 1;


    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 minute

    private final static int MY_RQEUEST_PERMISSIONS = 1;

    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        //  this.mContextActivity = mContext.getApplicationContext();
        getLocation();

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled) {
            /*try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                ChazkiLog.v("GPS Cargado");
            }catch (SecurityException ex){
                Log.i("ERROR","Access denied " + ex.getMessage());
            }
*/
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }else{
                if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){


                    //   ActivityCompat.requestPermissions((Activity)mContextActivity,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},MY_RQEUEST_PERMISSIONS);
//                    Log.i("ERROR","Permission denied ");
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
            }


        } else if (isNetworkEnabled) {

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }else{
                if(ContextCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                    // ActivityCompat.requestPermissions((Activity)mContext,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},MY_RQEUEST_PERMISSIONS);

                    //onRequestPermissionsResult(,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},MY_RQEUEST_PERMISSIONS);
                    // registerComponentCallbacks();
                    // Log.i("ERROR","Permission denied ");

                }else{
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                }

            }

            /*
            try{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }catch (SecurityException ex){
                Log.i("ERROR","Access denied " + ex.getMessage());
            }

*/
        }

    }

    public boolean isGpsEnabled() {
        return isGPSEnabled;
    }

    public Location getLocation() {
        try {

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;

                boolean trackuse = false;

                if (isGPSEnabled) {


                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    } else {
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            //  ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_RQEUEST_PERMISSIONS);

                            Log.i("ERROR", "Permission denied ");


                        } else {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        }
                    }

                        /*try{

                        }catch (SecurityException ex){
                            Log.i("ERROR","Permission denied " + ex.getMessage());
                        }
*/

                    Log.v("DATAGPS","is locationManager Enabled " + (locationManager != null));
                    if (locationManager != null) {

                     /*  try{
                           location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                       }catch (SecurityException ex){
                           Log.i("ERROR","Permission denied " + ex.getMessage());
                       }
*/
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        } else {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                                // ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_RQEUEST_PERMISSIONS);

                                Log.i("ERROR", "Permission denied ");
                            } else {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                Log.v("DATAGPS","is location Enabled " + (location != null));

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    trackuse = true;
                                    Log.v("DATAGPS","Usando GPS");
                                }


                            }
                        }
                    } else {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                    if (isNetworkEnabled && !trackuse) {

                        try {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        }catch (SecurityException e){
                            Log.i("ERROR","Permission denied " + e.getMessage());
                        }
                        //  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


                        if (locationManager != null) {

                            try {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }catch (SecurityException e){
                                Log.i("ERROR","Permission denied " + e.getMessage());
                            }


                            //location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.v("DATAGPS","Usando WIFI");
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.v("response: ", e.getMessage());
        }

        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {

            try {
                locationManager.removeUpdates(GPSTracker.this);
            }catch (SecurityException e){
                Log.i("ERROR","Permission denied " + e.getMessage());
            }

        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mContext.startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }


}