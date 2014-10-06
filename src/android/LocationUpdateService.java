package com.tenforwardconsulting.cordova.bgloc;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.tenforwardconsulting.cordova.bgloc.data.DAOFactory;
import com.tenforwardconsulting.cordova.bgloc.data.LocationDAO;
import com.tenforwardconsulting.cordova.bgloc.data.CardDAO;

import android.annotation.TargetApi;

import android.media.AudioManager;
import android.media.ToneGenerator;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import static android.telephony.PhoneStateListener.*;
import android.telephony.CellLocation;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import android.location.Location;
import android.location.Criteria;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;

import android.util.Log;
import android.widget.Toast;

import static java.lang.Math.*;

public class LocationUpdateService extends Service implements LocationListener {
    private static final String TAG = "LocationUpdateService";
    private static final String STATIONARY_REGION_ACTION        = "com.tenforwardconsulting.cordova.bgloc.STATIONARY_REGION_ACTION";
    private static final String STATIONARY_ALARM_ACTION         = "com.tenforwardconsulting.cordova.bgloc.STATIONARY_ALARM_ACTION";
    private static final String SINGLE_LOCATION_UPDATE_ACTION   = "com.tenforwardconsulting.cordova.bgloc.SINGLE_LOCATION_UPDATE_ACTION";
    private static final String STATIONARY_LOCATION_MONITOR_ACTION = "com.tenforwardconsulting.cordova.bgloc.STATIONARY_LOCATION_MONITOR_ACTION";
    private static final String NOTIFICATION_CONFIRM_ACTION         = "com.tenforwardconsulting.cordova.bgloc.NOTIFICATION_CONFIRM_ACTION";
    private static final String NOTIFICATION_DISCARD_ACTION         = "com.tenforwardconsulting.cordova.bgloc.NOTIFICATION_DISCARD_ACTION";
    private static final String NOTIFICATION_ARG_ID =           "NOTIF_ID";
    private static final String NOTIFICATION_ARG_CARD_ID =           "CARD_ID";
    private static final long STATIONARY_TIMEOUT                                = 5 * 1000 * 60;    // 5 minutes.
    private static final long STATIONARY_LOCATION_POLLING_INTERVAL_LAZY         = 3 * 1000 * 60;    // 3 minutes.  
    private static final long STATIONARY_LOCATION_POLLING_INTERVAL_AGGRESSIVE   = 1 * 1000 * 60;    // 1 minute.
    private static final Integer MAX_STATIONARY_ACQUISITION_ATTEMPTS = 5;
    private static final Integer MAX_SPEED_ACQUISITION_ATTEMPTS = 3;
    private static Context mContext;
    private static String message;
    
    private PowerManager.WakeLock wakeLock;
    private Location lastLocation;
    private long lastUpdateTime = 0l;
    
    private JSONObject params;
    private JSONObject params_share;
    private JSONObject headers;
    private String url = "http://192.168.2.15:3000/users/current_location.json";

    private float stationaryRadius;
    private Location stationaryLocation;
    private PendingIntent stationaryAlarmPI;
    private PendingIntent stationaryLocationPollingPI;
    private long stationaryLocationPollingInterval;
    private PendingIntent stationaryRegionPI;
    private PendingIntent singleUpdatePI;
    private PendingIntent notificationConfirmPI;
    //private PendingIntent notificationDiscardPI;
    
    private Boolean isMoving = false;
    private Boolean isAcquiringStationaryLocation = false;
    private Boolean isAcquiringSpeed = false;
    private Integer locationAcquisitionAttempts = 0;
    
    private Integer desiredAccuracy = 100;
    private Integer distanceFilter = 30;
    private Integer scaledDistanceFilter;
    private Integer locationTimeout = 30;
    private Boolean isDebugging;
    private String notificationTitle = "Background checking";
    private String notificationText = "ENABLED";

    private ToneGenerator toneGenerator;
    
    private Criteria criteria;
    
    private LocationManager locationManager;
    private AlarmManager alarmManager;
    private ConnectivityManager connectivityManager;
    private NotificationManager notificationManager;
    public static TelephonyManager telephonyManager = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.i(TAG, "OnBind" + intent);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Log.i(TAG, "OnCreate");
        
        locationManager         = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        alarmManager            = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        toneGenerator           = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        connectivityManager     = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        notificationManager     = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        telephonyManager        = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        
        // Stop-detection PI
        stationaryAlarmPI   = PendingIntent.getBroadcast(this, 0, new Intent(STATIONARY_ALARM_ACTION), 0);
        registerReceiver(stationaryAlarmReceiver, new IntentFilter(STATIONARY_ALARM_ACTION));
        
        // Stationary region PI
        stationaryRegionPI  = PendingIntent.getBroadcast(this, 0, new Intent(STATIONARY_REGION_ACTION), PendingIntent.FLAG_CANCEL_CURRENT);
        registerReceiver(stationaryRegionReceiver, new IntentFilter(STATIONARY_REGION_ACTION));
        
        // Stationary location monitor PI
        stationaryLocationPollingPI = PendingIntent.getBroadcast(this, 0, new Intent(STATIONARY_LOCATION_MONITOR_ACTION), 0);
        registerReceiver(stationaryLocationMonitorReceiver, new IntentFilter(STATIONARY_LOCATION_MONITOR_ACTION));
        
