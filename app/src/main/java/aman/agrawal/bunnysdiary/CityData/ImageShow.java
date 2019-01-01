package aman.agrawal.bunnysdiary.CityData;

/**
 * Created by Dell-1 on 9/21/2018.
 */

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import aman.agrawal.bunnysdiary.R;
import me.relex.circleindicator.CircleIndicator;

import static java.lang.Math.min;

public class ImageShow extends AsyncTask<Object,String,String> {

    private String url,data;
    private String ref,photoUrl;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    CityActivity cityActivity ;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<String> imageArray = new ArrayList<String>();

    public ImageShow(CityActivity cityActivity) {
        this.cityActivity = cityActivity ;
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

        } catch (MalformedURLException e)
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

            int m = min(resultArray.length(),5);
            for (int i=0;i<m;i++){
                JSONObject jsonObject=resultArray.getJSONObject(i);
                JSONArray photo = jsonObject.getJSONArray("photos");
                JSONObject img_ref = photo.getJSONObject(0);
                ref = img_ref.getString("photo_reference");

                photoUrl = "https://maps.googleapis.com/maps/api/place/photo?photoreference="+
                        ref+"&maxwidth=400"+"&key=" +cityActivity.getResources().getString(R.string.google_place_key);

                imageArray.add(photoUrl);
            }

            Button btnBookmark = (Button)cityActivity.findViewById(R.id.btn_bookmark);
            cityActivity.setPhotoUrl(imageArray.get(0));
            btnBookmark.setEnabled(true);
            imageInit();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void imageInit() {

        mPager = (ViewPager) cityActivity.findViewById(R.id.pager);
        mPager.setAdapter(new CitySliderAdapter(cityActivity,imageArray));
        CircleIndicator indicator = (CircleIndicator) cityActivity.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        //Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == (imageArray.size())) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 4000, 4000);
    }

}

