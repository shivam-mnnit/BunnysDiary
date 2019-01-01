package aman.agrawal.bunnysdiary.CityData;

/**
 * Created by Dell-1 on 9/21/2018.
 */

import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GetNearbyPlaces extends AsyncTask<Object,String,String>{

    String url;
    InputStream is;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    String data;
    CityActivity cityActivity ;
    List<PlacesData> placesData;
    int addressType;

    public GetNearbyPlaces(CityActivity cityActivity,int addressType) {
        this.cityActivity = cityActivity ;
        this.addressType = addressType;
    }

    @Override
    protected String doInBackground(Object... params) {

        url=(String)params[0];
        try{
            URL myurl = new URL(url);
            HttpURLConnection httpURLConnection= (HttpsURLConnection) myurl.openConnection();
            httpURLConnection.connect();
            is = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line="";
            stringBuilder= new StringBuilder();
            while((line=bufferedReader.readLine())!=null)
            {
                stringBuilder.append(line);

            }
            data=stringBuilder.toString();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }


    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject parentObject = new JSONObject(s);
            JSONArray resultArray = parentObject.getJSONArray("results");

                placesData = new ArrayList<>();

                for(int i=0;i<resultArray.length();i++)
                {
                    JSONObject jsonObject = resultArray.getJSONObject(i);
                    JSONObject locationObj = jsonObject.getJSONObject("geometry").getJSONObject("location");
                    String placeLatitude = locationObj.getString("lat");
                    String placeLongitude = locationObj.getString("lng");

                    String placeName = jsonObject.getString("name");
                    String placeIcon = jsonObject.getString("icon");
                    String placeRating = jsonObject.getString("rating");
                    String placeAddress;

                    if(addressType==0)
                        placeAddress = jsonObject.getString("vicinity");
                    else
                        placeAddress = jsonObject.getString("formatted_address");


                    placesData.add(
                            new PlacesData(
                                    placeName,placeIcon,placeAddress,Double.parseDouble(placeLatitude),Double.parseDouble(placeLongitude)
                                    ,Double.parseDouble(placeRating))
                    );

                }

                Intent showList = new Intent(cityActivity,ShowPlacesList.class);
                showList.putExtra("placesList", (Serializable) placesData);
                cityActivity.startActivity(showList);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

