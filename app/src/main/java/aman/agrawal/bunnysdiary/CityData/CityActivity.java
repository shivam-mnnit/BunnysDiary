package aman.agrawal.bunnysdiary.CityData;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import aman.agrawal.bunnysdiary.MapsActivity;
import aman.agrawal.bunnysdiary.R;
import aman.agrawal.bunnysdiary.Trips.BucketListContent;
import aman.agrawal.bunnysdiary.Trips.PlanATrip;
import aman.agrawal.bunnysdiary.Extras.WeatherFetchFuction;

public class CityActivity extends AppCompatActivity {

    private Button btnShowOnMap,btnExplore,btnRestaurant,btnDetails,btnBookings,btnMakePlan,btnBookmark;
    private String cityToSearch;
    private TextView tvCity,tvWeatherIcon,tvTemperature,tvCondition,tvHumidity;
    private LatLng latLongCurrent;
    private ImageShow imageShow;
    private Typeface weatherFont;
    private String photoUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        btnShowOnMap = (Button) findViewById(R.id.btn_showOnMap);
        btnExplore = (Button) findViewById(R.id.btn_explore);
        btnRestaurant = (Button) findViewById(R.id.btn_restaurant);
        btnDetails = (Button) findViewById(R.id.btn_details);
        btnBookings = (Button) findViewById(R.id.btn_booking);
        btnMakePlan = (Button) findViewById(R.id.btn_makePlan);
        btnBookmark = (Button) findViewById(R.id.btn_bookmark);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvWeatherIcon = (TextView) findViewById(R.id.tv_weatherIcon);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        tvHumidity = (TextView) findViewById(R.id.tv_humidity);
        tvCondition = (TextView) findViewById(R.id.tv_condition);

        Intent intent = getIntent();
        if (intent.getIntExtra("Current", 0) == 1) {
            String[] tokens = intent.getStringExtra("cityToSearch").split(",");
            tvCity.setText(tokens[5].toString().trim().toUpperCase());
        } else {
            cityToSearch = intent.getStringExtra("cityToSearch");
            tvCity.setText(cityToSearch.toString().trim().toUpperCase());
        }
        cityToSearch = intent.getStringExtra("cityToSearch");

        btnBookmark.setEnabled(false);

        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        tvWeatherIcon.setTypeface(weatherFont);
        weatherInit();

        MapsActivity mapsActivity = new MapsActivity();

        latLongCurrent = mapsActivity.getCityLatitude(CityActivity.this, cityToSearch);
        showImage();

        btnShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showOnMap = new Intent(CityActivity.this, MapsActivity.class);
                showOnMap.putExtra("UniqueID", "FromCityActivity");
                showOnMap.putExtra("showCityOnMap", cityToSearch);
                startActivity(showOnMap);
            }
        });

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?query=");
                stringBuilder.append(cityToSearch.toLowerCase().trim() + "+point+of+interest&language=en&");
                stringBuilder.append("&radius=" + 30000);
                stringBuilder.append("&key=" + getResources().getString(R.string.google_place_key));
                String url = stringBuilder.toString();
                getPlaceDetails(url, 1);
            }
        });

        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + latLongCurrent.latitude + "," + latLongCurrent.longitude);
                stringBuilder.append("&radius=" + 50000);
                stringBuilder.append("&type=restaurant");
                stringBuilder.append("&key=" + getResources().getString(R.string.google_place_key));
                String url = stringBuilder.toString();
                getPlaceDetails(url, 0);
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://en.wikipedia.org/wiki/" + cityToSearch));
                startActivity(viewIntent);
            }
        });

        btnBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CityActivity.this);
                builder.setTitle("Choose a Booking");

                String[] booking = {"Hotel Booking", "Train Booking", "Flight Booking"};
                builder.setItems(booking, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent viewIntent = null;
                        switch (which) {
                            case 0:
                                viewIntent = new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://www.goibibo.com/hotels/"));
                                break;
                            case 1:
                                viewIntent = new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://www.irctc.co.in/nget/train-search"));
                                break;
                            case 2:
                                viewIntent = new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://www.makemytrip.com/flights/"));
                                break;
                        }
                        startActivity(viewIntent);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        btnMakePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent planAtrip = new Intent(CityActivity.this,PlanATrip.class);
                planAtrip.putExtra("Fixed",1);
                planAtrip.putExtra("Destination",cityToSearch);
                startActivity(planAtrip);
            }
        });

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ImageShow imageShow = new ImageShow(CityActivity.this);
                BucketListContent bucketListContent = new BucketListContent(cityToSearch,photoUrl);

                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(currentUser)
                                                                        .child("BucketList").child(cityToSearch);
                databaseReference.setValue(bucketListContent).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CityActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar snackbar = Snackbar.make(view, " City Added to BucketList", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(CityActivity.this, R.color.colorSnackbar));
                        snackbar.show();
                    }
                });



            }
        });
    }

    private void weatherInit() {
        if (WeatherFetchFuction.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(tvCity.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void getPlaceDetails(String url,int addressType) {
        try {
            Object dataTransfer[] = new Object[1];
            dataTransfer[0] = url;
            GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(CityActivity.this, addressType);
            getNearbyPlaces.execute(dataTransfer);
        }catch(Exception e){
            Toast.makeText(this, "Check your Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void showImage(){
        try {
            StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?query=");
            stringBuilder.append(cityToSearch.toLowerCase().trim() + "+point+of+interest&language=en&");
            stringBuilder.append("&radius=" + 30000);
            stringBuilder.append("&key=" + getResources().getString(R.string.google_place_key));
            String url = stringBuilder.toString();
            Object dataTransfer[] = new Object[1];
            dataTransfer[0] = url;
            imageShow = new ImageShow(this);
            imageShow.execute(dataTransfer);
        }catch(Exception e){
            Toast.makeText(this, "Check your Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String...args) {
            String xml = WeatherFetchFuction.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + getResources().getString(R.string.open_weather_map_key));
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    tvCondition.setText(details.getString("description").toUpperCase(Locale.UK));
                    tvTemperature.setText(String.format("%.2f", main.getDouble("temp")) + " °C");
                    tvHumidity.setText("Humidity: " + main.getString("humidity") + "%");
                    tvWeatherIcon.setText(Html.fromHtml(WeatherFetchFuction.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
