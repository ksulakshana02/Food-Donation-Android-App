package com.s22010213.wasteless.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.activities.AdDetailsActivity;
import com.s22010213.wasteless.databinding.FragmentMapBinding;
import com.s22010213.wasteless.models.ModelAd;

import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private Context mContext;
    private FragmentMapBinding binding;
    private GoogleMap mMap;
    private Location userLocation;
    private String adId = "";
    private static final String TAG = "MAP_FRAGMENT_TAG";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Map<Marker,String> markerAdIdMap = new HashMap<>();
    private SupportMapFragment mapFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nearbyMap);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nearbyMap, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);

    }


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        // Set the map's camera position to focus on Sri Lanka
        LatLng sriLanka = new LatLng(7.8731, 80.7718);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 10));

        // Fetch and display donation locations
        fetchDonationLocations();

        // Enable user location if permission granted
        enableUserLocation();


        // Set a marker click listener to show detailed information
        mMap.setOnMarkerClickListener(marker ->  {
                String adId = markerAdIdMap.get(marker);
                if (adId != null){
                    Log.d(TAG,"Marker clicked: adId: "+ adId);
                    Intent intent = new Intent(getActivity(), AdDetailsActivity.class);
                    intent.putExtra("adId",adId);
                    startActivity(intent);
                }else {
                    Log.e(TAG, "Marker tag is null");
                    Utils.toast(getActivity(),"can not found Id");
                }
                return true;
        });
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(location -> userLocation = location);
        }
    }

    private void fetchDonationLocations() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mMap != null) {
                    mMap.clear();
                    markerAdIdMap.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelAd modelAd = ds.getValue(ModelAd.class);
                        if (modelAd != null) {
                            LatLng donationLocation = new LatLng(modelAd.getLatitude(), modelAd.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(donationLocation)
                                    .title(modelAd.getTitle())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            if (marker != null) {
                                markerAdIdMap.put(marker, modelAd.getId());
                                Log.d(TAG, "adId: " + modelAd.getId());
                            }


                        }else {
                            Log.e(TAG,"Ad data is null");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e(TAG, "Failed to load donor details", error.toException());
            }
        });
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if (mMap != null){
            mMap.clear();
        }
        if (mapFragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
    }
}