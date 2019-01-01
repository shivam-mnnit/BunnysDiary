package aman.agrawal.bunnysdiary.CityData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import aman.agrawal.bunnysdiary.MapsActivity;
import aman.agrawal.bunnysdiary.R;

/**
 * Created by Dell-1 on 9/22/2018.
 */

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder> {

    private Context mCtx;

    //we are storing all the products in a list
    private List<PlacesData> placesList;

    //getting the context and product list with constructor
    public PlacesListAdapter(Context mCtx, List<PlacesData> placesList) {
        this.mCtx = mCtx;
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public PlacesListAdapter.PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recyclerview_item_layout, null);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesListAdapter.PlacesViewHolder holder, int position) {
        final PlacesData placesData = placesList.get(position);

        //binding the data with the viewholder views
        holder.tvPlaceName.setText(placesData.getPlaceName());
        holder.tvPlaceVicinity.setText(placesData.getPlaceVicinity());
        holder.tvPlaceRating.setText(String.valueOf(placesData.getPlaceRating()));

        String placeIcon = placesData.getPlaceIcon();
        Glide.with(mCtx).load(placeIcon).into(holder.ivPlaceIcon);

        holder.btnShowPlaceOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent showOnMap = new Intent(mCtx,MapsActivity.class);
                showOnMap.putExtra("UniqueID","FromShowPlacesList");
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",placesData.getPlaceLatitude());
                bundle.putDouble("longitude",placesData.getPlaceLongitude());
                bundle.putString("placeName",placesData.getPlaceName());
                showOnMap.putExtras(bundle);
                mCtx.startActivity(showOnMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {

        TextView tvPlaceName,tvPlaceVicinity,tvPlaceRating;
        ImageView ivPlaceIcon;
        Button btnShowPlaceOnMap;

        public PlacesViewHolder(View itemView) {
            super(itemView);

            tvPlaceName = itemView.findViewById(R.id.tv_placeName);
            tvPlaceVicinity = itemView.findViewById(R.id.tv_placeVicinity);
            tvPlaceRating = itemView.findViewById(R.id.tv_placeRating);
            ivPlaceIcon = itemView.findViewById(R.id.iv_placeIcon);
            btnShowPlaceOnMap = itemView.findViewById(R.id.btn_showPlaceOnMap);
        }
    }
}
