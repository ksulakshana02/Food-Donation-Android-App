package com.s22010213.wasteless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.s22010213.wasteless.databinding.ActivityHomeBinding;
import com.s22010213.wasteless.fragment.DonationFragment;
import com.s22010213.wasteless.fragment.HomeFragment;
import com.s22010213.wasteless.fragment.MapFragment;
import com.s22010213.wasteless.fragment.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity((new Intent(this, GetStartActivity.class)));
        }

        showHomeFragment();

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.home){
                    showHomeFragment();
                    return true;
                } else if (itemId == R.id.donation) {
                    showDonationFragment();
                    return true;
                } else if (itemId == R.id.map) {
                    showMapFragment();
                    return true;
                } else if (itemId == R.id.profile) {
                    showProfileFragment();
                    return true;
                }else {
                    return false;
                }

            }
        });

    }

    private void showHomeFragment(){
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.container.getId(),fragment,"HomeFragment");
        fragmentTransaction.commit();
    }

    private void showDonationFragment(){
        DonationFragment fragment = new DonationFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.container.getId(),fragment,"DonationFragment");
        fragmentTransaction.commit();
    }

    private void showMapFragment(){
        MapFragment fragment = new MapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.container.getId(),fragment,"MapFragment");
        fragmentTransaction.commit();
    }

    private void showProfileFragment(){
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.container.getId(),fragment,"ProfileFragment");
        fragmentTransaction.commit();
    }
}