        // One-shot PI (TODO currently unused)  
        singleUpdatePI = PendingIntent.getBroadcast(this, 0, new Intent(SINGLE_LOCATION_UPDATE_ACTION), PendingIntent.FLAG_CANCEL_CURRENT);
        registerReceiver(singleUpdateReceiver, new IntentFilter(SINGLE_LOCATION_UPDATE_ACTION));
        
        // Notification Confirm Monitor PI
        notificationConfirmPI   = PendingIntent.getBroadcast(this, 0, new Intent(NOTIFICATION_CONFIRM_ACTION), 0);
        registerReceiver(notificatinConfirmReceiver, new IntentFilter(NOTIFICATION_CONFIRM_ACTION));

        // Notification Discard Monitor PI
        //notificationDiscardPI   = PendingIntent.getBroadcast(this, 0, new Intent(NOTIFICATION_DISCARD_ACTION), 0);
        //registerReceiver(notificatinDiscardReceiver, new IntentFilter(NOTIFICATION_DISCARD_ACTION));
        
        ////
        // DISABLED
        // Listen to Cell-tower switches (NOTE does not operate while suspended)
        //telephonyManager.listen(phoneStateListener, LISTEN_CELL_LOCATION);
        //
        
        PowerManager pm         = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        wakeLock.acquire();
        
        // Location criteria
        criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        if (intent != null) { 
            try {
                params = new JSONObject(intent.getStringExtra("params"));
                headers = new JSONObject(intent.getStringExtra("headers"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            url = intent.getStringExtra("url");
            stationaryRadius = Float.parseFloat(intent.getStringExtra("stationaryRadius"));
            distanceFilter = Integer.parseInt(intent.getStringExtra("distanceFilter"));
            scaledDistanceFilter = distanceFilter;
            desiredAccuracy = Integer.parseInt(intent.getStringExtra("desiredAccuracy"));
            locationTimeout = Integer.parseInt(intent.getStringExtra("locationTimeout"));
            isDebugging = Boolean.parseBoolean(intent.getStringExtra("isDebugging"));
            notificationTitle = intent.getStringExtra("notificationTitle");
            notificationText = intent.getStringExtra("notificationText");

            // Build a Notification required for running service in foreground.
            Intent main = new Intent(this, BackgroundGpsPlugin.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, main,  PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle(notificationTitle);
            builder.setContentText(notificationText);
            builder.setSmallIcon(android.R.drawable.ic_menu_mylocation);
            builder.setContentIntent(pendingIntent);
            Notification notification;
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                notification = buildForegroundNotification(builder);
            } else {
                notification = buildForegroundNotificationCompat(builder);
            }
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;
            startForeground(startId, notification);
        }
        Log.i(TAG, "- url: " + url);
        Log.i(TAG, "- params: " + params.toString());
        Log.i(TAG, "- headers: " + headers.toString());
        Log.i(TAG, "- stationaryRadius: "   + stationaryRadius);
        Log.i(TAG, "- distanceFilter: "     + distanceFilter);
        Log.i(TAG, "- desiredAccuracy: "    + desiredAccuracy);
        Log.i(TAG, "- locationTimeout: "    + locationTimeout);
        Log.i(TAG, "- isDebugging: "        + isDebugging);
        Log.i(TAG, "- notificationTitle: "  + notificationTitle);
        Log.i(TAG, "- notificationText: "   + notificationText);

        this.setPace(false);
        
        //We want this service to continue running until it is explicitly stopped
        return START_REDELIVER_INTENT;
    }
    
    @TargetApi(16)
    private Notification buildForegroundNotification(Notification.Builder builder) {
        return builder.build();
    }
    
    @SuppressWarnings("deprecation")
    @TargetApi(15)
    private Notification buildForegroundNotificationCompat(Notification.Builder builder) {
        return builder.getNotification();
    }

    @Override
    public boolean stopService(Intent intent) {
        Log.i(TAG, "- Received stop: " + intent);
        cleanUp();
        if (isDebugging) {
            Toast.makeText(this, "Background location tracking stopped", Toast.LENGTH_SHORT).show();
        }
        return super.stopService(intent);
    }
    
    /**
     * 
     * @param value set true to engage "aggressive", battery-consuming tracking, false for stationary-region tracking
     */
    private void setPace(Boolean value) {
        Log.i(TAG, "setPace: " + value);
        
        Boolean wasMoving   = isMoving;
        isMoving            = value;
        isAcquiringStationaryLocation = false;
        isAcquiringSpeed    = false;
        stationaryLocation  = null;
        
        locationManager.removeUpdates(this);
        
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setHorizontalAccuracy(translateDesiredAccuracy(desiredAccuracy));
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        
        if (isMoving) {
            // setPace can be called while moving, after distanceFilter has been recalculated.  We don't want to re-acquire velocity in this case.
            if (!wasMoving) {
                isAcquiringSpeed = true;
            }
        } else {
            isAcquiringStationaryLocation = true;
        }

        // Temporarily turn on super-aggressive geolocation on all providers when acquiring velocity or stationary location.
        if (isAcquiringSpeed || isAcquiringStationaryLocation) {
            locationAcquisitionAttempts = 0;
            // Turn on each provider aggressively for a short period of time
            List<String> matchingProviders = locationManager.getAllProviders();
            for (String provider: matchingProviders) {
                if (provider != LocationManager.PASSIVE_PROVIDER) { 
                    locationManager.requestLocationUpdates(provider, 0, 0, this);
                }
            }
        } else {
            locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), locationTimeout*1000, scaledDistanceFilter, this);
        }
    }

