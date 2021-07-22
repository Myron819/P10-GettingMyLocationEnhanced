package sg.edu.rp.webservices.p09_gettingmylocation;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class LocationService extends Service {

    LocationRequest mLocationRequest = LocationRequest.create();
    LocationCallback mLocationCallback;
    FusedLocationProviderClient client;
//    boolean started;

    public LocationService() {
    }


    @Override
    public void onCreate() {
        Log.d("MyService", "Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!started) {
//            started = true;
        Log.d("MyService", "Service started");

        // Step 1: Create a client to connect to Google Play Location Services
        client = LocationServices.getFusedLocationProviderClient(LocationService.this);

        // Step 5: Define a set of actions to be carried out whenever a new location is reported from the Google Play Location Services,
        //          by creating a LocationCallback to be associated with the update event.
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();

//                  tvLat.setText("Latitude : " + lat);
//                  tvLong.setText("Latitude : " + lng);

                    Toast.makeText(getApplicationContext(), lat + ", " + lng, Toast.LENGTH_SHORT).show();

                    // Folder creation
                    String folderLocation_I = getFilesDir().getAbsolutePath() + "/Folder";
                    File folder = new File(folderLocation_I);
                    if (folder.exists() == false) {
                        boolean result = folder.mkdir();
                        if (result == true) {
                            Log.d("File Read/Write", "Folder created");
                        }
                    }

                    // File creation and writing
                    try {
                        File targetFile_I = new File(folderLocation_I, "data.txt");
                        FileWriter writer_I = new FileWriter(targetFile_I, true);
                        writer_I.write(lat + ", " + lng + "\n");
                        writer_I.flush();
                        writer_I.close();
                    } catch (Exception e) {
                        Toast.makeText(LocationService.this, "Failed to write!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            }
        };

        // Step 3a: Perform runtime check of the required permissions (see checkPermission() method at bottom).
        if (checkPermission()) {
            // Step 4: define the criteria for a location update
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Accuracy of the provider

//              // PS Solution values
//              mLocationRequest.setInterval(30 * 1000);
//              mLocationRequest.setFastestInterval(5 * 1000);
//              mLocationRequest.setSmallestDisplacement(500);

            // Testing values
            mLocationRequest.setInterval(5 * 1000); // (milliseconds) Set the interval in which you want to get locations.
            mLocationRequest.setFastestInterval(5 * 1000); // (milliseconds) If a location is available sooner you can get it (i.e. another app is using the location services).
            mLocationRequest.setSmallestDisplacement(0); // (metres) Minimum geographical distance from last reported location

            // Step 6: Start the location updates using the FusedLocationProviderClient from step 1.
            client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }

//        } else {
//            Log.d("MyService", "Service is still running");
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy () {
        Log.d("MyService", "Service exited");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Step 3b
    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}