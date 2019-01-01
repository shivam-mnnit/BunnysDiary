package aman.agrawal.bunnysdiary.Trips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import aman.agrawal.bunnysdiary.CityData.CityActivity;
import aman.agrawal.bunnysdiary.R;

public class UpcomingTripsItem extends AppCompatActivity {

    private TextView tvTripName,tvDestination,tvStartDate,tvNoOfDays,tvBudget,tvFriends;
    private Button btnPdf,btnShare,btnVisitDestination;

    private PlanATripContent upcomingTripsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_trips_item);

        tvTripName = (TextView)findViewById(R.id.tv_tripName);
        tvDestination = (TextView)findViewById(R.id.tv_destination);
        tvStartDate = (TextView)findViewById(R.id.tv_startDate);
        tvNoOfDays = (TextView)findViewById(R.id.tv_noOfDays);
        tvBudget = (TextView)findViewById(R.id.tv_budget);
        tvFriends = (TextView)findViewById(R.id.tv_friends);
        btnPdf = (Button)findViewById(R.id.btn_pdf);
        btnShare = (Button)findViewById(R.id.btn_share);
        btnVisitDestination = (Button)findViewById(R.id.btn_visitDestination);

        Bundle bundle = getIntent().getExtras();
        upcomingTripsItem = bundle.getParcelable("UpcomingTripsItem");

        tvTripName.setText(upcomingTripsItem.getTripName().trim());
        tvDestination.setText(upcomingTripsItem.getDestination().trim());
        tvStartDate.setText(upcomingTripsItem.getStartDate());

        if(upcomingTripsItem.getNoOfDays()!="")
            tvNoOfDays.setText(upcomingTripsItem.getNoOfDays());
        else
            tvNoOfDays.setText("-");

        if(upcomingTripsItem.getBudget()!="")
            tvBudget.setText(upcomingTripsItem.getBudget());
        else
            tvBudget.setText("-");

        if(upcomingTripsItem.getFriends()!=null){
            for (int i=0;i<upcomingTripsItem.getFriends().size();i++)
                tvFriends.setText(tvFriends.getText()+"\n"+upcomingTripsItem.getFriends().get(i).trim());
        }
        else
            tvFriends.setText("No Entry!");

        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upcomingTripsItem.getPdfUrl()==null)
                    Toast.makeText(UpcomingTripsItem.this,"No PDF Attached",Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(upcomingTripsItem.getPdfUrl()));
                    startActivity(intent);
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Hey, I am Planning a trip to "+upcomingTripsItem.getDestination().toUpperCase()+" on "
                                    + upcomingTripsItem.getStartDate()+" for "+upcomingTripsItem.getNoOfDays()+" days.\nWanna Join Me?";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "How to Invite Friends?"));
            }
        });

        btnVisitDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpcomingTripsItem.this,CityActivity.class);
                intent.putExtra("cityToSearch",upcomingTripsItem.getDestination());
                startActivity(intent);
            }
        });
    }
}
