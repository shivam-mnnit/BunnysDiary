package aman.agrawal.bunnysdiary.Trips;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import aman.agrawal.bunnysdiary.R;

/**
 * Created by Dell-1 on 9/25/2018.
 */

class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder> {

    private List<String> mCityNames = new ArrayList<>();
    private Context mContext;
    private Bitmap mBitmap;
    private OnItemClickListener mListener;
    private List<BucketListContent> bucketListContentList;

    public StaggeredRecyclerViewAdapter(List<String> mCityNames, Context mContext, Bitmap mBitmap) {
        this.mCityNames = mCityNames;
        this.mContext = mContext;
        this.mBitmap = mBitmap;
    }

    public StaggeredRecyclerViewAdapter(List<BucketListContent> bucketListContentList,Context mContext){
        this.bucketListContentList = bucketListContentList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public StaggeredRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull StaggeredRecyclerViewAdapter.ViewHolder holder, final int position) {

        if(mContext instanceof MyTrips || mContext instanceof UpcomingTrips){
            holder.name.setText(mCityNames.get(position));
            holder.image.setImageBitmap(mBitmap);
        }
        else{
            holder.name.setText(bucketListContentList.get(position).getCityName());
            Picasso.with(mContext)
                    .load(bucketListContentList.get(position).getPhotoUrl())
                    .fit()
                    .placeholder(R.drawable.bucketlist)
                    .error(R.drawable.error)
                    .into(holder.image);
        }

    }

    @Override
    public int getItemCount() {

        if (mContext instanceof MyTrips || mContext instanceof UpcomingTrips)
            return mCityNames.size();
        else
            return bucketListContentList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private ImageView image;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.imageview_widget);
            this.name = itemView.findViewById(R.id.name_widget);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
