package aman.agrawal.bunnysdiary.Trips;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import aman.agrawal.bunnysdiary.R;

public class PlanATrip extends AppCompatActivity {

    private static final int CHOOSE_PDF = 100;
    private EditText etTripName,etDestination,etBudget,etStartDate,etNoOfDays,etAddFriends;
    private Button btnAdd,btnAddItinerary,btnDone;
    private TextView tvPdfName;
    private ListView listView;

    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener startDate;

    private Uri pdfUri;
    private String currentUserId,stDate;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_atrip);

        etTripName = (EditText)findViewById(R.id.et_tripName);
        etDestination = (EditText)findViewById(R.id.et_destination);
        etBudget = (EditText)findViewById(R.id.et_budget);
        etStartDate = (EditText)findViewById(R.id.et_startDate);
        etNoOfDays = (EditText)findViewById(R.id.et_noOfDays);
        etAddFriends = (EditText)findViewById(R.id.et_addFriends);
        btnAdd = (Button)findViewById(R.id.btn_add);
        btnAddItinerary = (Button)findViewById(R.id.btn_addItinerary);
        btnDone = (Button)findViewById(R.id.btn_done);
        tvPdfName = (TextView)findViewById(R.id.tv_pdfName);
        listView = (ListView)findViewById(R.id.lv_listView);

        if(getIntent().getIntExtra("Fixed",0)==1);
            etDestination.setText(getIntent().getStringExtra("Destination"));

        myCalendar = Calendar.getInstance();
        startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateEditText();
            }
        };

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etStartDate.setError(null);
                new DatePickerDialog(PlanATrip.this, startDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(etAddFriends.getText().toString().trim())) {
                    listView.setVisibility(View.VISIBLE);
                    listItems.add(etAddFriends.getText().toString().trim());
                    adapter.notifyDataSetChanged();
                    etAddFriends.getText().clear();
                }
            }
        });

        btnAddItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select Itinerary"), CHOOSE_PDF);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(etTripName))
                    etTripName.setError("Please Set some Trip name");
                else if(isEmpty(etDestination))
                    etDestination.setError("What's the Destination");
                else if(isEmpty(etStartDate))
                    etStartDate.setError("Required");
                else if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(PlanATrip.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadUpcomingTrip();
                }
            }
        });

    }

    private void uploadUpcomingTrip() {

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(currentUserId)
                                        .child("UpcomingTrips").child(etTripName.getText().toString().trim());
        mStorageRef = FirebaseStorage.getInstance().getReference(currentUserId).child(System.currentTimeMillis()+".pdf");

        if(pdfUri!=null){
            mUploadTask = mStorageRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String tripName = etTripName.getText().toString().trim();
                    String destination = etDestination.getText().toString().trim();
                    stDate = etStartDate.getText().toString().trim();
                    String budget = etBudget.getText().toString().trim();
                    String noOfDays = etNoOfDays.getText().toString().trim();

                    PlanATripContent planATripContent = new PlanATripContent(tripName,destination,stDate
                            ,taskSnapshot.getDownloadUrl().toString(),budget,noOfDays,listItems);
                    mDatabaseRef.setValue(planATripContent);

                    Toast.makeText(PlanATrip.this,"Trip Planned on "+ stDate +" for " + destination,Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
        else {

            String tripName = etTripName.getText().toString().trim();
            String destination = etDestination.getText().toString().trim();
            stDate = etStartDate.getText().toString().trim();
            String budget = etBudget.getText().toString().trim();
            String noOfDays = etNoOfDays.getText().toString().trim();

            PlanATripContent planATripContent = new PlanATripContent(tripName,destination,stDate
                    ,null,budget,noOfDays,listItems);
            mDatabaseRef.setValue(planATripContent);

            Toast.makeText(PlanATrip.this,"Trip Planned on "+ stDate +" for " + destination,Toast.LENGTH_LONG).show();
        }

        //Notification push working properly with Android Version N, Creating problem for Android O
//        try {
//            notification();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    private boolean isEmpty(EditText et) {
        if(TextUtils.isEmpty(et.getText().toString().trim()))
            return true;
        return false;
    }

    private void updateDateEditText() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etStartDate.setText(sdf.format(myCalendar.getTime()));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PDF && resultCode == RESULT_OK && data!=null && data.getData()!=null ) {

            pdfUri = data.getData();

            String uriString = pdfUri.toString();
            File myFile = new File(uriString);
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(pdfUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            tvPdfName.setVisibility(View.VISIBLE);
            tvPdfName.setText(displayName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void notification() throws ParseException {
//        scheduleNotification(getNotification());
//    }
//
//    private void scheduleNotification(Notification notification) throws ParseException {
//
//        Intent notificationIntent = new Intent(this, AlarmBroadcastReceiver.class);
//        notificationIntent.putExtra(AlarmBroadcastReceiver.NOTIFICATION_ID, 1);
//        notificationIntent.putExtra(AlarmBroadcastReceiver.NOTIFICATION, notification);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent,
//                                                    PendingIntent.FLAG_UPDATE_CURRENT);
//        Calendar c = Calendar.getInstance();
//        String formattedDate = simpleDateFormat.format(c.getTime());
//        Date date2 = simpleDateFormat.parse("27/09/2018 23:10:00");
//        Date date1 = simpleDateFormat.parse(formattedDate);
//        if (date1.compareTo(date2) > 0) {
//            System.out.println("..");
//        } else {
//            long futureInMillis = SystemClock.elapsedRealtime() + date2.getTime() - date1.getTime();
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
//        }
//    }
//
//    private Notification getNotification() {
//        Intent inten = new Intent(this, UpcomingTrips.class);
//        PendingIntent intent = PendingIntent.getActivity(this, 0,inten,0);
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Notification.Builder builder = new Notification.Builder(PlanATrip.this);
//        builder.setContentTitle("Scheduled Notification");
//        builder.setContentText("Trip tomorrow");
//        builder.setSmallIcon(R.drawable.ic_insert_emoticon_black_24dp);
//        builder.setContentIntent(intent);
//        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000,1000 ,1000,1000,1000,1000,1000,1000});
//        builder.setSound(uri);
//        return builder.build();
//    }

}
