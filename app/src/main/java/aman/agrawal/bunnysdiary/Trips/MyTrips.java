package aman.agrawal.bunnysdiary.Trips;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import aman.agrawal.bunnysdiary.R;

public class MyTrips extends AppCompatActivity implements StaggeredRecyclerViewAdapter.OnItemClickListener{

    private static final int NUM_COLUMNS = 2;
    private DatabaseReference mDatabaseRef;
    private String currentUser;
    private List<String> mCityNames;
    private RecyclerView recyclerView;
    private Bitmap compressedBitmap;
    private StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        Drawable drawable = getResources().getDrawable(R.drawable.mytrips);
        final Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        byte[] byteArray = stream.toByteArray();
        compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addExperience);
        fab.setImageResource(R.drawable.ic_add_box_black_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newExperience = new Intent(MyTrips.this,AddExperience.class);
                startActivity(newExperience);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(currentUser).child("MyTrips");

        mCityNames = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading your wonderful memories...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(mCityNames, MyTrips.this, compressedBitmap);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
        staggeredRecyclerViewAdapter.setOnItemClickListener(MyTrips.this);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCityNames.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    mCityNames.add(dataSnapshot1.getKey());

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

        Intent intent = new Intent(MyTrips.this, MyTripsItem.class);
        intent.putExtra("cityName", mCityNames.get(position));
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final int position) {

        mDatabaseRef.child(mCityNames.get(position)).removeValue();

        mCityNames.remove(position);
        recyclerView.removeViewAt(position);
        staggeredRecyclerViewAdapter.notifyItemRemoved(position);
        staggeredRecyclerViewAdapter.notifyItemRangeChanged(position, mCityNames.size());

        Toast.makeText(MyTrips.this,"Data Deleted!",Toast.LENGTH_SHORT).show();
    }

}
