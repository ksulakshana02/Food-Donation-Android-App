package com.s22010213.wasteless.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
            //ensure the activity is finished so user can't come back to this screen
            finish();
            return;
        }

        //by default show home fragment
        showFragment(new HomeFragment(), "HomeFragment");

        //handle bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.home){
                    showFragment(new HomeFragment(), "HomeFragment");
                    return true;
                } else if (itemId == R.id.donation) {
                    showFragment(new DonationFragment(), "DonationFragment");
                    return true;
                } else if (itemId == R.id.map) {
                    showFragment(new MapFragment(), "MapFragment");
                    return true;
                } else if (itemId == R.id.profile) {
                    showFragment(new ProfileFragment(),"ProfileFragment");
                    return true;
                }else {
                    return false;
                }

            }
        });

    }

    private void showFragment(Fragment fragment, String tag){
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (existingFragment == null || !existingFragment.isVisible()){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(binding.container.getId(),fragment,tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}