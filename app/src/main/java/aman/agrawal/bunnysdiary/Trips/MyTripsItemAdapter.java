package aman.agrawal.bunnysdiary.Trips;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import aman.agrawal.bunnysdiary.R;

/**
 * Created by Dell-1 on 9/26/2018.
 */

public class MyTripsItemAdapter extends RecyclerView.Adapter<MyTripsItemAdapter.MyTripsItemViewHolder> {

    private Context mContext;
    private List<String> mImages;

    public MyTripsItemAdapter(Context mContext, List<String> mImages) {
        this.mContext = mContext;
        this.mImages = mImages;
    }

    @NonNull
    @Override
    public MyTripsItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.mytripsitem_recyclerview_item, null);
        return new MyTripsItemAdapter.MyTripsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTripsItemViewHolder holder, final int position) {

        String url = mImages.get(position);
        Picasso.with(mContext)
                .load(url)
                .fit()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.ivMyTripsImageItem);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class MyTripsItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMyTripsImageItem;

        public MyTripsItemViewHolder(View itemView) {
            super(itemView);

            ivMyTripsImageItem = (ImageView) itemView.findViewById(R.id.iv_myTripsItemImage);
        }
    }
}
