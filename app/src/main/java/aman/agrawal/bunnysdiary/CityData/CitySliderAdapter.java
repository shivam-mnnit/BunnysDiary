package aman.agrawal.bunnysdiary.CityData;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import aman.agrawal.bunnysdiary.R;

/**
 * Created by Dell-1 on 9/23/2018.
 */

public class CitySliderAdapter extends PagerAdapter {

    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context context;

    public CitySliderAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.layout_city_slider, view, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.iv_citySlider);

        String url = images.get(position);
        Picasso.with(context).load(url)
                .fit()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(myImage);

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
