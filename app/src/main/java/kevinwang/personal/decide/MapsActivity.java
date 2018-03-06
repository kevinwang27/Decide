package kevinwang.personal.decide;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.LinkedList;

public class MapsActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient gac;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private ProgressBar spinner;
    private TextView resultText;
    private TextView resultAddressText;
    private MapFragment mapFragment;
    private GoogleMap map;
    private Marker marker;
    private double lat, lng;

    private String placeID;
    private String placeName;
    private String placeAddress;
    private boolean searched;

    private LinkedList<String> eatTypes;
    private LinkedList<String> shopTypes;
    private LinkedList<String> playTypes;
    private LinkedList<String> relaxTypes;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        resultText = (TextView) findViewById(R.id.result);
        resultAddressText = (TextView) findViewById(R.id.result_address);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (savedInstanceState != null) {
                    placeName = savedInstanceState.getString("place");
                    placeAddress = savedInstanceState.getString("address");
                    placeID = savedInstanceState.getString("id");
                    lat = savedInstanceState.getDouble("markerLat");
                    lng = savedInstanceState.getDouble("markerLng");
                    resultText.setText(placeName);
                    resultAddressText.setText(placeAddress);
                    spinner.setVisibility(View.GONE);
                    LatLng point = new LatLng(lat, lng);
                    marker = map.addMarker(new MarkerOptions().position(point).title(savedInstanceState.getString("place")));
                }
            }
        });
        if (savedInstanceState == null) {
            searched = false;
        } else {
            searched = true;
        }
        initLists();

        if (!locationIsEnabled()) {
            buildLocationAlert();
        }
        buildLocationRequest();
        gac = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    /*
     * Makes a location request
     */
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*
     * Initializes all linked lists of location types
     */
    private void initLists() {
        eatTypes = new LinkedList<>();
        eatTypes.add("restaurant");
        eatTypes.add("cafe");
        eatTypes.add("bakery");

        shopTypes = new LinkedList<>();
        shopTypes.add("clothing_store");
        shopTypes.add("book_store");
        shopTypes.add("department_store");
        shopTypes.add("shopping_mall");
        shopTypes.add("electronics_store");

        playTypes = new LinkedList<>();
        playTypes.add("bowling_alley");
        playTypes.add("movie_theater");
        playTypes.add("zoo");
        playTypes.add("museum");

        relaxTypes = new LinkedList<>();
        relaxTypes.add("spa");
        relaxTypes.add("park");
        relaxTypes.add("beauty_salon");
    }

    /*
     * Helper method for testing whether location is enabled
     */
    private boolean locationIsEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service != null && (service.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || service.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    /*
     * Builds an alert for when location is off
     */
    private void buildLocationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable")
                .setMessage("Please turn on location services and allow Decide access in order to use this app")
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

    /*
     * Builds an alert for when selection criteria are too strict
     */
    private void buildTryAgain() {
        spinner.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Results")
                .setMessage("Please expand your selections and try again")
                .setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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

    /*
     * Once connected, update location and make API call
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(gac);
        if (lastLocation != null) {
            currentLocation = lastLocation;
        } else {
            Log.d("loc", "null location");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
        if (!searched) {
            makeAPIRequest();
        }
    }

    /*
     * Reads data from the intent and calls HTTPRequestTask to make the API call
     */
    private void makeAPIRequest() {
        Intent intent = getIntent();
        final LinkedList<String> typeArray = chooseTypeArray(intent.getIntExtra(MainActivity.TYPE_OF_ACTIVITY, 0));
        String type = typeArray.remove((int) (Math.random() * typeArray.size()));
        int maxDistance = intent.getIntExtra(OptionsActivity.DISTANCE, 5);

        new HTTPRequestTask(new HTTPRequestTask.AsyncResponse() {
            @Override
            public void processFinished(String response) {
                processJSON(response, typeArray);
            }
        }).execute(new Data(this, type, maxDistance));
    }

    /*
     * Receives string from API call and extracts name and id of a random place within the JSON object
     */
    private void processJSON(String res, LinkedList<String> typeArray) {
        try {
            JSONObject obj = new JSONObject(res);
            if (obj.getString("status").equals("ZERO_RESULTS")) {
                if (!typeArray.isEmpty()) {
                    makeAPIRequest();
                } else {
                    buildTryAgain();
                }
                return;
            } else if (!obj.getString("status").equals("OK")) {
                buildTryAgain();
                return;
            }

            JSONArray results = obj.getJSONArray("results");
            int index = (int) (Math.random() * results.length());
            if (results.length() > 1 && placeID != null) {
                while (placeID.equals(results.getJSONObject(index).getString("place_id"))) {
                    index = (int) (Math.random() * results.length());
                }
            }
            JSONObject place = results.getJSONObject(index);
            placeName = place.getString("name");
            placeID = place.getString("place_id");
            placeAddress = place.getString("vicinity");
            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
            spinner.setVisibility(View.INVISIBLE);

            lat = location.getDouble("lat");
            lng = location.getDouble("lng");
            setMap(new LatLng(lat, lng));
            resultAddressText.setText(placeAddress);
            resultText.setText(placeName);

            searched = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * Customizes the map
     */
    private void setMap(final LatLng result) {
        marker = map.addMarker(new MarkerOptions().position(result)
                .title(placeName));
        map.getUiSettings().setMapToolbarEnabled(false);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(result, 15));

    }

    /*
     * Helper method for returning the correct array of types
     */
    private LinkedList<String> chooseTypeArray(int choice) {
        switch (choice) {
            case MainActivity.EAT:
                return eatTypes;
            case MainActivity.PLAY:
                return playTypes;
            case MainActivity.SHOP:
                return shopTypes;
            case MainActivity.RELAX:
                return relaxTypes;
            default:
                return eatTypes;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildLocationAlert();
    }

    /*
     * Updates location when it changes (in case user is moving and wants another suggestion)
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLocation = location;
        }
    }

    /*
     * Reads whether user gave proper permissions or not
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0) {
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                gac, locationRequest, this);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    buildLocationAlert();
                }
            }
        }
    }

    /*
     * Re-initializes then makes another API call
     */
    public void tryAgain(View view) {
        initLists();
        searched = false;
        makeAPIRequest();
        resultText.setText("");
        resultAddressText.setText("");
        spinner.setVisibility(View.VISIBLE);
        marker.remove();
    }

    /*
     * Launches google maps with the suggested location
     */
    public void go(View view) {
        String url = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(placeName) +
                "&query_place+id=" + URLEncoder.encode(placeID);
        openWebPage(url);
    }

    /*
     * Opens a given web page
     */
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /*
     * Saves necessary data for saving the state of the app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("place", placeName);
        outState.putString("address", placeAddress);
        outState.putString("id", placeID);
        outState.putDouble("markerLat", lat);
        outState.putDouble("markerLng", lng);
        super.onSaveInstanceState(outState);
    }

    /*
     * Stores data to pass to HTTPRequestTask
     */
    class Data {
        private Context context;
        private String type;
        private int maxDistance;
        private Location location;

        Data(Context context, String type, int maxDistance) {
            this.context = context;
            this.type = type;
            this.maxDistance = maxDistance;
            this.location = currentLocation;
        }

        int getMaxDistance() {
            return this.maxDistance;
        }

        double getLatitude() {
            return this.location.getLatitude();
        }

        double getLongitude() {
            return this.location.getLongitude();
        }

        String getType() {
            return this.type;
        }

        Context getContext() {
            return this.context;
        }
    }
}
