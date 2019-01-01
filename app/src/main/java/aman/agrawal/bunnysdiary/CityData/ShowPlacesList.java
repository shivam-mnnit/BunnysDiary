package aman.agrawal.bunnysdiary.CityData;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import aman.agrawal.bunnysdiary.R;

public class ShowPlacesList extends AppCompatActivity {

    List<PlacesData> placesList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_places_list);

        recyclerView = (RecyclerView) findViewById(R.id.rv_placeList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placesList = new ArrayList<>();
        placesList = (List<PlacesData>) getIntent().getSerializableExtra("placesList");

        PlacesListAdapter adapter = new PlacesListAdapter(this, placesList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }
}
