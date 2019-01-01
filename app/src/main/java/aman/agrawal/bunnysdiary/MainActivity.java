package aman.agrawal.bunnysdiary;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aman.agrawal.bunnysdiary.CityData.CityActivity;
import aman.agrawal.bunnysdiary.Extras.DevelopersActivity;
import aman.agrawal.bunnysdiary.Extras.UserProfile;
import aman.agrawal.bunnysdiary.Trips.BucketList;
import aman.agrawal.bunnysdiary.Trips.MyTrips;
import aman.agrawal.bunnysdiary.Trips.PlanATrip;
import aman.agrawal.bunnysdiary.Trips.UpcomingTrips;
import aman.agrawal.bunnysdiary.Utilities.CheckListActivity;
import aman.agrawal.bunnysdiary.Utilities.CurrencyConverterActivity;
import aman.agrawal.bunnysdiary.Utilities.NotesActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_CODE = 1234;
    private FirebaseUser user;
    private GridLayout mainGrid;
    private Button btnCurrentLocation;
    private ImageView ivDelhi,ivMumbai,ivKolkata,ivBengaluru,ivAhmedabad,ivChennai,userProfilePic;
    private TextView userName;
    private double latitude, longitude;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private AutoCompleteTextView mAutocompleteTextView;
    private LocationManager locationManager;
    private String provider;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW =
            new LatLngBounds(new LatLng(20.5937, 55.87777), new LatLng(28.7041, 78.9629));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        //check if user has granted location permission to the app
        checkLocationPermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //check if internet connection is available
        if(!haveNetworkConnection())
        {
            //if not ask user to turn on internet connection
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Turn on Internet?");
            builder1.setCancelable(false
            );

            builder1.setPositiveButton(
                    "Connect to Internet",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //if user agrees then goto internet settings of the device
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });

            builder1.setNegativeButton(
                    "Deny",
                    new DialogInterface.OnClickListener() {
                        //else close the app
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }


        //Building GoogleApiClient for autosuggestion of places and searching user's current location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        //Setting up autosuggestion search bar and corresponding adapter(PlacesArrayAdapter)
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.et_searchCity);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW,null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        mainGrid = (GridLayout) findViewById(R.id.main_grid);
        btnCurrentLocation = (Button) findViewById(R.id.btn_currentLocation);

        //setting imageViews by compressing images for the popular cities list
        ivDelhi = (ImageView)findViewById(R.id.img1);
        compressAndSetImage(ivDelhi,getResources().getDrawable(R.drawable.delhi));
        ivMumbai = (ImageView)findViewById(R.id.img2);
        compressAndSetImage(ivMumbai,getResources().getDrawable(R.drawable.mumbai));
        ivKolkata = (ImageView)findViewById(R.id.img3);
        compressAndSetImage(ivKolkata,getResources().getDrawable(R.drawable.kolkata));
        ivBengaluru = (ImageView)findViewById(R.id.img4);
        compressAndSetImage(ivBengaluru,getResources().getDrawable(R.drawable.bangalore));
        ivAhmedabad = (ImageView)findViewById(R.id.img5);
        compressAndSetImage(ivAhmedabad,getResources().getDrawable(R.drawable.ahmedabad));
        ivChennai = (ImageView)findViewById(R.id.img6);
        compressAndSetImage(ivChennai,getResources().getDrawable(R.drawable.chennai));

        //Fab for voice search
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.voiceSearch);
        fab.setImageResource(R.drawable.ic_keyboard_voice_black_24dp);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            fab.setEnabled(false);
        }
        mAutocompleteTextView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity(); //this function  will start voice search action
            }
        });


        //Recommend places nearby according to user's current location
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                //get user's location(latitude and longitude) using previously set GoogleApiClient
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    longitude = mLastLocation.getLongitude();
                    latitude = mLastLocation.getLatitude();

                    //Getting city name from latitude and longitude using Geocoder
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String cityName = addresses.get(0).getAddressLine(0);

                    //calling the cityActivity which shows all data related to a city
                    Intent searchCity = new Intent(MainActivity.this,CityActivity.class);
                    searchCity.putExtra("Current",1);
                    searchCity.putExtra("cityToSearch",cityName);
                    startActivity(searchCity);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Can't Access your Location now! Please check your internet connection" +
                                    "or GPS settings.",Toast.LENGTH_LONG).show();
                }
            } catch (SecurityException e) {}

            }

        });

        //setting onClick listener for the gridLayout item which contains the list of popular cities
        setSingleEvent(mainGrid);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setting user's profile pic and name in navigation drawer header
        user = FirebaseAuth.getInstance().getCurrentUser();
        View navHeaderView =  navigationView.getHeaderView(0);
        userProfilePic = (ImageView) navHeaderView.findViewById(R.id.iv_userPic);
        userName = (TextView) navHeaderView.findViewById(R.id.tv_userName);

        Picasso.with(this).load(user.getPhotoUrl()).fit().centerCrop().placeholder(R.drawable.profilepicplaceholder)
                .error(R.drawable.profilepicplaceholder)
                .into(userProfilePic);
        userName.setText(user.getDisplayName());
    }

    //function for compressing and setting images in gridLayout item
    private void compressAndSetImage(ImageView iv, Drawable drawable) {
        final Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        iv.setImageBitmap(compressedBitmap);
    }

    //function to set onClick listener for each gridLayout item
    private void setSingleEvent(GridLayout mainGrid) {
        CardView cardViewDelhi = (CardView)mainGrid.getChildAt(0);
        CardView cardViewMumbai = (CardView)mainGrid.getChildAt(1);
        CardView cardViewKolkata = (CardView)mainGrid.getChildAt(2);
        CardView cardViewBengaluru = (CardView)mainGrid.getChildAt(3);
        CardView cardViewAhmedabad = (CardView)mainGrid.getChildAt(4);
        CardView cardViewChennai = (CardView)mainGrid.getChildAt(5);

        cardViewDelhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callIntent("cityToSearch","Delhi");
            }
        });
        cardViewMumbai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent("cityToSearch","Mumbai");
            }
        });
        cardViewKolkata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent("cityToSearch","Kolkata");
            }
        });
        cardViewBengaluru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent("cityToSearch","Bengaluru");
            }
        });
        cardViewAhmedabad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent("cityToSearch","Ahmedabad");
            }
        });
        cardViewChennai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent("cityToSearch","Chennai");
            }
        });
    }

    //calling cityActivity on gridLayout item click
    private void callIntent(String id,String city){
        Intent cityIntent = new Intent(MainActivity.this,CityActivity.class);
        cityIntent.putExtra(id,city);
        startActivity(cityIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Intent exit = new Intent(Intent.ACTION_MAIN);
            exit.addCategory(Intent.CATEGORY_HOME);
            exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exit);
            finish();
        }
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //OnClick for menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {

            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Bunny's Diary");
                String sAux = "\nLet me recommend you this Amazing app\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {

            }
        }
        else if(id == R.id.action_profile){
            Intent userProfile = new Intent(MainActivity.this,UserProfile.class);
            startActivity(userProfile);
        }

        return super.onOptionsItemSelected(item);
    }

    //OnClick for navigation drawer item selected
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_developers) {

            Intent developers = new Intent(MainActivity.this,DevelopersActivity.class);
            startActivity(developers);

        } else if (id == R.id.nav_myTrips) {

            Intent myTrips = new Intent(MainActivity.this,MyTrips.class);
            startActivity(myTrips);


        } else if (id == R.id.nav_bucketList) {

            Intent bucketList = new Intent(MainActivity.this,BucketList.class);
            startActivity(bucketList);

        } else if (id == R.id.nav_location) {

            Intent showLocation = new Intent(MainActivity.this,MapsActivity.class);
            showLocation.putExtra("UniqueID","FromMainActivity");
            startActivity(showLocation);

        } else  if(id == R.id.nav_planATrip){

            Intent planATrip = new Intent(MainActivity.this,PlanATrip.class);
            planATrip.putExtra("Fixed",0);
            startActivity(planATrip);

        }else  if(id == R.id.nav_upcomingTrips){

            Intent upcomingTrips = new Intent(MainActivity.this,UpcomingTrips.class);
            startActivity(upcomingTrips);

        }
        else if (id == R.id.nav_checkList) {

            Intent checklist = new Intent(MainActivity.this,CheckListActivity.class);
            startActivity(checklist);

        } else if (id == R.id.nav_currency) {

            Intent currency = new Intent(MainActivity.this,CurrencyConverterActivity.class);
            startActivity(currency);

        } else if (id == R.id.nav_notes) {

            Intent checklist = new Intent(MainActivity.this,NotesActivity.class);
            startActivity(checklist);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }
    }

    //on GoogleApiClient Connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                longitude = mLastLocation.getLongitude();   //get user current longitude
                latitude = mLastLocation.getLatitude();     //get user current latitude
            }
        } catch (SecurityException e) {}

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Places API connection failed with error code:" + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    //on clicking on any item of suggested list of cities, redirect to cityActivity(and show data of selected cities)
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item);
            String[] name=placeId.split(",");
            Intent searchCity = new Intent(MainActivity.this,CityActivity.class);
            searchCity.putExtra("cityToSearch",name[0]);
            startActivity(searchCity);
        }
    };

    //check user's GPS setting
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    //check location permission state
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        }
        else {
            displayLocationSettingsRequest(this);
            return true;
        }
    }

    //if requesting for location permission, proceed according to user response
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    displayLocationSettingsRequest(this);

                } else {

                    // permission denied

                }
                return;
            }

        }
    }

    //voice search function
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    //voice search result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList< String > matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty())
            {
                String Query = matches.get(0);
                mAutocompleteTextView.setText(Query);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //function to check internet connection
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
