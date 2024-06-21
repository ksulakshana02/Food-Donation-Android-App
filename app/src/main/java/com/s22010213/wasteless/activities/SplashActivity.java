package com.s22010213.wasteless.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.models.ModelAd;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null){

//            String adId = getIntent().getExtras().getString("adId");
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//            ref.child(adId).get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()){
//
//                }
//            });



        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIMER);

        }


    }
}