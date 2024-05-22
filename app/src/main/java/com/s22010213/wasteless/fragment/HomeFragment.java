package com.s22010213.wasteless.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.adapters.AdapterAd;
import com.s22010213.wasteless.models.ModelAd;
import com.s22010213.wasteless.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME_TAG";
    private static final int MAX_DISTANCE_TO_LOAD_ADS_KM = 10;
    private FragmentHomeBinding binding;
    private Context mContext;
    private ArrayList<ModelAd> adArrayList;
    private AdapterAd adapterAd;
//    private SharedPreferences locationSp;
//
//    private double currentLatitude = 0.0;
//    private double currentLongitude = 0.0;
//    private String currentAddress = "";

    @Override
    public void onAttach(@NonNull Context context){
        mContext = context;
        super.onAttach(context);
    }

    public HomeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(mContext), container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

//        locationSp = mContext.getSharedPreferences("LOCATION_SP", Context.MODE_PRIVATE);
//        currentLatitude = locationSp.getFloat("CURRENT_LATITUDE", 0.0f);
//        currentLongitude = locationSp.getFloat("CURRENT_LONGITUDE", 0.0f);
//        currentAddress = locationSp.getString("CURRENT_ADDRESS", "");

//        if (currentLatitude != 0.0 && currentLongitude != 0.0){
//            bin
//        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DonationFragment.class));
            }
        });

        loadAds();
    }

    private void loadAds(){

        adArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelAd modelAd = ds.getValue(ModelAd.class);

                    adArrayList.add(modelAd);

//                    double distance = calculateDistanceKm(modelAd.getLatitude(), modelAd.getLongitude());
//
//                    if (distance <= MAX_DISTANCE_TO_LOAD_ADS_KM){
//                        adArrayList.add(modelAd);
//                    }
                }
                adapterAd = new AdapterAd(mContext, adArrayList);
                binding.recyclerviewAds.setAdapter(adapterAd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private double calculateDistanceKm(double adLatitude, double adLongitude) {
//        Log.d(TAG, "CalculateDistanceKm: currentLatitude: ");
//
//        Location startPoint = new Location(LocationManager.NETWORK_PROVIDER);
//        startPoint.setLatitude(currentLatitude);
//        startPoint.setLongitude(currentLongitude);
//
//        Location endPoint = new Location(LocationManager.NETWORK_PROVIDER);
//        endPoint.setLatitude(adLatitude);
//        endPoint.setLongitude(adLongitude);
//
//        double distanceInMeters = startPoint.distanceTo(endPoint);
//        double distanceInKm = distanceInMeters / 1000;
//
//        return distanceInKm;
//    }

}