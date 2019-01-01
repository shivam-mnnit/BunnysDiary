package aman.agrawal.bunnysdiary.AppStart;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import aman.agrawal.bunnysdiary.LoginSignUp.LoginActivity;
import aman.agrawal.bunnysdiary.R;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashScreen = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(splashScreen);
                finish();
            }
        },SPLASH_TIME);

    }
}
