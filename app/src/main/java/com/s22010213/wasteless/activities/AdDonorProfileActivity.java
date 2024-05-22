package com.s22010213.wasteless.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.Utils;
import com.s22010213.wasteless.adapters.AdapterAd;
import com.s22010213.wasteless.databinding.ActivityAdDonorProfileBinding;
import com.s22010213.wasteless.models.ModelAd;

import java.util.ArrayList;

public class AdDonorProfileActivity extends AppCompatActivity {

    private ActivityAdDonorProfileBinding binding;
    private static final String TAG = "AD_SELLER_PROFILE_TAG";
    private String donorUid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdDonorProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        donorUid = getIntent().getStringExtra("donorUid");
        Log.d(TAG,"onCreate: sellerUid: "+ donorUid);

        loadDonorDetails();
        loadAds();

        binding.toolBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void loadDonorDetails(){
        Log.d(TAG, "loadDonorDetails: ");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(donorUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String name = ""+ snapshot.child("name").getValue();
                        String profileImage = ""+ snapshot.child("profileImageUrl").getValue();
                        long timestamp = (Long) snapshot.child("timestamp").getValue();
                        //format date time
                        String formattedDate = Utils.formatTimestampDate(timestamp);

                        //set data
                        binding.donorNameTv.setText(name);
                        binding.donorMemberSinceTv.setText(formattedDate);

                        try {
                            Glide.with(AdDonorProfileActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_profile)
                                    .into(binding.donorProfileIv);
                        }catch (Exception e){
                            Log.e(TAG,"onDataChange: ",e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAds(){

        ArrayList<ModelAd> adArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.orderByChild("uid").equalTo(donorUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adArrayList.clear();

                        for (DataSnapshot ds: snapshot.getChildren()){

                            try {
                                //prepare ModelAd with all data from firebase db
                                ModelAd modelAd = ds.getValue(ModelAd.class);
                                //add the prepared modelAd to list
                                adArrayList.add(modelAd);
                            }catch (Exception e){
                                Log.e(TAG,"onDataChange: ",e);
                            }
                        }
                        //init adapterAd and set to rv
                        AdapterAd adapterAd = new AdapterAd(AdDonorProfileActivity.this,adArrayList);
                        binding.adsRv.setAdapter(adapterAd);

                        String adsCount = ""+ adArrayList.size();
                        binding.publishedAdsCount.setText(adsCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}