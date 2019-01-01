package aman.agrawal.bunnysdiary.Trips;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import aman.agrawal.bunnysdiary.R;

public class UpcomingTrips extends AppCompatActivity implements StaggeredRecyclerViewAdapter.OnItemClickListener {

    private static final int NUM_COLUMNS = 2;
    private DatabaseReference mDatabaseRef;
    private String currentUser;
    private ArrayList<PlanATripContent> planATripContentList;
    private List<String> mDestinationList;
    private RecyclerView recyclerView;
    private Bitmap compressedBitmap;
    private StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_trips);

        Drawable drawable = getResources().getDrawable(R.drawable.upcomingtrips);
        final Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        byte[] byteArray = stream.toByteArray();
        compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(currentUser).child("UpcomingTrips");

        mDestinationList = new ArrayList<>();
        planATripContentList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("You are going to...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(mDestinationList, UpcomingTrips.this, compressedBitmap);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
        staggeredRecyclerViewAdapter.setOnItemClickListener(UpcomingTrips.this);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDestinationList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    mDestinationList.add(dataSnapshot1.child("destination").getValue().toString());
                    planATripContentList.add(dataSnapshot1.getValue(PlanATripContent.class));
                }

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

        Intent intent = new Intent(UpcomingTrips.this, UpcomingTripsItem.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("UpcomingTripsItem",planATripContentList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onDeleteClick(int position) {

        mDatabaseRef.child(planATripContentList.get(position).getTripName()).removeValue();

        mDestinationList.remove(position);
        recyclerView.removeViewAt(position);
        staggeredRecyclerViewAdapter.notifyItemRemoved(position);
        staggeredRecyclerViewAdapter.notifyItemRangeChanged(position, mDestinationList.size());

        Toast.makeText(UpcomingTrips.this,"Data Deleted!",Toast.LENGTH_SHORT).show();

    }
}
