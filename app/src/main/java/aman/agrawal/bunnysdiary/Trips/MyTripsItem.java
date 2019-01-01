package aman.agrawal.bunnysdiary.Trips;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import aman.agrawal.bunnysdiary.R;

public class MyTripsItem extends AppCompatActivity {

    private TextView tvCityName,tvStartDate,tvEndDate,tvExpenses,tvExperience;
    private String cityName,currentUser,experience;
    private DatabaseReference mDatabaseDataRef,mDatabasePhotosRef;
    private RecyclerView recyclerView;
    private List<String> myTripsImageItem;
    private MyTripsItemAdapter myTripsItemAdapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips_item);

        tvCityName = (TextView)findViewById(R.id.tv_cityName);
        tvStartDate = (TextView)findViewById(R.id.tv_startDate);
        tvEndDate = (TextView)findViewById(R.id.tv_endDate);
        tvExpenses = (TextView)findViewById(R.id.tv_expenses);
        tvExperience = (TextView)findViewById(R.id.tv_experience);

        myTripsImageItem = new ArrayList<>();

        cityName = getIntent().getStringExtra("cityName");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseDataRef = FirebaseDatabase.getInstance().getReference(currentUser).child("MyTrips").child(cityName).child("data");
        mDatabasePhotosRef = FirebaseDatabase.getInstance().getReference(currentUser).child("MyTrips").child(cityName).child("photos");

        fab = (FloatingActionButton) findViewById(R.id.shareExperience);
        fab.setEnabled(false);
        fab.setImageResource(R.drawable.ic_share_black_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "Here is my Experience of "+cityName+" I wanna share with you!!\n\n"+experience;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UploadExperience uploadExperience = dataSnapshot.getValue(UploadExperience.class);

                tvCityName.setText(uploadExperience.getCityName().toUpperCase().trim());
                tvStartDate.setText(uploadExperience.getTripDateFrom().trim());
                tvEndDate.setText(uploadExperience.getTripDateTo().trim());

                if(uploadExperience.getExpenses().equals(""))
                    tvExpenses.setText("You Never Told us About it!!");
                else
                    tvExpenses.setText(uploadExperience.getExpenses().trim());

                if(uploadExperience.getExperience().equals(""))
                    tvExperience.setText("You Never Shared Your Experience!!");
                else
                {
                    fab.setEnabled(true);
                    experience = uploadExperience.getExperience().trim();
                    tvExperience.setText(experience);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabasePhotosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myTripsImageItem.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    myTripsImageItem.add(postSnapshot.getValue().toString());
                }

                myTripsItemAdapter = new MyTripsItemAdapter(MyTripsItem.this,myTripsImageItem);
                recyclerView.setAdapter(myTripsItemAdapter);
                myTripsItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
