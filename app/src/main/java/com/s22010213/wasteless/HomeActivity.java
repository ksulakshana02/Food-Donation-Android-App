package com.s22010213.wasteless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.s22010213.wasteless.fragment.DonationFragment;
import com.s22010213.wasteless.fragment.HomeFragment;
import com.s22010213.wasteless.fragment.MapFragment;
import com.s22010213.wasteless.fragment.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    DonationFragment donationFragment = new DonationFragment();
    MapFragment mapFragment = new MapFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity((new Intent(this, GetStartActivity.class)));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                } else if (itemId == R.id.donation) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, donationFragment).commit();
                    return true;
                } else if (itemId == R.id.map) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
                    return true;
                } else if (itemId == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    return true;
                }
                return true;
            }
        });
    }

//    public void logout(View view) {
//        FirebaseAuth.getInstance().signOut();
//        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//        finish();
//    }
}