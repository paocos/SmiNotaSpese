package com.paocos.sminotaspese.connector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.paocos.sminotaspese.MainActivity;
import com.paocos.sminotaspese.R;
import com.paocos.sminotaspese.model.entities.GpsPoint;
import com.paocos.sminotaspese.model.entities.MyLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSConnectorFrg extends Service implements LocationListener {

    private static final String TAG = GPSConnectorFrg.class.getSimpleName();
    public static final String CHANNEL_ID = "GPSConnectorFrgChannel";

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 1 * 1;
    private static final float LOCATION_DISTANCE = 0;
    private static final Double EARTH_RADIUS_POLE = 6356.7523;
    private static final Double EARTH_RADIUS_EQUATOR = 6378.137;
    private static final int COUNTER_FOR_GEOCODER = 7;
    private static final int COUNTER_FOR_SPEED = 4;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int TIMEOUT_GPS_UPDATED = 1000 * 2; // 3 secondi
    private static final float MIN_ACCURACY = 50;
    private final int MY_PERMISSION_LOCATION = 1;
    private MyLocation oldLocation;
    private int counter_for_geocoder;
    private Geocoder geocoder;
    private Address actAddress;
    private GpsPoint gpsPoint;

    private double sumLatitude;
    private double sumLongitude;
    private double sumAltitude;

    private double sumDistance;
    private double sumTime;
    private int nrForSpeed;
    private double velocity;

    private Long firstTime;
    private Long lastTime;
    private int nrOfGpsRead;
    private boolean isGettingMyLocation;
    private boolean isUpdatingLocation;

    private Boolean isNetworkConnected;

    private final IBinder mBinder = new LocalBinder();

    public GPSConnectorFrg() {
    }

//    public GPSConnectorFrg(Activity activity , Context context) {
    private void initializeGPS() {

        Log.e(TAG, "new Connector");
        try {
//            this.context = context;
//            this.activity = activity;
            initializeLocationManager();

            sumLatitude=0;
            sumLongitude=0;
            sumAltitude=0;
            firstTime = null;
            lastTime=new Long(0);
            nrOfGpsRead=0;

            sumDistance=0;
            sumTime=0;
            nrForSpeed=0;
            velocity=0;

        } catch (SecurityException e) {

        }
    }
 //   }

    /**
     * new START
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeGPS();
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GPS Location Provider")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_gps)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public GPSConnectorFrg getService() {
            return GPSConnectorFrg.this;
        }
    }
    /**
     * new END
     */



/*
    public void start() {
        Log.e(TAG, "start Connector");
        try {
            startListener();
            //setFirstRead();
        } catch (SecurityException e) {

        }
    }

    public void  stop() {
        stopListener();
    }

    private void startListener() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION} , MY_PERMISSION_LOCATION);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, this);
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, this);
    }

    private void stopListener() {
        mLocationManager.removeUpdates(this);
    }
*/

    private Address getGeocoderAddress(Location location) {
        if (!isNetworkConnected) {
            return null;
        }
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                return addressList.get(0);
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        geocoder = new Geocoder(getApplicationContext() , Locale.getDefault());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (!isGettingMyLocation) {
                isUpdatingLocation = true;
                sumAltitude += location.getAltitude();
                sumLatitude += location.getLatitude();
                sumLongitude += location.getLongitude();
                nrOfGpsRead++;
                if (firstTime == null) {
                    firstTime = location.getTime();
                }
                lastTime = location.getTime();
                isUpdatingLocation = false;
            } // (!isGettingMyLocation) {
        } // (location != null) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e(TAG, "onStatusChanged: " + provider + "/" + status + "/" + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e(TAG, "onProviderDisabled: " + provider);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: " + intent);
        return mBinder;
    }

    public boolean isLocationEnabled() {
        boolean gps_enabled = false;
//        boolean network_enabled = false;
        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

/*
        try {
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
*/

//        if (gps_enabled || network_enabled) {
        if (gps_enabled) {
            return true;
        } else {
            return false;
        }
    }

    public MyLocation getMyLocation(Boolean isNetworkConnected) {

        try {

            if (isUpdatingLocation) {
                return null;
            }

            isGettingMyLocation = true;

            this.isNetworkConnected = isNetworkConnected;

            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location == null) {
                location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            if (location != null) {
                MyLocation myLocation = new MyLocation(location);

                if (nrOfGpsRead != 0) {

                    myLocation.setMyAltitude(calcMedia(sumAltitude, nrOfGpsRead));
                    myLocation.setMyLatitude(calcMedia(sumLatitude, nrOfGpsRead));
                    myLocation.setMyLongitude(calcMedia(sumLongitude, nrOfGpsRead));
                    myLocation.setMyTime(lastTime - firstTime);
                    sumLatitude = 0;
                    sumLongitude = 0;
                    sumAltitude = 0;
                    firstTime = lastTime;
                    lastTime = new Long(0);
                    nrOfGpsRead = 0;

                    if (oldLocation != null) {
                        setDistance(myLocation, oldLocation);
                        setSpeed(myLocation, oldLocation);
                    } else {
                        myLocation.setDistance(0);
                        myLocation.setMySpeed(0);
                    }

                    counter_for_geocoder++;
                    if (isNetworkConnected) {
                        myLocation.setAddress(getGeocoderAddress(myLocation));
                    }
                    if (oldLocation != null) {
                        gpsPoint = setGpsPoint(oldLocation, myLocation);
                    }

                    if (oldLocation != null && (myLocation.getDistance() == null || myLocation.getDistance().isNaN())) {
                        myLocation = new MyLocation(oldLocation);
                    } else {
                        oldLocation = new MyLocation(myLocation);
                    }
                }

                isGettingMyLocation = false;
                return myLocation;

            }

        } catch(SecurityException e){

        }
        isGettingMyLocation = false;
        return null;
    }

    private double calcMedia(double summary, int nrOfGpsRead) {
        double toReturn = 0;
        try {
            if (nrOfGpsRead != 0) {
                toReturn = summary / nrOfGpsRead;
            }
        } catch (Exception e) {
        }
        return toReturn;
    }

    private GpsPoint setGpsPoint(MyLocation oldLocation, MyLocation myLocation) {
        GpsPoint gpsPoint = new GpsPoint();
        gpsPoint.setLatitudine(myLocation.getMyLatitude());
        gpsPoint.setLongitudine(myLocation.getMyLongitude());
        gpsPoint.setAltitudine(myLocation.getMyAltitude());
        gpsPoint.setVelocity(myLocation.getMySpeed());
        gpsPoint.setDistance(myLocation.getDistance());
        gpsPoint.setTimeInterval((long) myLocation.getMyTime());
        gpsPoint.setNrOfFixSatellites(myLocation.getNrOfSatellites());
        gpsPoint.setTimeToFix(myLocation.getTimeToFix());
        gpsPoint.setAccuracy(myLocation.getAccuracy());
        return gpsPoint;
    }

    public GpsPoint getGpsPoint() {
        return gpsPoint;
    }

    private void setSpeed(MyLocation newLocation , MyLocation oldLocation) {

        try {
            if (newLocation.getDistance() == 0) {
                velocity = 0;
            } else {
                sumDistance += newLocation.getDistance();
                sumTime += newLocation.getMyTime();
                nrForSpeed++;
                if (nrForSpeed > COUNTER_FOR_SPEED) {
                    // double tempo = myLocation.getMyTime() - oldLocation.getMyTime();
                    //double tempo = newLocation.getMyTime();
                    //Log.d(TAG, "tempo : " + tempo);
                    //if (tempo != 0) {
                    //    velocity = newLocation.getDistance() / tempo * 3600000;
                    //} else {
                    //    velocity = 0;
                    //}
                    if (sumTime != 0) {
                        velocity = sumDistance / sumTime * 3600000;
                    } else {
                        velocity = 0;
                    }
                    sumDistance=0;
                    sumTime=0;
                    nrForSpeed=0;
                }
            }
//            Toast.makeText(context, "Distanza:" + newLocation.getDistance() +"\nTempo:" + newLocation.getMyTime() + "\nVelocità:" + newLocation.getMySpeed() , Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Errore:" + e.getMessage() , Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "Velocità : " + velocity);
        newLocation.setMySpeed(velocity);
    }

    private void setDistance(MyLocation newLocation , MyLocation oldLocation) {
        double distance = 0.0;
        double radius = EARTH_RADIUS_EQUATOR + (EARTH_RADIUS_POLE - EARTH_RADIUS_EQUATOR) * (oldLocation.getMyLatitude() + newLocation.getMyLatitude()) / Math.PI;
        radius = 6372.795477598;
        double temp = Math.acos(
                Math.sin(getRadiant(oldLocation.getMyLatitude())) *
                        Math.sin(getRadiant(newLocation.getMyLatitude()))
                        +
                        Math.cos(getRadiant(oldLocation.getMyLatitude())) *
                                Math.cos(getRadiant(newLocation.getMyLatitude())) *
                                Math.cos(getRadiant(newLocation.getMyLongitude()) -
                                        getRadiant(oldLocation.getMyLongitude()))) * radius;
        Log.d(TAG, "Distanza : " + temp);
        if (((Double) temp).isNaN()) {
            temp =0;
        }

        distance = temp;
        newLocation.setDistance(distance);
    }

    private double getRadiant(double gradi) {
        return (Math.PI * gradi / 180);
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        // parto da minima accuracy
        if (location.getAccuracy() > MIN_ACCURACY) {
            return false;
        }

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public void setGpsPoint(GpsPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }
}
