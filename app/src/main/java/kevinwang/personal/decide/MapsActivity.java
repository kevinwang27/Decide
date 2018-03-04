package kevinwang.personal.decide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MapsActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient gac;
    LocationRequest locationRequest;
    Location currentLocation;
    String[] eatTypes = {};
    String[] shopTypes = {};
    String[] playTypes = {};
    String[] relaxTypes = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!isEnabled()) {
            buildAlert();
        }
        setLocationRequest();
        gac = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        Intent intent = getIntent();
        int type = intent.getIntExtra(MainActivity.TYPE_OF_ACTIVITY, 0);
        int maxDistance = intent.getIntExtra(OptionsActivity.DISTANCE, 5);
        int maxPrice = intent.getIntExtra(OptionsActivity.PRICE, 1);
    }
    private void getNearbySearch(String url, int maxDistance, int maxPrice, String type) throws Exception{
        URL obj = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();
        connection.setRequestMethod("get");
        connection.addRequestProperty("location", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
        connection.addRequestProperty("radius", String.valueOf(maxDistance));
        connection.addRequestProperty("minprice", "0");
        connection.addRequestProperty("maxprice", String.valueOf(maxPrice));
        connection.addRequestProperty("type", type);
        connection.addRequestProperty("key", "123");
    }

    private boolean isEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service != null && (service.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || service.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void setLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void displayError() {
        TextView textView = (TextView) findViewById(R.id.location_error);
        textView.setVisibility(View.VISIBLE);
    }

    private void buildAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable")
                .setMessage("Please turn on location services to use this app")
                .setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    @Override
    public void onStart() {
        super.onStart();
        gac.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        gac.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(gac);
        if (lastLocation != null) {
            currentLocation = lastLocation;
            Log.d("loc", "" + currentLocation.getLongitude());
        } else {
            Log.d("loc", "null location");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("loc", "failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLocation = location;
            Log.d("loc", ""+currentLocation.getLongitude());
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0) {
                    try{
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                gac, locationRequest, this);
                    } catch (SecurityException e) {
                        Log.d("loc", "securityexception");
                    }
                } else {
                    displayError();
                }
            }
        }
    }
}