    /**
    * Translates a number representing desired accuracy of GeoLocation system from set [0, 10, 100, 1000].
    * 0:  most aggressive, most accurate, worst battery drain
    * 1000:  least aggressive, least accurate, best for battery.
    */
    private Integer translateDesiredAccuracy(Integer accuracy) {
        switch (accuracy) {
            case 1000:
                accuracy = Criteria.ACCURACY_LOW;
                break;
            case 100:
                accuracy = Criteria.ACCURACY_MEDIUM;
                break;
            case 10:
                accuracy = Criteria.ACCURACY_HIGH;
                break;
            case 0:
                accuracy = Criteria.ACCURACY_HIGH;
                break;
            default:
                accuracy = Criteria.ACCURACY_MEDIUM;
        }
        return accuracy;
    }

    /**
     * Returns the most accurate and timely previously detected location.
     * Where the last result is beyond the specified maximum distance or
     * latency a one-off location update is returned via the {@link LocationListener}
     * specified in {@link setChangedLocationListener}.
     * @param minDistance Minimum distance before we require a location update.
     * @param minTime Minimum time required between location updates.
     * @return The most accurate and / or timely previously detected location.
     */
    public Location getLastBestLocation() {
        int minDistance = (int) stationaryRadius;
        long minTime    = System.currentTimeMillis() - (locationTimeout * 1000);
        
        Log.i(TAG, "- fetching last best location " + minDistance + "," + minTime);
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Iterate through all the providers on the system, keeping
        // note of the most accurate result within the acceptable time limit.
        // If no result is found within maxTime, return the newest Location.
        List<String> matchingProviders = locationManager.getAllProviders();
        for (String provider: matchingProviders) {
            Log.d(TAG, "- provider: " + provider);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.d(TAG, " location: " + location.getLatitude() + "," + location.getLongitude() + "," + location.getAccuracy() + "," + location.getSpeed() + "m/s");
                float accuracy = location.getAccuracy();
                long time = location.getTime();
                Log.d(TAG, "time>minTime: " + (time > minTime) + ", accuracy<bestAccuracy: " + (accuracy < bestAccuracy));
                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
            }
        }
        return bestResult;
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "- onLocationChanged: " + location.getLatitude() + "," + location.getLongitude() + ", accuracy: " + location.getAccuracy() + ", isMoving: " + isMoving + ", speed: " + location.getSpeed());
        
        if (!isMoving && !isAcquiringStationaryLocation && stationaryLocation==null) {
            // Perhaps our GPS signal was interupted, re-acquire a stationaryLocation now.
            setPace(false);
        }
        
        if (isDebugging) {
            Toast.makeText(this, "mv:"+isMoving+",acy:"+location.getAccuracy()+",v:"+location.getSpeed()+",df:"+scaledDistanceFilter, Toast.LENGTH_LONG).show();
        }
        if (isAcquiringStationaryLocation) {
            if (stationaryLocation == null || stationaryLocation.getAccuracy() > location.getAccuracy()) {
                stationaryLocation = location;
            }
            if (++locationAcquisitionAttempts == MAX_STATIONARY_ACQUISITION_ATTEMPTS) {
                isAcquiringStationaryLocation = false;
                startMonitoringStationaryRegion(stationaryLocation);
                if (isDebugging) {
                    startTone("long_beep");
                }
            } else {
                // Unacceptable stationary-location: bail-out and wait for another.
                if (isDebugging) {
                    startTone("beep");
                }
                return;
            }
        } else if (isAcquiringSpeed) {
            if (++locationAcquisitionAttempts == MAX_SPEED_ACQUISITION_ATTEMPTS) {
                // Got enough samples, assume we're confident in reported speed now.  Play "woohoo" sound.
                if (isDebugging) {
                    startTone("doodly_doo");
                }
                isAcquiringSpeed = false;
                scaledDistanceFilter = calculateDistanceFilter(location.getSpeed());
                setPace(true);
            } else {
                if (isDebugging) {
                    startTone("beep");
                }
                return;
            }
        } else if (isMoving) {
            if (isDebugging) {
                startTone("beep");
            }
            // Only reset stationaryAlarm when accurate speed is detected, prevents spurious locations from resetting when stopped.
            if ( (location.getSpeed() >= 1) && (location.getAccuracy() <= stationaryRadius) ) {
                resetStationaryAlarm();
            }
            // Calculate latest distanceFilter, if it changed by 5 m/s, we'll reconfigure our pace.
            Integer newDistanceFilter = calculateDistanceFilter(location.getSpeed());
            if (newDistanceFilter != scaledDistanceFilter.intValue()) {
                Log.i(TAG, "- updated distanceFilter, new: " + newDistanceFilter + ", old: " + scaledDistanceFilter);
                scaledDistanceFilter = newDistanceFilter;
                setPace(true);
            }
            if (location.distanceTo(lastLocation) < distanceFilter) {
                return;
            }
        } else if (stationaryLocation != null) {
            return;
        }
        // Go ahead and cache, push to server
        lastLocation = location;
        persistLocation(location);

        if (this.isNetworkConnected()) {
            Log.d(TAG, "Scheduling location network post");
            schedulePostLocations();
        } else {
            Log.d(TAG, "Network unavailable, waiting for now");
        }
    }
    
    /**
     * Plays debug sound
     * @param name
     */
    private void startTone(String name) {
        int tone = 0;
        int duration = 1000;
        
        if (name.equals("beep")) {
            tone = ToneGenerator.TONE_PROP_BEEP;
        } else if (name.equals("beep_beep_beep")) {
            tone = ToneGenerator.TONE_CDMA_CONFIRM;
        } else if (name.equals("long_beep")) {
            tone = ToneGenerator.TONE_CDMA_ABBR_ALERT;
        } else if (name.equals("doodly_doo")) {
            tone = ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE;
        } else if (name.equals("chirp_chirp_chirp")) {
            tone = ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD;
        } else if (name.equals("dialtone")) {
            tone = ToneGenerator.TONE_SUP_RINGTONE;
        }
        toneGenerator.startTone(tone, duration);
    }
    
    public void resetStationaryAlarm() {
        alarmManager.cancel(stationaryAlarmPI);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + STATIONARY_TIMEOUT, stationaryAlarmPI); // Millisec * Second * Minute
    }

    private Integer calculateDistanceFilter(Float speed) {
        Double newDistanceFilter = (double) distanceFilter;
        if (speed < 100) {
            float roundedDistanceFilter = (round(speed / 5) * 5);
            newDistanceFilter = pow(roundedDistanceFilter, 2) + (double) distanceFilter;
        }
        return (newDistanceFilter.intValue() < 1000) ? newDistanceFilter.intValue() : 1000;
    }

    private void startMonitoringStationaryRegion(Location location) {
        locationManager.removeUpdates(this);
        stationaryLocation = location;
        
        Log.i(TAG, "- startMonitoringStationaryRegion (" + location.getLatitude() + "," + location.getLongitude() + "), accuracy:" + location.getAccuracy());

        // Here be the execution of the stationary region monitor
        locationManager.addProximityAlert(
                location.getLatitude(),
                location.getLongitude(),
                (location.getAccuracy() < stationaryRadius) ? stationaryRadius : location.getAccuracy(),
                (long)-1,
                stationaryRegionPI
        );
        
        startPollingStationaryLocation(STATIONARY_LOCATION_POLLING_INTERVAL_LAZY);
    }
    
    public void startPollingStationaryLocation(long interval) {
        // proximity-alerts don't seem to work while suspended in latest Android 4.42 (works in 4.03).  Have to use AlarmManager to sample
        //  location at regular intervals with a one-shot.
        stationaryLocationPollingInterval = interval;
        alarmManager.cancel(stationaryLocationPollingPI);
        long start = System.currentTimeMillis() + (60 * 1000);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, start, interval, stationaryLocationPollingPI);
    }
    
    public void onPollStationaryLocation(Location location) {
        if (isMoving) {
            return;
        }
        if (isDebugging) {
            startTone("beep");
        }
	float distance = abs(location.distanceTo(stationaryLocation) - stationaryLocation.getAccuracy() - location.getAccuracy());
        
        if (isDebugging) {
            Toast.makeText(this, "Stationary exit in " + (stationaryRadius-distance) + "m", Toast.LENGTH_LONG).show();
        }
        
        // TODO http://www.cse.buffalo.edu/~demirbas/publications/proximity.pdf
        // determine if we're almost out of stationary-distance and increase monitoring-rate.
        Log.i(TAG, "- distance from stationary location: " + distance);
        if (distance > stationaryRadius) {
            onExitStationaryRegion(location);
        } else if (distance > 0) {
            startPollingStationaryLocation(STATIONARY_LOCATION_POLLING_INTERVAL_AGGRESSIVE);
        } else if (stationaryLocationPollingInterval != STATIONARY_LOCATION_POLLING_INTERVAL_LAZY) {
            startPollingStationaryLocation(STATIONARY_LOCATION_POLLING_INTERVAL_LAZY);
        }
    }
    /**
    * User has exit his stationary region!  Initiate aggressive geolocation!
    */
    public void onExitStationaryRegion(Location location) {
        // Filter-out spurious region-exits:  must have at least a little speed to move out of stationary-region
        if (isDebugging) {
            startTone("beep_beep_beep");
        }
        // Cancel the periodic stationary location monitor alarm.
        alarmManager.cancel(stationaryLocationPollingPI);
        
        // Kill the current region-monitor we just walked out of.
        locationManager.removeProximityAlert(stationaryRegionPI);
        
        // Engage aggressive tracking.
        this.setPace(true);
    }
    
    /**
    * TODO Experimental cell-tower change system; something like ios significant changes.
    */
    public void onCellLocationChange(CellLocation cellLocation) {
        Log.i(TAG, "- onCellLocationChange" + cellLocation.toString());
        if (isDebugging) {
            Toast.makeText(this, "Cellular location change", Toast.LENGTH_LONG).show();
            startTone("chirp_chirp_chirp");
        }
        if (!isMoving && stationaryLocation != null) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            locationManager.requestSingleUpdate(criteria, singleUpdatePI);
        }
    }

    /**
    * Broadcast receiver for receiving a single-update from LocationManager.
    */
    private BroadcastReceiver singleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = LocationManager.KEY_LOCATION_CHANGED;
            Location location = (Location)intent.getExtras().get(key);
            if (location != null) {
                Log.d(TAG, "- singleUpdateReciever" + location.toString());
                onPollStationaryLocation(location);
            }
        }
    };
    
    /**
    * Broadcast receiver which detcts a user has stopped for a long enough time to be determined as STOPPED
    */
    private BroadcastReceiver stationaryAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "- stationaryAlarm fired");
            setPace(false);
        }
    };
    /**
     * Broadcast receiver to handle stationaryMonitor alarm, fired at low frequency while monitoring stationary-region.
     * This is required because latest Android proximity-alerts don't seem to operate while suspended.  Regularly polling
     * the location seems to trigger the proximity-alerts while suspended.
     */
     private BroadcastReceiver stationaryLocationMonitorReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent)
         {
             Log.i(TAG, "- stationaryLocationMonitorReceiver fired");
             if (isDebugging) {
                 startTone("dialtone");
             }
             criteria.setAccuracy(Criteria.ACCURACY_FINE);
             criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
             criteria.setPowerRequirement(Criteria.POWER_HIGH);
             locationManager.requestSingleUpdate(criteria, singleUpdatePI);
         }
     };
    /**
    * Broadcast receiver which detects a user has exit his circular stationary-region determined by the greater of stationaryLocation.getAccuracy() OR stationaryRadius
    */
    private BroadcastReceiver stationaryRegionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "stationaryRegionReceiver");
            String key = LocationManager.KEY_PROXIMITY_ENTERING;

            Boolean entering = intent.getBooleanExtra(key, false);
            if (entering) {
                Log.d(TAG, "- ENTER");
                if (isMoving) {
                    setPace(false);
                }
            }
            else {
                Log.d(TAG, "- EXIT");
                // There MUST be a valid, recent location if this event-handler was called.
                Location location = getLastBestLocation();
                if (location != null) {
                    onExitStationaryRegion(location);
                }
            }
        }
    };
    /**
    * Listen to Notification confirmation action
    */
    private BroadcastReceiver notificatinConfirmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "- CONFIRMED CARD ACTION");
            if (intent.hasExtra(NOTIFICATION_ARG_CARD_ID)){
                Log.i(TAG, "- YES CARD EXTRA");
            }else{
                Log.i(TAG, "- NO CARD EXTRA");
            }
            
            int notificationId = intent.getIntExtra(NOTIFICATION_ARG_ID, -1);
            int notificationCardId = intent.getIntExtra(NOTIFICATION_ARG_CARD_ID, -1);
            //boolean confirmed_card = true;
            Log.i(TAG, "- NOTIFICATION CARD ID: " + intent.getIntExtra(NOTIFICATION_ARG_CARD_ID, -1));
            if (notificationCardId > 0){
                new ShareTask().execute(notificationCardId);
                /*
                CardDAO cdao = DAOFactory.createCardDAO(context);
                com.tenforwardconsulting.cordova.bgloc.data.Card confirmCard = cdao.getCardById("pending_confirm", notificationCardId);
                if (confirmCard != null){
                    Log.i(TAG, "Confirm Sharing");
                    
                    if (shareCard(confirmCard)){
                        if (cdao.persistCard("shared_cards", confirmCard)) {
                            Log.d(TAG, "Persisted Card in shared_cards: " + confirmCard);
                        } else {
                            Log.w(TAG, "CARD SHARED! but failed to persist card in shared_cards table");
                        }
                    }
                    else{
                        if (cdao.persistCard("pending_internet", confirmCard)) {
                            Log.d(TAG, "Persisted Card in pending_internet: " + confirmCard);
                        } else {
                            Log.w(TAG, "Failed to persist card in pending_internet table");
                            confirmed_card = false;
                        }
                    }
                    if (confirmed_card){
                        cdao.deleteCard("pending_confirm", confirmCard);
                        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                        if (notificationId >= 0){
                            mNotificationManager.cancel(notificationId);
                        }
                    }
                }
                */
            }
        }
    };

    private class ShareTask extends AsyncTask<int, void, boolean> {
        protected boolean doInBackground(int... confirmCardId) {
            boolean confirmed_card = true;
            //CardDAO cdao = DAOFactory.createCardDAO(context);
            CardDAO cdao = DAOFactory.createCardDAO(LocationUpdateService.this.getApplicationContext());
            com.tenforwardconsulting.cordova.bgloc.data.Card confirmCard = cdao.getCardById("pending_confirm", confirmCardId);
            if (confirmCard != null){
                Log.i(TAG, "Confirm Sharing");
                
                if (shareCard(confirmCard)){
                    if (cdao.persistCard("shared_cards", confirmCard)) {
                        Log.d(TAG, "Persisted Card in shared_cards: " + confirmCard);
                    } else {
                        Log.w(TAG, "CARD SHARED! but failed to persist card in shared_cards table");
                    }
                }
                else{
                    if (cdao.persistCard("pending_internet", confirmCard)) {
                        Log.d(TAG, "Persisted Card in pending_internet: " + confirmCard);
                    } else {
                        Log.w(TAG, "Failed to persist card in pending_internet table");
                        confirmed_card = false;
                    }
                }
                if (confirmed_card){
                    cdao.deleteCard("pending_confirm", confirmCard);
                    NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    if (notificationId >= 0){
                        mNotificationManager.cancel(notificationId);
                    }
                }
            }
            return confirmed_card;
        }

        protected void onPostExecute(boolean result) {
        }
    }
    /**
    * Listen to Notification discarding action
    */
    private BroadcastReceiver notificatinDiscardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {            
            int notificationId = intent.getIntExtra(NOTIFICATION_ARG_ID, -1);
            NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if (notificationId>=0){
                mNotificationManager.cancel(notificationId);
            }
        }
    };
    /**
    * TODO Experimental, hoping to implement some sort of "significant changes" system here like ios based upon cell-tower changes.
    */
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCellLocationChanged(CellLocation location)
        {
            onCellLocationChange(location);
        }
    };

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Log.d(TAG, "- onProviderDisabled: " + provider);
    }
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Log.d(TAG, "- onProviderEnabled: " + provider);
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        Log.d(TAG, "- onStatusChanged: " + provider + ", status: " + status);
    }
    private void schedulePostLocations() {
        PostLocationTask task = new LocationUpdateService.PostLocationTask();
        Log.d(TAG, "beforeexecute " +  task.getStatus());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
        Log.d(TAG, "afterexecute " +  task.getStatus());
    }

    private boolean postCard(com.tenforwardconsulting.cordova.bgloc.data.Card geoCard) {
        if (geoCard == null) {
            Log.w(TAG, "postCard: invalid geo card");
            return false;
        }else{
            try {
                Log.i(TAG, "Posting  native card: " + geoCard);
                
                //Get user settings for creating and sharing a card
                SharedPreferences pref = mContext.getSharedPreferences("lifesharePreferences", Context.MODE_MULTI_PROCESS);
                //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                String location_setting = pref.getString("location_setting", "");
                String sharing_setting = pref.getString("sharing_setting", "");
                String user_id = pref.getString("user_id", "");
                
                String curAdd = getAddress(Double.parseDouble(geoCard.getLatitude()), Double.parseDouble(geoCard.getLongitude()), geoCard.getLocation_level());
                if (curAdd == null){
                    if (isNetworkConnected()){
                        postNotification("Error", "Please reset your device and start the application again", -1);
                    }
                    return false;                    
                }
                String curInfo = getInfo();

                //Control to avoid creating redundant cards
                SharedPreferences.Editor edit = pref.edit();

                String lastAdd = pref.getString("lastAddress", "");
                String lastInfo = pref.getString("lastInfo", "");

                /*if (curAdd.equals(lastAdd) && curInfo.equals(lastInfo)){
                    Log.i(TAG, "repeated card");
                    //postNotification(curInfo, curAdd + " (Not shared)", -1);
                    return true;
                }else{*/
                    Log.i(TAG, "new card");
                    edit.putString("lastAddress", curAdd);
                    edit.putString("lastInfo", curInfo);
                    edit.commit();
                    geoCard.setInfo(curInfo);
                    geoCard.setLocation(curAdd);
                //}

                CardDAO cdao = DAOFactory.createCardDAO(this.getApplicationContext());

                //Create a notification if necessary
                if (sharing_setting.equals("confirm")){
                    if (cdao.persistCard("pending_confirm", geoCard)) {
                        Log.i(TAG, "Confirm Sharing");
                        //SEND NOTIFICATION
                        postNotification(curInfo, curAdd, geoCard.getId());
                        Log.d(TAG, "Persisted Card in pending_confirm: " + geoCard);
                        return true;
                    } else {
                        Log.w(TAG, "Failed to persist card in pending_confirm table");
                        return false;
                    }
                }else{
                    Log.i(TAG, "Automatic Sharing");
                    if (shareCard(geoCard)){
                        if (cdao.persistCard("shared_cards", geoCard)) {
                            Log.d(TAG, "Persisted Card in shared_cards: " + geoCard);
                        } else {
                            Log.w(TAG, "CARD SHARED! but failed to persist card in shared_cards table");
                        }
                        return true;
                    }
                    else{
                        if (cdao.persistCard("pending_internet", geoCard)) {
                            Log.d(TAG, "Persisted Card in pending_internet: " + geoCard);
                            return true;
                        } else {
                            Log.w(TAG, "Failed to persist card in pending_internet table");
                            return false;
                        }
                    }
                }
            } catch (Throwable e) {
                Log.w(TAG, "Exception updating geo card: " + e);
                e.printStackTrace();
                return false;
            }
        }
    }

    private boolean shareCard(com.tenforwardconsulting.cordova.bgloc.data.Card geoCard){
        try {
            Log.i(TAG, "SS 11");
            //params.remove("LocationSetting");
            //Log.i(TAG, "SS 22");
            //params.remove("SharingSetting");
            //params.remove("UserId");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);
            Log.i(TAG, "SS 33");

            //Proces for creating the card on the server
            params_share = new JSONObject();
            params_share.put("info", geoCard.getInfo());
            params_share.put("lat", geoCard.getLatitude());
            params_share.put("lon", geoCard.getLongitude());
            params_share.put("location", geoCard.getLocation());
            params_share.put("timestamp", geoCard.getCreated());
            Log.i(TAG, "info: " + geoCard.getInfo() + " - location: " + geoCard.getLocation());

            /*
            params.put("info", geoCard.getInfo());
            params.put("lat", geoCard.getLatitude());
            params.put("lon", geoCard.getLongitude());
            params.put("location", geoCard.getLocation());
            params.put("timestamp", geoCard.getCreated());
            */
            Log.i(TAG, "SS 44");

            StringEntity se = new StringEntity(params_share.toString());
            //StringEntity se = new StringEntity(params.toString());
            request.setEntity(se);
            request.setHeader("Content-type", "application/json");

            Iterator<String> headkeys = headers.keys();
            while( headkeys.hasNext() ){
                String headkey = headkeys.next();
                if(headkey != null) {
                    Log.d(TAG, "Adding Header: " + headkey + " : " + (String)headers.getString(headkey));
                    request.setHeader(headkey, (String)headers.getString(headkey));
                }
            }

            Log.i(TAG, "SS 55");

            Log.d(TAG, "Posting to " + request.getURI().toString());
            HttpResponse response = httpClient.execute(request);
            Log.i(TAG, "Response received: " + response.getStatusLine());
            if ((response.getStatusLine().getStatusCode() == 200) || (response.getStatusLine().getStatusCode() == 204)) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            Log.w(TAG, "Exception sharing card: " + e);
            e.printStackTrace();
            return false;
        }
    }

    private void postNotification(String info, String loc, int cardId){
        //Intent notificationServiceIntent;
        //notificationServiceIntent = new Intent(this, LifeshareNotificationService.class);
        //PendingIntent pintent = PendingIntent.getService(mContext, 0, notificationServiceIntent, 0);

        //Intent snoozeIntent = new Intent("confirm");
        //PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);

        Notification.Builder shareLocBuilder = new Notification.Builder(this);
        shareLocBuilder.setContentTitle("Lifeshare Card");
        shareLocBuilder.setContentText(info + " " + loc);
        shareLocBuilder.setSmallIcon(android.R.drawable.ic_menu_mylocation);

        int notifiId = getNotificationId();

        //Construct the Confirm Action button for the notification
        //We need to create a specific intent or else the putExtra data will be overwritten be the new notification
        Intent notificationConfirmIntent = new Intent(NOTIFICATION_CONFIRM_ACTION+notifiId);
        //We need to notify the broadcast listener to listen for the new created intent
        registerReceiver(notificatinConfirmReceiver, new IntentFilter(NOTIFICATION_CONFIRM_ACTION+notifiId));
        //For the discard action we only need the notification id
        notificationConfirmIntent.putExtra(NOTIFICATION_ARG_ID, notifiId);
        notificationConfirmIntent.putExtra(NOTIFICATION_ARG_CARD_ID, cardId);
        //We create the Pending intent using the created intent. FLAG_UPDATE_CURRENT is needed or else the putExtra does not "put" the data
        PendingIntent piConfirm = PendingIntent.getBroadcast(this, 0, notificationConfirmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //We add the created intent to the notification
        shareLocBuilder.addAction(android.R.drawable.ic_menu_agenda, "Confirm", piConfirm);

        //Construct the Discard Action button for the notification
        //We need to create a specific intent or else the putExtra data will be overwritten be the new notification
        Intent notificationDiscardIntent = new Intent(NOTIFICATION_DISCARD_ACTION+notifiId);
        //We need to notify the broadcast listener to listen for the new created intent
        registerReceiver(notificatinDiscardReceiver, new IntentFilter(NOTIFICATION_DISCARD_ACTION+notifiId));
        //For the discard action we only need the notification id
        notificationDiscardIntent.putExtra(NOTIFICATION_ARG_ID, notifiId);
        //We create the Pending intent using the created intent. FLAG_UPDATE_CURRENT is needed or else the putExtra does not "put" the data
        PendingIntent piDiscard = PendingIntent.getBroadcast(this, 0, notificationDiscardIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //We add the created intent to the notification
        shareLocBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Discard", piDiscard);

        Notification shareNotification;
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            shareNotification = buildForegroundNotification(shareLocBuilder);
        } else {
            shareNotification = buildForegroundNotificationCompat(shareLocBuilder);
        }
        //shareNotification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;
        //shareNotification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE;
        shareNotification.flags |= Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
            // Including the notification ID allows you to update the notification later on.
        mNotificationManager.notify(notifiId, shareNotification);
    }

    private int getNotificationId() {
        SharedPreferences pref = mContext.getSharedPreferences("lifesharePreferences", Context.MODE_MULTI_PROCESS);
        int currentNotId = pref.getInt("notificationId", 1);

        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("notificationId", currentNotId+1);
        edit.commit();
        
        return currentNotId;
    }

    private String getInfo(){
        Date currentdate = new Date();
        DateFormat df = new SimpleDateFormat("HH");
        String curInfo = "is at";
        if (Integer.parseInt(df.format(currentdate)) <= 6){
            curInfo = "is sleeping";
        } else if (Integer.parseInt(df.format(currentdate)) >= 12 && Integer.parseInt(df.format(currentdate)) <=14){
            curInfo = "is having lunch";
        }
        return curInfo;
    }

    private String getAddress(double lat, double lng, String location_setting) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String revCity = null;
        String revRegion =  null;
        String revCountry = null;
        String add = "unavailable";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null)
                if (!addresses.isEmpty()) {
                    Address obj = addresses.get(0);
                    if (obj.getLocality() != null)
                        revCity = obj.getLocality();
                    if (obj.getAdminArea() != null)
                        revRegion = obj.getAdminArea();
                    if (obj.getCountryName() != null)
                        revCountry = obj.getCountryName();

                    add = prepareLocation(revCity, revRegion, revCountry, location_setting);
                }
        } catch (IOException e) {
            e.printStackTrace();
            message = "Error during reverse geocoding. " + e.getMessage();
            add = null;
        }
        return add;
    }

    private String prepareLocation(String curCity, String curRegion, String curCountry, String userloc_setting){
        int loc_level = location_level(userloc_setting);
        String curLocation = "unavailable. City: " + curCity + ". Reg: " + curRegion + ". Coun: " + curCountry + ". Sets: " + loc_level;
        if ((curCity != null) && (location_level(userloc_setting) <= 0)){
            curLocation = curCity;
            if (curRegion != null){
                curLocation = curLocation + ", " + curRegion;
            }
            if (curCountry != null){
                curLocation = curLocation + ", " + curCountry;
            }
        }else if ((curRegion != null) && (location_level(userloc_setting) <= 1)){
            curLocation = curRegion;
            if (curCountry != null){
                curLocation = curLocation + ", " + curCountry;
            }
        }else if ((curCountry != null) && (location_level(userloc_setting) <= 2)){
            curLocation = curCountry;
        }
        return curLocation;      
    };

    private int location_level(String loc_level){
        int ret_level = 3;
        if (loc_level.equals("city"))
            ret_level = 0;
        if (loc_level.equals("region"))
            ret_level = 1;
        if (loc_level.equals("country"))
            ret_level = 2;
        
        return ret_level;
    }

    private void persistLocation(Location location) {
        //LocationDAO dao = DAOFactory.createLocationDAO(this.getApplicationContext());
        CardDAO cdao = DAOFactory.createCardDAO(this.getApplicationContext());
        //com.tenforwardconsulting.cordova.bgloc.data.Location savedLocation = com.tenforwardconsulting.cordova.bgloc.data.Location.fromAndroidLocation(location);
        //Store settings variables passed during the initial configuration of the service
        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences("lifesharePreferences", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = pref.edit();
        String user_id = "";
        String location_setting = "";
        String sharing_setting = "";
        try{
            user_id = params.getString("UserId");
            location_setting = params.getString("LocationSetting");
            sharing_setting = params.getString("SharingSetting");
                
            edit.putString("user_id", user_id);
            edit.putString("location_setting", location_setting);
            edit.putString("sharing_setting", sharing_setting);
            edit.commit();
        } catch (Throwable e) {
            Log.w(TAG, "Exception obtaining user Id: " + e);
            e.printStackTrace();
        }
        //Create the partial card for this location
        int cardId = cdao.getCardId();
        com.tenforwardconsulting.cordova.bgloc.data.Card savedCard = com.tenforwardconsulting.cordova.bgloc.data.Card.createCard(location, mContext, user_id, cardId);
        /*if (dao.persistLocation(savedLocation)) {
            Log.d(TAG, "Persisted Location: " + savedLocation);
        } else {
            Log.w(TAG, "Failed to persist location");
        }*/
        if (cdao.persistCard("pending_geo", savedCard)) {
            Log.d(TAG, "Persisted Card in pending_geo: " + savedCard);
        } else {
            Log.w(TAG, "Failed to persist card in pending_geo table");
        }
    }

    private boolean isNetworkConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            Log.d(TAG, "Network found, type = " + networkInfo.getTypeName());
            return networkInfo.isConnected();
        } else {
            Log.d(TAG, "No active network info");
            return false;
        }
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "------------------------------------------ Destroyed Location update Service");
        cleanUp();
        super.onDestroy();
    }
    private void cleanUp() {
        locationManager.removeUpdates(this);
        alarmManager.cancel(stationaryAlarmPI);
        alarmManager.cancel(stationaryLocationPollingPI);
        toneGenerator.release();
        CardDAO cdao = DAOFactory.createCardDAO(this.getApplicationContext());
        cdao.closeDB();
        
        unregisterReceiver(stationaryAlarmReceiver);
        unregisterReceiver(singleUpdateReceiver);
        unregisterReceiver(stationaryRegionReceiver);
        unregisterReceiver(stationaryLocationMonitorReceiver);
        unregisterReceiver(notificatinConfirmReceiver);
        unregisterReceiver(notificatinDiscardReceiver);
        
        if (stationaryLocation != null && !isMoving) {
            try {
                locationManager.removeProximityAlert(stationaryRegionPI);
            } catch (Throwable e) {
                Log.w(TAG, "- Something bad happened while removing proximity-alert");
            }
        }
        stopForeground(true);
        wakeLock.release();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        this.stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    private class PostLocationTask extends AsyncTask<Object, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Object...objects) {
            Log.d(TAG, "Executing PostLocationTask#doInBackground");
            Log.i(TAG, "1111 Post saved card");
            CardDAO cardDAO = DAOFactory.createCardDAO(LocationUpdateService.this.getApplicationContext());
            for (com.tenforwardconsulting.cordova.bgloc.data.Card savedGeoCard : cardDAO.geoPendingCards()) {
                Log.d(TAG, "Posting saved card");
                if (postCard(savedGeoCard)) {
                    cardDAO.deleteCard("pending_geo", savedGeoCard);
                }
            }
            Log.i(TAG, "9999 Post saved card");
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "PostLocationTask#onPostExecture");
        }
    }
}
