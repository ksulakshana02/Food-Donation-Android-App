package com.s22010213.wasteless.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.activities.GetStartActivity;
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

        //firebase auth for auth related task
        firebaseAuth = FirebaseAuth.getInstance();

        //check if user is logged in or not
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity((new Intent(this, GetStartActivity.class)));
        }

        //by default show home fragment
        showHomeFragment();

        //handle bottom navigation
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

//        Intent intent = new Intent(HomeActivity.this, DonationFragment.class);
//        intent.putExtra("isEditMode", false);
//        startActivity(intent);
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