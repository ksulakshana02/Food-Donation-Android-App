package com.s22010213.wasteless.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s22010213.wasteless.R;
import com.s22010213.wasteless.adapters.AdapterAd;
import com.s22010213.wasteless.databinding.ActivitySearchBinding;
import com.s22010213.wasteless.models.ModelAd;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private static final String TAG = "SEARCH_ACTIVITY_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SearchView searchName = binding.searchDonationInput;
        searchName.clearFocus();

        binding.toolbarBack.setOnClickListener(v -> {
            onBackPressed();
        });

//        binding.searchDonationBtn.setOnClickListener(v -> {
//            String searchName = binding.searchDonationInput.getText().toString();
//            if (searchName.isEmpty()){
//                binding.searchDonationInput.setError("Please Enter search name...");
//                return;
//            }
//
//            loadAds();
//        });

        loadAds();

        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
        
    }

    ArrayList<ModelAd> adArrayList = new ArrayList<>();

    private void loadAds(){



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.addValueEventListener(new ValueEventListener() {
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
                        AdapterAd adapterAd = new AdapterAd(SearchActivity.this,adArrayList);
                        adapterAd.notifyDataSetChanged();
                        binding.searchAdResult.setAdapter(adapterAd);

//                        String adsCount = ""+ adArrayList.size();
//                        binding.publishedAdsCount.setText(adsCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void searchList(String text){
        ArrayList<ModelAd> searchList = new ArrayList<>();
        for (ModelAd modelAd: adArrayList){
            if (modelAd.getTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(modelAd);
            }
        }
        AdapterAd adapterAd = new AdapterAd(SearchActivity.this,searchList);
        binding.searchAdResult.setAdapter(adapterAd);

    }
}