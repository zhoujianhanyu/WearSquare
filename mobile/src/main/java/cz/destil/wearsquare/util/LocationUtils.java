package cz.destil.wearsquare.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import cz.destil.wearsquare.core.App;

/**
 * Location-related utils.
 *
 * @author David Vávra (david@vavra.me)
 */
public class LocationUtils {

    private static final int MAX_LOCATION_AGE_MINUTES = 2;

    public static void getLastLocation(final LocationListener listener) {
        // return "51.497470, -0.135633";//"40.765068,-73.983172"; // FAKE FOR screenshots
        final LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double locationAgeMinutes = ((double) (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos())) / 60000000000L;
        if (location == null || locationAgeMinutes > MAX_LOCATION_AGE_MINUTES) {
            IntentFilter locIntentFilter = new IntentFilter("SINGLE_LOCATION_UPDATE_ACTION");
            Intent updateIntent = new Intent("SINGLE_LOCATION_UPDATE_ACTION");
            final PendingIntent singleUpdatePI = PendingIntent.getBroadcast(App.get(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            BroadcastReceiver singleUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    String key = LocationManager.KEY_LOCATION_CHANGED;
                    Location location = (Location) intent.getExtras().get(key);
                    listener.onLocationUpdate(location.getLatitude() + "," + location.getLongitude());
                    locationManager.removeUpdates(singleUpdatePI);
                }
            };
            App.get().registerReceiver(singleUpdateReceiver, locIntentFilter);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
            locationManager.requestSingleUpdate(criteria, singleUpdatePI);
        } else {
            listener.onLocationUpdate(location.getLatitude() + "," + location.getLongitude());
        }
    }


    public static int getLastAccuracy() {
        LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        return (int) location.getAccuracy();
    }

    public static int getLastAltitude() {
        LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        return (int) location.getAltitude();
    }

    public interface LocationListener {
        public void onLocationUpdate(String location);
    }
}
