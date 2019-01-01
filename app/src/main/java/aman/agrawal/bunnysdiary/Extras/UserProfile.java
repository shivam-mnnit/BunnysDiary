package aman.agrawal.bunnysdiary.Extras;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import aman.agrawal.bunnysdiary.LoginSignUp.LoginActivity;
import aman.agrawal.bunnysdiary.LoginSignUp.SignUpActivity;
import aman.agrawal.bunnysdiary.R;

public class UserProfile extends AppCompatActivity {

    private static final int CHOOSE_DP = 101;
    private Button btnChangePassword, btnSignOut, btnDeleteAccout;
    private TextView tvUserName,tvUserEmail;
    private ImageView ivUserProfilePic;
    private ImageButton ibtnEditUserName;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private Uri uriDP;
    private String urlDP;
    private String displayName,currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnChangePassword  = (Button)findViewById(R.id.btn_changePassword);
        btnSignOut = (Button)findViewById(R.id.btn_signOut);
        btnDeleteAccout = (Button)findViewById(R.id.btn_deleteAccount);
        ibtnEditUserName = (ImageButton)findViewById(R.id.ibtn_editUserName);
        tvUserName = (TextView)findViewById(R.id.tv_userName);
        tvUserEmail = (TextView)findViewById(R.id.tv_userEmail);
        ivUserProfilePic = (ImageView)findViewById(R.id.iv_userProfilePic);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent goBack = new Intent(UserProfile.this, LoginActivity.class);
                    startActivity(goBack);
                    finish();
                }
            }
        };


        ivUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent choosePic = new Intent();
                choosePic.setType("image/*");
                choosePic.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(choosePic,"Select Profile Image"),CHOOSE_DP);
            }
        });


        if(user.getPhotoUrl()!=null)
        {
            Picasso.with(this).load(user.getPhotoUrl().toString())
                    .placeholder(R.drawable.placeholder).into(ivUserProfilePic);
        }

        String userName = user.getDisplayName();
        String userEmail = user.getEmail();
        if(userName!=null)
            tvUserName.setText(userName);
        else
        {
            int index = userEmail.indexOf('@');
            String displayName = userEmail.substring(0,index);
            tvUserName.setText(displayName);
        }

        ibtnEditUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfile.this);
                alertDialog.setTitle("NEW DISPLAY NAME");
                alertDialog.setCancelable(false);

                final EditText etDisplayName = new EditText(UserProfile.this);
                etDisplayName.setHint("Enter here");

                alertDialog.setView(etDisplayName);
                alertDialog.setPositiveButton("UPDATE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        displayName = etDisplayName.getText().toString().trim();
                        if(!TextUtils.isEmpty(displayName))
                        {
                            tvUserName.setText(displayName);
                            if(displayName!=null){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName).build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(UserProfile.this,"Can't Update Username now!",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(UserProfile.this,"Username updated successfully!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        else
                        {
                            Toast.makeText(UserProfile.this,"Username can't be empty",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                alertDialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        tvUserEmail.setText(userEmail);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfile.this);
                alertDialog.setTitle("CHANGE PASSWORD");
                alertDialog.setCancelable(false);

                LinearLayout layout = new LinearLayout(UserProfile.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText password = new EditText(UserProfile.this);
                password.setHint("Enter Password");
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(password);

                final EditText confirmPassword = new EditText(UserProfile.this);
                confirmPassword.setHint("Confirm Password");
                confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(confirmPassword);

                alertDialog.setView(layout);

                alertDialog.setPositiveButton("CHANGE",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String inputPassword  = password.getText().toString().trim();
                            String inputConfirmPassword  = confirmPassword.getText().toString().trim();

                            if(inputPassword.equals(inputConfirmPassword)==false)
                            {
                                 confirmPassword.setError("Password Mismatch!");
                            }
                            else if(inputPassword!=null)
                            {
                                user.updatePassword(inputPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Password is updated!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to update password!", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }

                        }
                    });

                alertDialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                alertDialog.show();

            }
        });


        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Toast.makeText(getApplicationContext(), "Successfully signed out!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteAccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null){
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserProfile.this, SignUpActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_DP && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            uriDP = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriDP);
                ivUserProfilePic.setImageBitmap(bitmap);

                uploadProfilePic();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfilePic() {

        StorageReference profilePicRef = FirebaseStorage.getInstance()
                                                        .getReference(currentUserId+"/profilepics/"+System.currentTimeMillis()+".jpg");

        if(uriDP!=null)
        {
            profilePicRef.putFile(uriDP).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    urlDP = taskSnapshot.getDownloadUrl().toString();
                    if(urlDP!=null){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(urlDP)).build();

                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(UserProfile.this,"Can't Update Profile Pic now!",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(UserProfile.this,"Profile Pic updated successfully!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfile.this,"Can't update Profile Pic Now!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
