package aman.agrawal.bunnysdiary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest request;
    private LatLng latLongCurrent;
    private String city,place;
    private int intentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if(intent!=null)
        {
            switch (intent.getStringExtra("UniqueID")){
                case "FromCityActivity":
                    city = intent.getStringExtra("showCityOnMap");
                    intentID = 0;
                    place = intent.getStringExtra("showPlacesOnMap");
                    break;

                case "FromMainActivity":
                    intentID = 1;
                    break;

                case "FromShowPlacesList":
                    Bundle bundle = intent.getExtras();
                    latLongCurrent = new LatLng(bundle.getDouble("latitude"),bundle.getDouble("longitude"));
                    place = bundle.getString("placeName");
                    intentID = 2;
                    break;
            }
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        switch (intentID){
            case 0:
                latLongCurrent = getCityLatitude(this, city);
                if (latLongCurrent == null) {
                    Toast.makeText(this, "Enter city name Correctly",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLongCurrent, 13);
                    mMap.animateCamera(update);
                    MarkerOptions options = new MarkerOptions();
                    options.position(latLongCurrent);
                    options.title(city);
                    mMap.addMarker(options);
                }
                break;

            case 1:
                buildGoogleApiClient();
                break;

            case 2:
                if (latLongCurrent == null) {
                    Toast.makeText(this, "Can't Locate the place",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLongCurrent, 13);
                    mMap.animateCamera(update);
                    MarkerOptions options = new MarkerOptions();
                    options.position(latLongCurrent);
                    options.title(place);
                    mMap.addMarker(options);
                }
        }

    }

    public LatLng getCityLatitude(Context context, String city){

            if (Geocoder.isPresent()) {
                Geocoder geocoder = new Geocoder(context, context.getResources().getConfiguration().locale);
                List<Address> addresses = null;
                LatLng latLng = null;
                try {
                    addresses = geocoder.getFromLocationName(city, 1);
                    Address address = addresses.get(0);
                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return latLng;
            }

        return null;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null)
        {
            Toast.makeText(getApplicationContext(),"Location not found.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            latLongCurrent = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLongCurrent,15);
            mMap.animateCamera(update);
            MarkerOptions options = new MarkerOptions();
            options.position(latLongCurrent);
            options.title(city);
            mMap.addMarker(options);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
