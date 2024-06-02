package com.s22010213.wasteless.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s22010213.wasteless.adapters.AdapterAd;
import com.s22010213.wasteless.databinding.ActivityMyListingsBinding;
import com.s22010213.wasteless.models.ModelAd;

import java.util.ArrayList;

public class MyListingsActivity extends AppCompatActivity {

    private static final String TAG = "MY_ADS_TAG";
    private ActivityMyListingsBinding binding;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelAd> adArrayList;
    private AdapterAd adapterAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyListingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadAds();

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadAds(){
        Log.d(TAG,"loadAds: ");

        adArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adArrayList.clear();

                        for (DataSnapshot ds: snapshot.getChildren()){

                            try {
                                ModelAd modelAd = ds.getValue(ModelAd.class);
                                adArrayList.add(modelAd);

                            }catch (Exception e){
                                Log.e(TAG,"onDataChange: ",e);
                            }
                        }
                        adapterAd = new AdapterAd(MyListingsActivity.this,adArrayList);
                        binding.recyclerviewAds.setAdapter(adapterAd);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}