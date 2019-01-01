package aman.agrawal.bunnysdiary.Trips;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import aman.agrawal.bunnysdiary.R;

public class AddExperience extends AppCompatActivity {

    private static final int CHOOSE_PHOTOS = 100;
    private Button btnAddPhotos, btnAddExpenses, btnAddExperience, btnAdd;
    private EditText etAddCity, etAddDateFrom, etAddDateTo, etAddExpenses, etAddExperience;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener dateFrom, dateTo;
    private List<Uri> userSelectedImages;
    private String cityName, tripDateFrom, tripDateTo, expenses, experience,currentUserId;
    private DatabaseReference mDatabaseRef,cityDatabaseRef,cityDataDatabaseRef,cityPhotosDatabaseRef;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_experience);

        btnAddPhotos = (Button) findViewById(R.id.btn_addPhotos);
        btnAddExpenses = (Button) findViewById(R.id.btn_addExpenses);
        btnAddExperience = (Button) findViewById(R.id.btn_addExperience);
        btnAdd = (Button) findViewById(R.id.btn_add);
        etAddCity = (EditText) findViewById(R.id.et_addCity);
        etAddDateFrom = (EditText) findViewById(R.id.et_addDateFrom);
        etAddDateTo = (EditText) findViewById(R.id.et_addDateTo);
        etAddExpenses = (EditText) findViewById(R.id.et_addExpenses);
        etAddExperience = (EditText) findViewById(R.id.et_addExperience);

        etAddExpenses.setVisibility(View.GONE);
        etAddExperience.setVisibility(View.GONE);

        userSelectedImages = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(currentUserId).child("MyTrips");
        mStorageRef = FirebaseStorage.getInstance().getReference(currentUserId);

        myCalendar = Calendar.getInstance();
        dateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateEditText("From");
            }
        };
        dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateEditText("To");
            }
        };


        etAddDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddDateFrom.setError(null);
                new DatePickerDialog(AddExperience.this, dateFrom, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etAddDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddDateTo.setError(null);
                new DatePickerDialog(AddExperience.this, dateTo, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent choosePic = new Intent();
                choosePic.setType("image/*");
                choosePic.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                choosePic.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(choosePic, "Select Your Journey Images"), CHOOSE_PHOTOS);
            }
        });

        btnAddExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAddExpenses.getVisibility() == View.GONE)
                    etAddExpenses.setVisibility(View.VISIBLE);
                else
                    etAddExpenses.setVisibility(View.GONE);
            }
        });

        btnAddExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAddExperience.getVisibility() == View.GONE)
                    etAddExperience.setVisibility(View.VISIBLE);
                else
                    etAddExperience.setVisibility(View.GONE);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityName = etAddCity.getText().toString().trim();
                tripDateFrom = etAddDateFrom.getText().toString();
                tripDateTo = etAddDateTo.getText().toString();

                if (TextUtils.isEmpty(cityName)) {
                    etAddCity.setError("City Name Can't be Empty");
                    return;
                }
                if (TextUtils.isEmpty(tripDateFrom)) {
                    etAddDateFrom.setError("Set Date");
                    return;
                }
                if (TextUtils.isEmpty(tripDateTo)) {
                    etAddDateTo.setError("Set Date");
                    return;
                }

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddExperience.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadExperience();
                }

            }
        });

    }

    private void uploadExperience(){

        StorageReference cityStorageRef = mStorageRef.child(cityName+"/");
        cityDatabaseRef = mDatabaseRef.child(cityName);
        cityPhotosDatabaseRef = cityDatabaseRef.child("photos");

        if(!userSelectedImages.isEmpty()) {

            for (int i=0;i<userSelectedImages.size();i++)
            {
                StorageReference photoStorageRef = cityStorageRef.child(System.currentTimeMillis()+ "." +
                        getExtension(userSelectedImages.get(i)));

                mUploadTask = photoStorageRef.putFile(userSelectedImages.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String url = taskSnapshot.getDownloadUrl().toString();
                                String child = cityPhotosDatabaseRef.push().getKey();
                                cityPhotosDatabaseRef.child(child).setValue(url);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddExperience.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        });
            }
        }

        updateDatabase();
    }

    private void updateDatabase() {

        expenses = etAddExpenses.getText().toString().trim();
        experience = etAddExperience.getText().toString().trim();

        UploadExperience uploadExperience = new UploadExperience(cityName,expenses,experience,tripDateFrom,tripDateTo);
        cityDataDatabaseRef = cityDatabaseRef.child("data");
        cityDataDatabaseRef.setValue(uploadExperience);

        Snackbar.make(findViewById(R.id.lineralayout),"New Experience Added!",Snackbar.LENGTH_LONG).show();

    }

    private String  getExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void updateDateEditText(String dateField) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if (dateField.equals("From"))
            etAddDateFrom.setText(sdf.format(myCalendar.getTime()));
        else
            etAddDateTo.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PHOTOS && resultCode == RESULT_OK && data != null) {

            userSelectedImages.clear();

            if (data.getData() != null) {
                Uri uri = data.getData();
                userSelectedImages.add(uri);

            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        userSelectedImages.add(uri);
                    }
                }
                else{
                    Snackbar.make(findViewById(R.id.lineralayout),"No item Selected!",Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

}
