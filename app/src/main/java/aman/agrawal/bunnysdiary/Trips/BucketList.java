package aman.agrawal.bunnysdiary.Trips;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import aman.agrawal.bunnysdiary.CityData.CityActivity;
import aman.agrawal.bunnysdiary.R;

public class BucketList extends AppCompatActivity implements StaggeredRecyclerViewAdapter.OnItemClickListener{

    private static final int NUM_COLUMNS = 2;
    private RecyclerView recyclerView;
    private StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabaseRef;
    private String currentUser;
    private List<BucketListContent> bucketListContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list);

        recyclerView = findViewById(R.id.recyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(currentUser).child("BucketList");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Your Bucket List Contains...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        bucketListContentList = new ArrayList<>();
        staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(bucketListContentList, BucketList.this);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
        staggeredRecyclerViewAdapter.setOnItemClickListener(this);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bucketListContentList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    bucketListContentList.add(dataSnapshot1.getValue(BucketListContent.class));

                staggeredRecyclerViewAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(BucketList.this, CityActivity.class);
        intent.putExtra("cityToSearch", bucketListContentList.get(position).getCityName());
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteClick(int position) {

        mDatabaseRef.child(bucketListContentList.get(position).getCityName()).removeValue();

        bucketListContentList.remove(position);
        recyclerView.removeViewAt(position);
        staggeredRecyclerViewAdapter.notifyItemRemoved(position);
        staggeredRecyclerViewAdapter.notifyItemRangeChanged(position, bucketListContentList.size());

        Toast.makeText(BucketList.this,"Data Deleted!",Toast.LENGTH_SHORT).show();

    }
}
